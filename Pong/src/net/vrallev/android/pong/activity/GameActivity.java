package net.vrallev.android.pong.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import net.vrallev.android.base.BaseActivity;
import net.vrallev.android.pong.R;
import net.vrallev.android.pong.game.GameEvent;
import net.vrallev.android.pong.game.GameController;
import net.vrallev.android.pong.game.GameState;
import net.vrallev.android.pong.game.GameTouchListener;
import net.vrallev.android.pong.view.DrawingView;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public class GameActivity extends BaseActivity {

    private static final String GAME_STATE = "gameState";

	private DrawingView mDrawingView;
    private ImageView mPlayView;

    private TextView mTextViewScoreLeft;
    private TextView mTextViewScoreRight;

    private GameController mGameController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

        if (savedInstanceState != null && savedInstanceState.getString(GAME_STATE) != null) {
            GameState gameState = GameState.fromJson(savedInstanceState.getString(GAME_STATE));
            mGameController = new GameController(gameState);
        } else {
            mGameController = new GameController();
        }

		mDrawingView = (DrawingView) findViewById(R.id.drawingView);
		mDrawingView.setGameField(mGameController.getGameField());
        mDrawingView.setOnTouchListener(new GameTouchListener(false, mGameController.getGameField()));

        mPlayView = (ImageView) findViewById(R.id.imageView_play);
        mPlayView.setOnClickListener(mOnClickListener);

        mTextViewScoreLeft = (TextView) findViewById(R.id.textView_score_left);
        mTextViewScoreRight = (TextView) findViewById(R.id.textView_score_right);
        mTextViewScoreLeft.setText(String.valueOf(mGameController.getPlayerLeftScore()));
        mTextViewScoreRight.setText(String.valueOf(mGameController.getPlayerRightScore()));

        // TODO: scale animation for button
	}

	@Override
	protected void onResume() {
		super.onResume();
		findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

    @Override
    protected void onPause() {
        EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PAUSE_GAME));
        super.onPause();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();

        EventBus.getDefault().unregister(this);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(GAME_STATE, new GameState(mGameController).toJson());
    }

    @Override
    public void onBackPressed() {
        if (mGameController.isGameRunning()) {
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PAUSE_GAME));
        } else {
            askForConfirmation();
        }
    }

    public void onEventMainThread(GameEvent event) {
        switch (event.getAction()) {
            case CONTINUE_GAME:
                mPlayView.setVisibility(View.INVISIBLE);
                mGameController.setGameRunning(true);
                break;

            case PAUSE_GAME:
                mPlayView.setVisibility(View.VISIBLE);
                mGameController.setGameRunning(false);
                break;

            case BALL_OUTSIDE_LEFT:
                mPlayView.setVisibility(View.VISIBLE);
                mGameController.setGameRunning(false);
                mGameController.setPlayerRightScore(1 + mGameController.getPlayerRightScore());
                mTextViewScoreRight.setText(String.valueOf(mGameController.getPlayerRightScore()));

                break;

            case BALL_OUTSIDE_RIGHT:
                mPlayView.setVisibility(View.VISIBLE);
                mGameController.setGameRunning(false);
                mGameController.setPlayerLeftScore(1 + mGameController.getPlayerLeftScore());
                mTextViewScoreLeft.setText(String.valueOf(mGameController.getPlayerLeftScore()));
                break;

            case GAME_CLOSED:
                mGameController.stopGame();
                finish();
                break;

        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_play:
                    EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.CONTINUE_GAME));
                    break;
            }
        }
    };

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.do_you_really_want_to_quit_the_game))
                .setTitle(getString(R.string.confirm))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.GAME_CLOSED));
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
