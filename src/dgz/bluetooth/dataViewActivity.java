package dgz.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class dataViewActivity extends Activity implements OnClickListener {
	
	private ImageView imageView;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;

	private TextView textView1psi;
	private TextView textView2psi;
	private TextView textView3psi;
	private TextView textView4psi;

	private TextView textView1c;
	private TextView textView2c;
	private TextView textView3c;
	private TextView textView4c;

	private TextView textView1v;
	private TextView textView2v;
	private TextView textView3v;
	private TextView textView4v;

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
		
		imageView=(ImageView) findViewById(R.id.ImageView);
		imageView.setOnClickListener(this);

		
		textView1psi = (TextView) findViewById(R.id.textview1psi);
		textView2psi = (TextView) findViewById(R.id.textview2psi);
		textView3psi = (TextView) findViewById(R.id.textview3psi);
		textView4psi = (TextView) findViewById(R.id.textview4psi);

		textView1c = (TextView) findViewById(R.id.textview1c);
		textView2c = (TextView) findViewById(R.id.textview2c);
		textView3c = (TextView) findViewById(R.id.textview3c);
		textView4c = (TextView) findViewById(R.id.textview4c);

		textView1v = (TextView) findViewById(R.id.textview1v);
		textView2v = (TextView) findViewById(R.id.textview2v);
		textView3v = (TextView) findViewById(R.id.textview3v);
		textView4v = (TextView) findViewById(R.id.textview4v);

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

		textView1v.setText(R.string.textview1v);
		textView2v.setText(R.string.textview2v);
		textView3v.setText(R.string.textview3v);
		textView4v.setText(R.string.textview4v);
		
		
		//textView1.setOnClickListener(listener1);
		//textView2.setOnClickListener(listener2);
		//textView3.setOnClickListener(listener3);
		//textView4.setOnClickListener(listener4);
		
		listener1 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent1=new Intent(dataViewActivity.this,setView1.class);
				intent1.putExtra("t1", "左前車輪相關設置");
				startActivity(intent1);
				
			}
		};
		textView1.setOnClickListener(listener1);
		listener2 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2=new Intent(dataViewActivity.this,setView.class);
				intent2.putExtra("t2", "左后車輪相關設置");
				startActivity(intent2);
				
			}
		};
		textView2.setOnClickListener(listener2);
		listener3 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent3=new Intent(dataViewActivity.this,setView.class);
				intent3.putExtra("t3", "右前車輪相關設置");
				startActivity(intent3);
				
			}
		};
		textView3.setOnClickListener(listener3);
		listener4 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent4=new Intent(dataViewActivity.this,setView.class);
				intent4.putExtra("t4", "右後車輪相關設置");
				startActivity(intent4);
				
			}
		};
		textView4v.setText(R.string.textview4v);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(dataViewActivity.this,setView.class);
		startActivity(intent);
		

	}

}
