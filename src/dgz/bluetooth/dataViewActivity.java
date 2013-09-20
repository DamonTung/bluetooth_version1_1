package dgz.bluetooth;

import dgz.bluetooth.chatActivity.deviceListItem;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class dataViewActivity extends Activity implements OnClickListener {

	private ImageView imageView;
	private static TextView textView1;
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

	// private TextView textView1v;
	// private TextView textView2v;
	// private TextView textView3v;
	// private TextView textView4v;

	OnClickListener listener1 = null;
	OnClickListener listener2 = null;
	OnClickListener listener3 = null;
	OnClickListener listener4 = null;

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

		// textView1v = (TextView) findViewById(R.id.textview1v);
		// textView2v = (TextView) findViewById(R.id.textview2v);
		// textView3v = (TextView) findViewById(R.id.textview3v);
		// textView4v = (TextView) findViewById(R.id.textview4v);

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

		// textView1v.setText(R.string.textview1v);
		// textView2v.setText(R.string.textview2v);
		// textView3v.setText(R.string.textview3v);
		// textView4v.setText(R.string.textview4v);

		// textView1.setOnClickListener(listener1);
		// textView2.setOnClickListener(listener2);
		// textView3.setOnClickListener(listener3);
		// textView4.setOnClickListener(listener4);

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

	// add start
	public static class MyHandler extends Handler {
//		int indexLFQ;
//		int indexLFT;
//		int indexLBQ;
//		int indexLBT;
//		int indexRFQ;
//		int indexRFT;
//		int indexRBQ;
//		int indexRBT;

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
			try {
				// Toast.makeText(mContext, (String)msg.obj,
				// Toast.LENGTH_SHORT).show();
				if (msg.what == 2) {
					String objString = msg.obj.toString();
					try {
						while (objString.length() >= 7) {
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
								textView1psi.setText(objString.substring(startIndex, startIndex)
										+ "." + objString.substring(++startIndex, startIndex));
							}
							if(indexLFT>0){
								int startIndex=indexLFT+7;
								textView1c.setText(objString.substring(startIndex, ++startIndex));
							}
							if (indexLBQ>0){
								int startIndex=indexLBQ+7;
								textView2psi.setText(objString.substring(startIndex, startIndex)
										+ "." + objString.substring(++startIndex,startIndex));
							}
							if(indexLBT>0){
								int startIndex=indexLBT+7;
								textView2c.setText(objString.substring(startIndex, ++startIndex));
							} 
							if (indexRFQ>0) {
								int startIndex=indexRFQ+7;
								textView3psi.setText(objString.substring(startIndex, startIndex)
										+ "." + objString.substring(++startIndex, startIndex));
							}
							if(indexRFT>0){
								int startIndex=indexRFT+7;
								textView3c.setText(objString.substring(startIndex, ++startIndex));
							} 
							if (indexRBQ>0) {
								int startIndex=indexRBQ+7;
								textView4psi.setText(objString.substring(startIndex,startIndex)
										+ "." + objString.substring(++startIndex, startIndex));
							}
							if(indexRBT>0){
								int startIndex=indexRBT+7;
								textView4c.setText(objString.substring(startIndex, ++startIndex));
							} else {
								Log.v("dgz", "数据读取有误。。");
								// startActivity();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.v("dgz", "。。读取字符异常。。。");
				e.printStackTrace();
			}

		}

	}

	// public static Handler DetectedHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// try {
	// // Toast.makeText(mContext, (String)msg.obj,
	// // Toast.LENGTH_SHORT).show();
	// if (msg.what == 2) {
	// String objString = msg.obj.toString();
	// if ((objString.substring(1, 4)).equals("LFQ")) {
	// textView1psi.setText(objString.substring(8, 8) + "."
	// + objString.substring(9, 9));
	// textView1c.setText(objString.substring(18, 19));
	// } else if ((objString.substring(1, 4)).equals("LBQ")) {
	// textView2psi.setText(objString.substring(8, 8) + "."
	// + objString.substring(9, 9));
	// textView2c.setText(objString.substring(18, 19));
	// } else if ((objString.substring(1, 4)).equals("RFQ")) {
	// textView3psi.setText(objString.substring(8, 8) + "."
	// + objString.substring(9, 9));
	// textView3c.setText(objString.substring(18, 19));
	// } else if ((objString.substring(1, 4)).equals("RBQ")){
	// textView4psi.setText(objString.substring(8, 8) + "."
	// + objString.substring(9, 9));
	// textView4c.setText(objString.substring(18, 19));
	// }
	// else{
	// Log.v("dgz","数据读取有误。。");
	// //startActivity();
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// Log.v("dgz","。。读取字符异常。。。");
	// e.printStackTrace();
	// }
	//
	// }
	// };

	/*
	 * private class UpdateThread extends Thread { public void run() {
	 * 
	 * 
	 * };
	 * 
	 * }
	 */
	// end
}
