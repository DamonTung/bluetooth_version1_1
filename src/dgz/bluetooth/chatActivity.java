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
	

	/* һЩ��������������������� */
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
					Toast.makeText(mContext, "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT)
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
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					shutdownClient();
				} else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {
					shutdownServer();
				}
				Bluetooth.isOpen = false;
				Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
				Toast.makeText(mContext, "�ѶϿ����ӣ�", Toast.LENGTH_SHORT).show();
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
			if (msg.what == 1) {// --�������˿ڽ������ݲ���ʾ
				list.add(new deviceListItem((String) msg.obj, true));
			} else if (msg.what == 2) {// --��Ƭ���������ݣ���ҪΪ�������ӵ�����
				String msgString = msg.obj.toString();
				sendMessageHandle(msgString);
			} else if (msg.what == 3) {// --��Tab(2)δ��ʼ������ִ�д˴���
				Bluetooth.mTabHost.setCurrentTab(2);
			} else if (msg.what == 4) {// --��Ƭ��û����Ӧ����δ���ܵ����ݣ���ת��Tab(0)
				Toast.makeText(mContext, "==û�����ݷ��ͣ��볢�����õ�Ƭ��==",
						Toast.LENGTH_SHORT).show();
				Bluetooth.mTabHost.setCurrentTab(0);
				onDestroy();
			} else if (msg.what == 5) {// --socket�����쳣����
				Toast.makeText(mContext, "==����socket�����쳣�������� ===",
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
	public synchronized void onResume() {// �˴������ͻ��˻��������߳�
		super.onResume();
		if (Bluetooth.isOpen) {
			Toast.makeText(mContext, "�����Ѿ��򿪣�����ͨ�š����Ҫ�ٽ������ӣ����ȶϿ���",
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
	
	public  void setVectorO(String string){//������Ӧ���� #o
		vectorOStrings.add(string);
	}
	public  String getVectorOString(){//��ȡ #o
		if(vectorOStrings.size()==0){
			
			Log.v("dgz","vectorO Ϊ�ա���");
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
			
			Log.v("dgz","vectorK Ϊ�ա��� ");
			LinkDetectedHandler.obtainMessage(2, "#c").sendToTarget();
			return "kk";
		}
		return vectorKStrings.firstElement();
	}

	/**
	 * @param vectorKStrings Ҫ���õ� vectorKStrings
	 */
	public   void setVectorKStrings(String vectorKStrings) {
		chatActivity.vectorKStrings.add(vectorKStrings);
		
	}
	
	/**  �����ͻ���
	 * �˴����������������ӽ������̣�ͬʱ���������˿ڣ�
	 * �������������Ƭ�������ݴ���
	 * 
	 * */
	
	
	private class clientThread extends Thread { // �ͻ������̣߳�
		public void run() {
			
			try {
				// ����һ��Socket���ӣ�ֻ��Ҫ��������ע��ʱ��UUID��
				// socket =
				// device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				// ����
				Message msg2 = new Message();
				msg2.obj = "���Ժ��������ӷ�����:" + Bluetooth.BlueToothAddress;
				LinkDetectedHandler.sendMessage(msg2);
				
				Log.v("dgz", String.valueOf(socket.isConnected()));
				Log.v("dgz", String.valueOf(socket.getRemoteDevice()));
				
				/** 
				 * ����socket���ӣ��ظ�����
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
				msg.obj = "�Ѿ������Ϸ���ˣ����Է�����Ϣ��";
				LinkDetectedHandler.sendMessage(msg);
				
								
			
				
				//���߳�����
				mreadThread = new readThread();
				mreadThread.listStrings.clear();
				mreadThread.start();
				
				sleep(3000);
				
				Message msgHelloString = new Message();
				msgHelloString.obj = "���е�Ƭ��������������#h";// ���е�Ƭ��
				LinkDetectedHandler.sendMessage(msgHelloString);
				
				Message msgLinkH = new Message();
				msgLinkH.obj = "#h";
				msgLinkH.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkH);
				
				sleep(3000);
				Log.v("dgz", "#h�ѷ��͡���");
								
				Message msgHelloWait = new Message();
				msgHelloWait.obj = "�ȴ���Ƭ����Ӧ�����������";
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
					LinkFalse.obj = "===��Ƭ��δ��Ӧ���볢�����õ�Ƭ��====";
					LinkDetectedHandler.sendMessage(LinkFalse);
					
					Log.v("dgz","--#oû���յ�--");
					sleep(2000);
					
					LinkDetectedHandler.obtainMessage(4).sendToTarget();;
				}
				
				sleep(3000);
								
				Message msgConnectionMessage = new Message();
				msgConnectionMessage.obj = "���е�Ƭ���ɹ������ͽ�����������#c��";// �뵥Ƭ����������
				LinkDetectedHandler.sendMessage(msgConnectionMessage);

				
				Message msgLinkC = new Message();
				msgLinkC.obj = "#c";
				msgLinkC.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkC);
				
				sleep(3000);
				
				Log.v("dgz", "#c���͡���");
				Message msgConnection = new Message();
				msgConnection.obj = "�ȴ���Ƭ����Ӧ���������������";
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
					LinkFalseK.obj = "===��Ƭ��δ��Ӧ���볢����������====";
					LinkDetectedHandler.sendMessage(LinkFalseK);
					
					sleep(1000);
					
					LinkDetectedHandler.obtainMessage(4).sendToTarget();;
				}
				
				Message msgRequestDataMessage = new Message();
				msgRequestDataMessage.obj = "�������ӳɹ���׼���������ݣ���������#r";
				LinkDetectedHandler.sendMessage(msgRequestDataMessage);
				
				Message msgLinkR = new Message();
				msgLinkR.obj = "#r";
				msgLinkR.what = 2;
				LinkDetectedHandler.sendMessage(msgLinkR);
				
				Log.v("dgz", "isConnected: " + String.valueOf(isConnected));

			} catch (IOException e) {
				
					Log.e("connect", "", e);
					Message msg = new Message();
					msg.obj = "���ӷ�����쳣���Ͽ�����������һ�ԡ�";
					LinkDetectedHandler.sendMessage(msg);
					try {
						sleep(1000);
					} catch (InterruptedException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}					
					LinkDetectedHandler.obtainMessage(5).sendToTarget();
			
			}
			
			catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
	};

	// ����������
	private class ServerThread extends Thread {
		public void run() {

			try {
				/*
				 * ����һ������������ �����ֱ𣺷��������ơ�UUID
				 */
				mserverSocket = mBluetoothAdapter
						.listenUsingRfcommWithServiceRecord(
								PROTOCOL_SCHEME_RFCOMM,
								UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				Log.d("server", "wait cilent connect...");

				Message msg = new Message();
				msg.obj = "���Ժ����ڵȴ��ͻ��˵�����...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);

				/* ���ܿͻ��˵��������� */
				socket = mserverSocket.accept();
				Log.d("server", "accept success !");

				Message msg2 = new Message();
				String info = "�ͻ����Ѿ������ϣ����Է�����Ϣ��";
				msg2.obj = info;
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg2);

				// ������������
				mreadThread = new readThread();
				mreadThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/* ֹͣ������ */
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
						mserverSocket.close();/* �رշ����� */
						mserverSocket = null;
					}
				} catch (IOException e) {
					Log.e("server", "mserverSocket.close()", e);
				}
			};
		}.start();
	}

	/* ֹͣ�ͻ������� */
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

	// ��������
	
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
				msgSocketNull.obj="socket ���Ӳ����ڡ������������ӡ���";
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

	// ��ȡ����
	private class readThread extends Thread {
		
		ArrayList<String> listStrings = new ArrayList<String>();

		public boolean getLinkString(String string) {
			return listStrings.contains(string);
		}

		public readThread() {
			// TODO �Զ����ɵĹ��캯�����
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
				// TODO �Զ����ɵ� catch ��
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