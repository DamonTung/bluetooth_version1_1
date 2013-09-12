package dgz.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class setView extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setview);
		
		TextView aboutTPMS=(TextView) findViewById(R.id.about);
		aboutTPMS.setText(R.string.about);
		
	}
}
