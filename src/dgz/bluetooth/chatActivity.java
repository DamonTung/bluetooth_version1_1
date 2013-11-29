package dgz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

import org.apache.http.impl.conn.tsccm.WaitingThread;

import dgz.bluetooth.R;
import dgz.bluetooth.Bluetooth.ServerOrCilent;
import android.R.bool;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class chatActivity extends Activity implements OnItemClickListener,
		OnClickListener {
	
	private static Lock lock = new ReentrantLock();
	private static Condition conditionO = lock.newCondition();
	private static Condition conditionK = lock.newCondition();
	
	public int countLinkNum = 0;
	public int countO = 0;
	public int countK = 0;
	/** Called when the activity is first created. */

	private ListView mListView;
	private ArrayList<deviceListItem> list;
	private Button sendButton;
	private Button disconnectButton;
	private EditText editMsgView;
	deviceListAdapter mAdapter;
	Context mContext;
	
	public static boolean isInitialized = false;
	public static boolean isConnected = false;
	public static boolean isLinked=false;
	public static Vector<String> vectorOStrings=new Vector<String>(1);
	public static Vector<String> vectorKStrings=new Vector<String>(1);
	

	/* 一些常量，代表服务器的名称 */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	private BluetoothServerSocket mserverSocket = null;
	private ServerThread startServerThread = null;
	private clientThread clientConnectThread = null;
	public static BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread mreadThread = null;
	
	
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	public MyDataSet myDataSet = (MyDataSet) getApplication();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		mContext = this;
		init();
	}

	private void init() {
		list = new ArrayList<deviceListItem>();
		mAdapter = new deviceListAdapter(this, list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setFastScrollEnabled(true);
		editMsgView = (EditText) findViewById(R.id.MessageText);
		editMsgView.clearFocus();

		sendButton = (Button) findViewById(R.id.btn_msg_send);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msgText = editMsgView.getText().toString();
				if (msgText.length() > 0) {
					sendMessageHandle(msgText);
					editMsgView.setText("");
					editMsgView.clearFocus();
					// close InputMethodManager
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);
				} else
					Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT)
							.show();
			}
		});

		disconnectButton = (Button) findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					shutdownClient();
				} else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {
					shutdownServer();
				}
				Bluetooth.isOpen = false;
				Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
				Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();
			}
		});
		//clientConnectThread = new clientThread();
		//clientConnectThread.start();
	}
	
	
	private  Handler LinkDetectedHandler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			// Toast.makeText(mContext, (String)msg.obj,
			// Toast.LENGTH_SHORT).show();
			if (msg.what == 1) {// --从蓝牙端口接收数据并显示
				list.add(new deviceListItem((String) msg.obj, true));
			} else if (msg.what == 2) {// --向单片机发送数据，主要为建立连接的命令
				String msgString = msg.obj.toString();
				sendMessageHandle(msgString);
			} else if (msg.what == 3) {// --若Tab(2)未初始化，则执行此代码
				Bluetooth.mTabHost.setCurrentTab(2);
			} else if (msg.what == 4) {// --单片机没有响应，即未接受到数据，跳转到Tab(0)
				Toast.makeText(mContext, "==没有数据发送，请尝试重置单片机==",
						Toast.LENGTH_SHORT).show();
				Bluetooth.mTabHost.setCurrentTab(0);
				onDestroy();
			} else if (msg.what == 5) {// --socket连接异常处理
				Toast.makeText(mContext, "==蓝牙socket连接异常，请重试 ===",
						Toast.LENGTH_SHORT).show();
				Bluetooth.mTabHost.setCurrentTab(0);
				onDestroy();
			} else {// --
				list.add(new deviceListItem((String) msg.obj, false));
			}
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
		}

	};

	@Override
	public synchronized void onPause() {
		super.onPause();
		//shutdownClient();
	}

	@Override
	public synchronized void onResume() {// 此处启动客户端或服务端主线程
		super.onResume();
		if (Bluetooth.isOpen) {
			Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
			String address = Bluetooth.BlueToothAddress;
			if (!address.equals("null")) {
				device = mBluetoothAdapter.getRemoteDevice(address);
				if(clientConnectThread == null){
					clientConnectThread = new clientThread();
					clientConnectThread.start();
				}
				Bluetooth.isOpen = true;
			} else {
				Toast.makeText(mContext, "address is null !",
						Toast.LENGTH_SHORT).show();
			}
		} else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {
			startServerThread = new ServerThread();
			startServerThread.start();
			Bluetooth.isOpen = true;
		}
	}
	
	public  void setVectorO(String string){//设置响应命令 #o
		vectorOStrings.add(string);
	}
	public  String getVectorOString(){//获取 #o
		if(vectorOStrings.size()==0){
			
			Log.v("dgz","vectorO 为空。。");
			LinkDetectedHandler.obtainMessage(2, "#h").sendToTarget();
			return "oo";
			
		}
		return vectorOStrings.firstElement();

	}
		
		

	/**
	 * @return vectorKStrings
	 */
	public  String getVectorKStrings() {
		if(vectorKStrings.size()==0){
			
			Log.v("dgz","vectorK 为空。。 ");
			LinkDetectedHandler.obtainMessage(2, "#c").sendToTarget();
			return "kk";
		}
		return vectorKStrings.firstElement();
	}

	/**
	 * @param vectorKStrings 要设置的 vectorKStrings
	 */
	public   void setVectorKStrings(String vectorKStrings) {
		chatActivity.vectorKStrings.add(vectorKStrings);
		
	}
	
	/**  开启客户端
	 * 此处启动与蓝牙的连接建立过程，同时启动监听端口，
	 * 检测与蓝牙、单片机的数据传输
	 * 
	 * */
	
	
	private class clientThread extends Thread { // 客户端主线程，
		public void run() {
			
			try {
				// 创建一个Socket连接：只需要服务器在注册时的UUID号
				// socket =
				// device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				// 连接
				Message msg2 = new Message();
				msg2.obj = "请稍候，正在连接服务器:" + Bluetooth.BlueToothAddress;
				LinkDetectedHandler.sendMessage(msg2);
				
				Log.v("dgz", String.valueOf(socket.isConnected()));
				Log.v("dgz", String.valueOf(socket.getRemoteDevice()));
				
				/** 
				 * 建立socket连接，重复三次
				 * 
				 * */
				socket.connect(); 
				
				while (countLinkNum < 3 && !socket.isConnected()) {
					countLinkNum++;
					sleep(1000);
					socket.connect();
				}
				
				sleep(1000);
				
				if(!socket.isConnected()){
					LinkDetectedHandler.obtainMessage(5).sendToTarget();
				}
				
				Message msg = new Message();
				msg.obj = "已经连接上服务端！可以发送信息。";
				LinkDetectedHandler.sendMessage(msg);
				
								
			
				
				//读线程启动
				mreadThread = new readThread();
				mreadThread.listStrings.clear();
				mreadThread.start();
				
				sleep(3000);
				
				Message msgHelloString = new Message();
				msgHelloString.obj = "呼叫单片机。。发送命令#h";// 呼叫单片机
				LinkDetectedHandler.sendMessage(msgHelloString);
				
				Message msgLinkH = new Message();
				msgLinkH.obj = "#h";
				msgLinkH.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkH);
				
				sleep(3000);
				Log.v("dgz", "#h已发送。。");
								
				Message msgHelloWait = new Message();
				msgHelloWait.obj = "等待单片机响应呼叫命令。。。";
				LinkDetectedHandler.sendMessage(msgHelloWait);
				
				String LinkO=getVectorOString();
				
				while (!LinkO.equals("#o") && countO<3) {
					Log.v("dgz", LinkO);
					countO++;
					sleep(1000);
					LinkO=getVectorOString();
				} 
				
			
				
				if(!LinkO.equals("#o")){
					Message LinkFalse=new Message();
					LinkFalse.obj = "===单片机未响应，请尝试重置单片机====";
					LinkDetectedHandler.sendMessage(LinkFalse);
					
					Log.v("dgz","--#o没有收到--");
					sleep(2000);
					
					LinkDetectedHandler.obtainMessage(4).sendToTarget();;
				}
				
				sleep(3000);
								
				Message msgConnectionMessage = new Message();
				msgConnectionMessage.obj = "呼叫单片机成功，发送建立连接命令#c，";// 与单片机建立连接
				LinkDetectedHandler.sendMessage(msgConnectionMessage);

				
				Message msgLinkC = new Message();
				msgLinkC.obj = "#c";
				msgLinkC.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkC);
				
				sleep(3000);
				
				Log.v("dgz", "#c发送。。");
				Message msgConnection = new Message();
				msgConnection.obj = "等待单片机响应建立连接命令。。。";
				LinkDetectedHandler.sendMessage(msgConnection);
				
				Log.v("dgz", "#k: "+String.valueOf(mreadThread.getLinkString("#k")));
				

				String LinkK=getVectorKStrings();
				
				while(!LinkK.equals("#k")&&countK<3){
					Log.v("dgz", LinkK);
					countK++;
					sleep(1000);
					LinkK=getVectorKStrings();
				}
				
				
				sleep(3000);
				
				if(!LinkK.equals("#k")){
					Message LinkFalseK=new Message();
					LinkFalseK.obj = "===单片机未响应，请尝试重新连接====";
					LinkDetectedHandler.sendMessage(LinkFalseK);
					
					sleep(1000);
					
					LinkDetectedHandler.obtainMessage(4).sendToTarget();;
				}
				
				Message msgRequestDataMessage = new Message();
				msgRequestDataMessage.obj = "建立连接成功，准备接收数据，发送命令#r";
				LinkDetectedHandler.sendMessage(msgRequestDataMessage);
				
				Message msgLinkR = new Message();
				msgLinkR.obj = "#r";
				msgLinkR.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkR);
				
				Log.v("dgz", "isConnected: " + String.valueOf(isConnected));

			} catch (IOException e) {
				
					Log.e("connect", "", e);
					Message msg = new Message();
					msg.obj = "连接服务端异常！断开连接重新试一试。";
					LinkDetectedHandler.sendMessage(msg);
					try {
						sleep(1000);
					} catch (InterruptedException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}					
					LinkDetectedHandler.obtainMessage(5).sendToTarget();
			
			}
			
			catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	};

	// 开启服务器
	private class ServerThread extends Thread {
		public void run() {

			try {
				/*
				 * 创建一个蓝牙服务器 参数分别：服务器名称、UUID
				 */
				mserverSocket = mBluetoothAdapter
						.listenUsingRfcommWithServiceRecord(
								PROTOCOL_SCHEME_RFCOMM,
								UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				Log.d("server", "wait cilent connect...");

				Message msg = new Message();
				msg.obj = "请稍候，正在等待客户端的连接...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);

				/* 接受客户端的连接请求 */
				socket = mserverSocket.accept();
				Log.d("server", "accept success !");

				Message msg2 = new Message();
				String info = "客户端已经连接上！可以发送信息。";
				msg2.obj = info;
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg2);

				// 启动接受数据
				mreadThread = new readThread();
				mreadThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/* 停止服务器 */
	private void shutdownServer() {
		new Thread() {
			public void run() {
				if (startServerThread != null) {
					startServerThread.interrupt();
					startServerThread = null;
				}
				if (mreadThread != null) {
					mreadThread.interrupt();
					mreadThread = null;
				}
				try {
					if (socket != null) {
						socket.close();
						socket = null;
					}
					if (mserverSocket != null) {
						mserverSocket.close();/* 关闭服务器 */
						mserverSocket = null;
					}
				} catch (IOException e) {
					Log.e("server", "mserverSocket.close()", e);
				}
			};
		}.start();
	}

	/* 停止客户端连接 */
	private void shutdownClient() {
		new Thread() {
			public void run() {
				if (clientConnectThread != null) {
					clientConnectThread.interrupt();
					clientConnectThread = null;
				}
				if (mreadThread != null) {
					mreadThread.interrupt();
					mreadThread = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket = null;
				}
			};
		}.start();
	}

	// 发送数据
	
	public void sendMessageHandle(String msg)  {
		Log.v("dgz", String.valueOf(socket));
		try {
			if(socket.isConnected()){
				OutputStream os = socket.getOutputStream();
				os.write(msg.getBytes());
				os.flush();
			}
			else{
				Message msgSocketNull= new Message();
				msgSocketNull.obj="socket 连接不存在。。请重新连接。。";
				LinkDetectedHandler.sendMessage(msgSocketNull);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		list.add(new deviceListItem(msg, false));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size() - 1);
	}

	// 读取数据
	private class readThread extends Thread {
		
		ArrayList<String> listStrings = new ArrayList<String>();

		public boolean getLinkString(String string) {
			return listStrings.contains(string);
		}

		public readThread() {
			// TODO 自动生成的构造函数存根
		}

		
		public void run() {
			InputStream mmInStream = null;
			
			try {
				mmInStream = socket.getInputStream();
				InputStreamReader input = new InputStreamReader(mmInStream);
				BufferedReader reader = new BufferedReader(input,8192);

				String s;
				while ((s = reader.readLine()) != null) {
					isLinked=true;
					isConnected = true;
					if (s.indexOf("@s") == -1) {
						
						if(s.equals("#o")){
							setVectorO(s);
							
						}else if (s.equals("#k")){
							setVectorKStrings(s);
							
						}
						
						listStrings.add(s);

						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);

					} else {
						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);

						if (!isInitialized) {
							
							Message msgChangeTab = new Message();
							msgChangeTab.obj = "2";
							msgChangeTab.what = 3;
							LinkDetectedHandler.sendMessage(msgChangeTab);
							
							sleep(1500);
							
							dataViewActivity.Two.obtainMessage(2, s)
							.sendToTarget();
							
							
						} else {
							
							sleep(1000);
							
							dataViewActivity.Two.obtainMessage(2, s)
									.sendToTarget();
						}

					}
					
					
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
			shutdownClient();
		} else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {
			shutdownServer();
		}
		Bluetooth.isOpen = false;
		Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
	}

	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}

	public class deviceListItem {
		String message;
		boolean isSiri;

		public deviceListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
}