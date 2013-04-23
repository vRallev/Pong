package net.vrallev.android.pong.activity;

import net.vrallev.android.pong.GameField;
import net.vrallev.android.pong.R;
import net.vrallev.android.pong.view.DrawingView;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public class GameActivity extends BaseActivity {

	private DrawingView mDrawingView;
	private GameField mGameField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		mGameField = new GameField();

		mDrawingView = (DrawingView) findViewById(R.id.drawingView);
		mDrawingView.setGameField(mGameField);
		
		// TODO: add pause button
	}

	@Override
	protected void onResume() {
		super.onResume();
		findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
	
	@Override
	protected void onDestroy() {
		mGameField.stopGame();
		super.onDestroy();
	}
}
