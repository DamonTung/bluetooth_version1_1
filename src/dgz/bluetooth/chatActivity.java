package dgz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.io.*;

import dgz.bluetooth.R;
import dgz.bluetooth.Bluetooth.ServerOrCilent;
import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
	/** Called when the activity is first created. */

	private ListView mListView;
	private ArrayList<deviceListItem> list;
	private Button sendButton;
	private Button disconnectButton;
	private EditText editMsgView;
	deviceListAdapter mAdapter;
	Context mContext;
	
	public static boolean isInitialized=false;

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
	private readThread mreadThread = null;;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	public  MyDataSet myDataSet=(MyDataSet)getApplication();

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
					shutdownClient();
				} else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {
					shutdownServer();
				}
				Bluetooth.isOpen = false;
				Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
				Toast.makeText(mContext, "�ѶϿ����ӣ�", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private Handler LinkDetectedHandler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			// Toast.makeText(mContext, (String)msg.obj,
			// Toast.LENGTH_SHORT).show();
			if (msg.what == 1) {
				list.add(new deviceListItem((String) msg.obj, true));
			} else if(msg.what==8) {
				String msgString=msg.obj.toString();
				if (socket == null) {
					//Toast.makeText(mContext, "û������", Toast.LENGTH_SHORT).show();
					Log.v("dgz","socket �����ѶϿ�����");
					return;
				}
				try {
					OutputStream os = socket.getOutputStream();
					os.write(msgString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.add(new deviceListItem(msgString, false));
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(list.size() - 1);
				
			}else if(msg.what==3){
				Bluetooth.mTabHost.setCurrentTab(2);
			}
			else {
				list.add(new deviceListItem((String) msg.obj, false));
			}
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
		}

	};

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public synchronized void onResume() {
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
				clientConnectThread = new clientThread();
				clientConnectThread.start();
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

	class MycountTime extends CountDownTimer {

		public MycountTime(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO �Զ����ɵĹ��캯�����
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO �Զ����ɵķ������

		}

		@Override
		public void onFinish() {
			// TODO �Զ����ɵķ������

		}

	}

	// �����ͻ���
	private class clientThread extends Thread {
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
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);

				socket.connect();

				Message msg = new Message();
				msg.obj = "�Ѿ������Ϸ���ˣ����Է�����Ϣ��";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				
				//Bluetooth.mTabHost.setCurrentTab(2);
				
				Message msgHelloString = new Message();
				msgHelloString.obj = "���е�Ƭ��������������#h";// ���е�Ƭ��
				msgHelloString.what = 0;
				LinkDetectedHandler.sendMessage(msgHelloString);
				
				//sendMessageHandle("#h");
				Message msgHello=new Message();
				msgHello.obj="#h";
				msgHello.what=8;
				LinkDetectedHandler.sendMessage(msgHello);
				
				Message msgLinkC=new Message();
				msgLinkC.obj="#c";
				msgLinkC.what=8;
				
				Message msgLinkR=new Message();
				msgLinkR.obj="#r";
				msgLinkR.what=8;
				
				
				Message msgHelloWait = new Message();
				msgHelloWait.obj = "�ȴ���Ƭ����Ӧ�����������";
				msgHelloWait.what = 0;
				LinkDetectedHandler.sendMessage(msgHelloWait);

				// ������������
				 //Looper.prepare();
				mreadThread = new readThread();
				mreadThread.start();

				try {
					
					String strHelloString = null;
					//mreadThread.hMap.put("#o", "#o");
					int countN = 0;
					while (mreadThread.getLinkString("#o")) {
						//strHelloString = mreadThread.getString("#o");
						for (int i = 0; i < 3; i++) {
							while (countN < 500000) {
								countN++;
							}
							LinkDetectedHandler.sendMessage(msgHelloWait);
						}
					}
					Message msgConnectionMessage = new Message();
					msgConnectionMessage.obj = "���е�Ƭ���ɹ������ͽ�����������#c��";// �뵥Ƭ����������
					msgConnectionMessage.what = 0;
					LinkDetectedHandler.sendMessage(msgConnectionMessage);
					
					//sendMessageHandle("#c");
					LinkDetectedHandler.sendMessage(msgLinkC);
					
					Message msgConnection = new Message();
					msgConnection.obj = "�ȴ���Ƭ����Ӧ���������������";
					msgConnection.what = 0;
					LinkDetectedHandler.sendMessage(msgConnection);
					
					//String strConnectionString = null;
					/*while (!strConnectionString.equalsIgnoreCase(mreadThread.getString("#k"))) {
						strConnectionString = mreadThread.getString("#k");

					}*/
					while(mreadThread.getLinkString("#k")){
						
					}
					Message msgRequestDataMessage = new Message();
					msgRequestDataMessage.obj = "�������ӳɹ���׼���������ݣ���������#r";
					msgRequestDataMessage.what = 0;
					sendMessageHandle(msgRequestDataMessage.obj.toString());
					//sendMessageHandle("#r");
					
					LinkDetectedHandler.sendMessage(msgLinkR);
					Looper.loop();
					
				} catch (Exception e) {
					// TODO: handle exception
					Log.v("dgz", "���ӳ�������");
				}

			} catch (IOException e) {
				Log.e("connect", "", e);
				Message msg = new Message();
				msg.obj = "���ӷ�����쳣���Ͽ�����������һ�ԡ�";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
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
	public synchronized void sendMessageHandle(String msg) {
		if (socket == null) {
			Toast.makeText(mContext, "û������", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
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
		Hashtable<String, String> hMap = new Hashtable<String, String>();
		Iterator<String> iteratorHMap = hMap.keySet().iterator();
		ArrayList<String> listStrings=new ArrayList<String>();
		
		public boolean getLinkString(String string){
			return listStrings.contains(string);
		}

		public readThread() {
			// TODO �Զ����ɵĹ��캯�����
		}

		// @SuppressWarnings("unused")
		public String getString(String str) {

			return hMap.get(str);
		}

		public String getDataString() {
			while (iteratorHMap.hasNext()) {
				return iteratorHMap.next();
			}
			return null;
		}

		public void delElement(String str) {
			if (str == iteratorHMap.next())
				iteratorHMap.remove();
		}

		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;

			try {
				mmInStream = socket.getInputStream();
				InputStreamReader input = new InputStreamReader(mmInStream);
				BufferedReader reader = new BufferedReader(input);

				String s;
				while ((s = reader.readLine()) != null) {

					
					if (s.indexOf("@s") == -1) {
						//hMap.put(s, s);
						listStrings.add(s);
						/*Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);*/

					} else {
						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);
						
						//myDataSet.setDataString(s);
						if(!isInitialized){
							/*Intent intent=new Intent(chatActivity.this,dataViewActivity.class);
							startActivity(intent);*/
							Message msgChangetTab=new Message();
							msgChangetTab.obj="2";
							msgChangetTab.what=3;
							LinkDetectedHandler.sendMessage(msgChangetTab);
							//Bluetooth.mTabHost.setCurrentTab(2);
							//dataViewActivity.Two.obtainMessage(2, s).sendToTarget();
						}else {
							dataViewActivity.Two.obtainMessage(2, s).sendToTarget();
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