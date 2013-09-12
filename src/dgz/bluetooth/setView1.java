package dgz.bluetooth;

import android.os.Bundle;
import android.widget.TextView;

public class setView1 extends setView {	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setview);
		
		TextView aboutTPMS=(TextView) findViewById(R.id.about);
		Bundle bundle=getIntent().getExtras();
		String str="";
		str += getString(R.string.about) +bundle.getString("t1");
		aboutTPMS.setText(str);
		
	}

}
