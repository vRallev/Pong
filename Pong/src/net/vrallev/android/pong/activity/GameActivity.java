package net.vrallev.android.pong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import net.vrallev.android.base.BaseActivity;
import net.vrallev.android.pong.R;
import net.vrallev.android.pong.game.GameEvent;
import net.vrallev.android.pong.game.GameHost;
import net.vrallev.android.pong.game.GameTouchListener;
import net.vrallev.android.pong.view.DrawingView;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public class GameActivity extends BaseActivity {

	private DrawingView mDrawingView;
    private ImageView mPlayView;

    private TextView mTextViewScoreLeft;
    private TextView mTextViewScoreRight;

    private GameHost mGameHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

        mGameHost = new GameHost();

		mDrawingView = (DrawingView) findViewById(R.id.drawingView);
		mDrawingView.setGameField(mGameHost.getGameField());
        mDrawingView.setOnTouchListener(new GameTouchListener(false));

        mPlayView = (ImageView) findViewById(R.id.imageView_play);
        mPlayView.setOnClickListener(mOnClickListener);

        mTextViewScoreLeft = (TextView) findViewById(R.id.textView_score_left);
        mTextViewScoreRight = (TextView) findViewById(R.id.textView_score_right);
        mTextViewScoreLeft.setText(String.valueOf(mGameHost.getPlayerLeftScore()));
        mTextViewScoreRight.setText(String.valueOf(mGameHost.getPlayerRightScore()));

        // TODO: scale animation for button
        // TODO: score
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
    public void onBackPressed() {
        if (mGameHost.isGameRunning()) {
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PAUSE_GAME));
        } else {
            // TODO: confirm
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.GAME_CLOSED));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GameEvent event) {
        switch (event.getAction()) {
            case CONTINUE_GAME:
                mPlayView.setVisibility(View.INVISIBLE);
                mGameHost.setGameRunning(true);
                break;

            case PAUSE_GAME:
                mPlayView.setVisibility(View.VISIBLE);
                mGameHost.setGameRunning(false);
                break;

            case PLAYER_LEFT_MOVED:
                mGameHost.getGameField().setPlayerLeftPos(event.getPositionY());
                break;

            case PLAYER_RIGHT_MOVED:
                mGameHost.getGameField().setPlayerRightPos(event.getPositionY());
                break;

            case BALL_OUTSIDE_LEFT:
                mPlayView.setVisibility(View.VISIBLE);
                mGameHost.setGameRunning(false);
                mGameHost.setPlayerRightScore(1 + mGameHost.getPlayerRightScore());
                mTextViewScoreRight.setText(String.valueOf(mGameHost.getPlayerRightScore()));

                break;

            case BALL_OUTSIDE_RIGHT:
                mPlayView.setVisibility(View.VISIBLE);
                mGameHost.setGameRunning(false);
                mGameHost.setPlayerLeftScore(1 + mGameHost.getPlayerLeftScore());
                mTextViewScoreLeft.setText(String.valueOf(mGameHost.getPlayerLeftScore()));
                break;

            case GAME_CLOSED:
                mGameHost.stopGame();
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
}
