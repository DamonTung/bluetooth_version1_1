package dgz.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import dgz.bluetooth.chatActivity.deviceListItem;
import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class dataViewActivity extends Activity  implements OnClickListener {

	private ImageView imageView;
	public static TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;

	private static TextView textView1psi;
	private static TextView textView2psi;
	private static TextView textView3psi;
	private static TextView textView4psi;

	private static TextView textView1c;
	private static TextView textView2c;
	private static TextView textView3c;
	private static TextView textView4c;

	
	OnClickListener listener1 = null;
	OnClickListener listener2 = null;
	OnClickListener listener3 = null;
	OnClickListener listener4 = null;
	
		
	Context myContext=this;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataview);
		
		init();
		
	}

	private void init() {
		textView1 = (TextView) findViewById(R.id.textview1);
		textView2 = (TextView) findViewById(R.id.textview2);
		textView3 = (TextView) findViewById(R.id.textview3);
		textView4 = (TextView) findViewById(R.id.textview4);

		imageView = (ImageView) findViewById(R.id.ImageView);
		imageView.setOnClickListener(this);

		textView1psi = (TextView) findViewById(R.id.textview1psi);
		textView2psi = (TextView) findViewById(R.id.textview2psi);
		textView3psi = (TextView) findViewById(R.id.textview3psi);
		textView4psi = (TextView) findViewById(R.id.textview4psi);

		textView1c = (TextView) findViewById(R.id.textview1c);
		textView2c = (TextView) findViewById(R.id.textview2c);
		textView3c = (TextView) findViewById(R.id.textview3c);
		textView4c = (TextView) findViewById(R.id.textview4c);

		
		textView1.setText(R.string.textview1);
		textView2.setText(R.string.textview2);
		textView3.setText(R.string.textview3);
		textView4.setText(R.string.textview4);

		textView1psi.setText(R.string.textview1psi);
		textView2psi.setText(R.string.textview2psi);
		textView3psi.setText(R.string.textview3psi);
		textView4psi.setText(R.string.textview4psi);

		textView1c.setText(R.string.textview1c);
		textView2c.setText(R.string.textview2c);
		textView3c.setText(R.string.textview3c);
		textView4c.setText(R.string.textview4c);

		
		listener1 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent1 = new Intent(dataViewActivity.this,
						setView1.class);
				intent1.putExtra("t1", "左前車輪相關設置");
				startActivity(intent1);

			}
		};
		textView1.setOnClickListener(listener1);
		listener2 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent(dataViewActivity.this,
						setView.class);
				intent2.putExtra("t2", "左后車輪相關設置");
				startActivity(intent2);

			}
		};
		textView2.setOnClickListener(listener2);
		listener3 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent3 = new Intent(dataViewActivity.this,
						setView.class);
				intent3.putExtra("t3", "右前車輪相關設置");
				startActivity(intent3);

			}
		};
		textView3.setOnClickListener(listener3);
		listener4 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent4 = new Intent(dataViewActivity.this,
						setView.class);
				intent4.putExtra("t4", "右後車輪相關設置");
				startActivity(intent4);

			}
		};
		// textView4v.setText(R.string.textview4v);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(dataViewActivity.this, setView.class);
		startActivity(intent);

	}
	
	public static void setValue(String str)
	{
		String string=str;
		String[] value=new String[8];
		int startIndex=string.indexOf("@s");
		
		if(Integer.parseInt(string.substring(startIndex+2, startIndex+3))==0)
			value[0] +="+";
		else {
			value[0] +="-";
		}
		value[0] +=string.substring(startIndex+3, startIndex+5)+"."+string.substring(startIndex+5, startIndex+8);
		textView1psi.setText(value[0]);
		
		if(Integer.parseInt(string.substring(startIndex+7, startIndex+8))==0)
			value[1] +="+";
		else {
			value[1] +="-";
		}
		value[1] +=string.substring(startIndex+8, startIndex+10)+"."+string.substring(startIndex+10, startIndex+13);
		textView1c.setText(value[1]);
		
		if(Integer.parseInt(string.substring(startIndex+12, startIndex+13))==0)
			value[2] +="+";
		else {
			value[2] +="-";
		}
		value[2] +=string.substring(startIndex+13, startIndex+15)+"."+string.substring(startIndex+15, startIndex+18);
		textView2psi.setText(value[2]);
		
		if(Integer.parseInt(string.substring(startIndex+17, startIndex+18))==0)
			value[3] +="+";
		else {
			value[3] +="-";
		}
		value[3] +=string.substring(startIndex+18, startIndex+20)+"."+string.substring(startIndex+20, startIndex+22);
		textView2c.setText(value[3]);
		
		if(Integer.parseInt(string.substring(startIndex+22, startIndex+23))==0)
			value[4] +="+";
		else {
			value[4] +="-";
		}
		value[4] +=string.substring(startIndex+23, startIndex+25)+"."+string.substring(startIndex+25, startIndex+27);
		textView3psi.setText(value[4]);
		
		if(Integer.parseInt(string.substring(startIndex+27, startIndex+28))==0)
			value[5] +="+";
		else {
			value[5] +="-";
		}
		value[5] +=string.substring(startIndex+28, startIndex+30)+"."+string.substring(startIndex+30, startIndex+32);
		textView3c.setText(value[5]);
		
		if(Integer.parseInt(string.substring(startIndex+32, startIndex+33))==0)
			value[6] +="+";
		else {
			value[6] +="-";
		}
		value[6] +=string.substring(startIndex+33, startIndex+35)+"."+string.substring(startIndex+35, startIndex+37);
		textView4psi.setText(value[6]);
		
		if(Integer.parseInt(string.substring(startIndex+37, startIndex+38))==0)
			value[7] +="+";
		else {
			value[7] +="-";
		}
		value[7] +=string.substring(startIndex+38, startIndex+40)+"."+string.substring(startIndex+40, startIndex+42);
		textView4c.setText(value[7]);
		
	}
	

	
	
	public  static Handler Two = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			try {
				if (msg.what == 2) {
					String objString = msg.obj.toString();
					
					try {
						setValue(objString);
					
						/*if (objString.length() >= 7) {
							int indexLFQ=objString.indexOf("LFQ");
							int indexLFT=objString.indexOf("LFT");
							int indexLBQ=objString.indexOf("LBQ");
							int indexLBT=objString.indexOf("LBT");
							int indexRFQ=objString.indexOf("RFQ");
							int indexRFT=objString.indexOf("RFT");
							int indexRBQ=objString.indexOf("RBQ");
							int indexRBT=objString.indexOf("RBT");
							if (indexLFQ>0) {
								int startIndex=indexLFQ+7;
								Log.v("dgz","indexLFQ="+Integer.toString(startIndex));
								
								textView2psi.setText(objString.substring(startIndex, startIndex+1)
										+ "." + objString.substring(startIndex+1, startIndex+2));
								
								Log.v("dgz",objString.substring(startIndex, ++startIndex)
										+ "." + objString.substring(startIndex, startIndex+1));
							}
							if(indexLFT>0){
								int startIndex=indexLFT+7;
								Log.v("dgz","indexLFT="+Integer.toString(startIndex));
								textView2c.setText(objString.substring(startIndex, startIndex+2));
							}
							if (indexLBQ>0){
								int startIndex=indexLBQ+7;
								textView1psi.setText(objString.substring(startIndex, ++startIndex)
										+ "." + objString.substring(startIndex,startIndex+2));
							}
							if(indexLBT>0){
								int startIndex=indexLBT+7;
								textView1c.setText(objString.substring(startIndex, startIndex+2));
							} 
							if (indexRFQ>0) {
								int startIndex=indexRFQ+7;
								textView4psi.setText(objString.substring(startIndex, ++startIndex)
										+ "." + objString.substring(startIndex, ++startIndex));
							}
							if(indexRFT>0){
								int startIndex=indexRFT+7;
								textView4c.setText(objString.substring(startIndex, startIndex+2));
							} 
							if (indexRBQ>0) {
								int startIndex=indexRBQ+7;
								textView3psi.setText(objString.substring(startIndex,++startIndex)
										+ "." + objString.substring(startIndex, ++startIndex));
							}
							if(indexRBT>0){
								int startIndex=indexRBT+7;
								textView3c.setText(objString.substring(startIndex, startIndex+2));
							} else {
								Log.v("dgz", "数据读取有误。。");
								// startActivity();
								//break;
							}
							
							
						}*/
					} catch (Exception e) {
						// TODO: handle exception
					}
					Log.v("dgz",objString);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.v("dgz", "。。读取字符异常。。。");
				e.printStackTrace();
			}
		}
		
		
		
	};
	
	// add start
	public static class MyHandler extends Handler {

		public MyHandler() {
			super();
			// TODO Auto-generated constructor stub
		}

		public MyHandler(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {

		}

		public static void handleMessage2(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);


		}

	}

	/*private class readThread extends Thread {
		
		public readThread() {
			// TODO 自动生成的构造函数存根
		}
		
		//@SuppressWarnings("unused")
		
		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;

			try {
				mmInStream = chatActivity.socket.getInputStream();
				Reader input = new InputStreamReader(mmInStream);
				BufferedReader reader = new BufferedReader(input);
				String s;
				while ((s = reader.readLine()) !=null) {
					
					Toast.makeText(myContext, s, Toast.LENGTH_SHORT).show();
					Message msg = new Message();
					msg.obj = s;
					msg.what = 2;
					MyHandler.handleMessage2(msg);
					
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
*/
	
	/*private class UpdateThread extends Thread { public void run() {
		  onRestart();
	  
	 }
	  
	}*/
	 
	// end
}
