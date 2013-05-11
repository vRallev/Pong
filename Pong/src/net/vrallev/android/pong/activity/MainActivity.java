package net.vrallev.android.pong.activity;

import net.vrallev.android.base.BaseActivity;
import net.vrallev.android.pong.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void buttonClick(View v) {
		switch (v.getId()) {
			case R.id.button_local:
				startActivity(new Intent(this, GameActivity.class));
				break;

			case R.id.button_network:
				// TODO: implement
				Toast.makeText(this, "Not implemented, yet.", Toast.LENGTH_SHORT).show();
				break;
		}
	}

}
