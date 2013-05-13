package net.vrallev.android.pong.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import net.vrallev.android.base.BaseActivity;
import net.vrallev.android.pong.R;
import net.vrallev.android.pong.game.*;
import net.vrallev.android.pong.view.PlayerTouchListener;
import net.vrallev.android.pong.view.DrawingView;

/**
 * @author Ralf Wondratschek
 */
public class GameActivity extends BaseActivity {

    public static final String GAME_SETUP = "gameSetup";
    private static final String GAME_STATE = "gameState";

    private static final long ANIMATION_DURATION = 1500l;

    private DrawingView mDrawingView;
    private ImageView mPlayView;

    private TextView mTextViewScoreLeft;
    private TextView mTextViewScoreRight;

    private PlayerTouchListener mPlayerTouchListener;

    private GameController mGameController;
    private GameAI mGameAI;
    private GameSetup mGameSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState != null && savedInstanceState.getString(GAME_STATE) != null && savedInstanceState.getString(GAME_SETUP) != null) {
            GameState gameState = GameState.fromJson(savedInstanceState.getString(GAME_STATE));
            mGameSetup = GameSetup.fromJson(savedInstanceState.getString(GAME_SETUP));
            mGameController = new GameController(gameState);
        } else {
            mGameSetup = GameSetup.fromIntent(getIntent());
            mGameController = new GameController(mGameSetup.getGameSpeed());
        }

        mDrawingView = (DrawingView) findViewById(R.id.drawingView);
        mDrawingView.setGameField(mGameController.getGameField());

        mPlayerTouchListener = new PlayerTouchListener(mGameSetup.isSinglePlayer(), mGameController.getGameField());
        EventBus.getDefault().register(mPlayerTouchListener);
        mDrawingView.setOnTouchListener(mPlayerTouchListener);

        mDrawingView.setAlpha(0.0f);
        mDrawingView.setScaleY(0.0f);
        mDrawingView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).scaleY(1.0f).alpha(1.0f);

        mPlayView = (ImageView) findViewById(R.id.imageView_play);
        mPlayView.setOnClickListener(mOnClickListener);
        mPlayView.setScaleX(0.0f);
        mPlayView.setScaleY(0.0f);
        scalePlayButtonIn();

        mTextViewScoreLeft = (TextView) findViewById(R.id.textView_score_left);
        mTextViewScoreRight = (TextView) findViewById(R.id.textView_score_right);
        mTextViewScoreLeft.setText(String.valueOf(mGameController.getPlayerLeftScore()));
        mTextViewScoreRight.setText(String.valueOf(mGameController.getPlayerRightScore()));
        mTextViewScoreLeft.setAlpha(0.0f);
        mTextViewScoreRight.setAlpha(0.0f);
        mTextViewScoreLeft.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).alpha(1.0f);
        mTextViewScoreRight.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).alpha(1.0f);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "visitor2.ttf");
        mTextViewScoreLeft.setTypeface(typeface);
        mTextViewScoreRight.setTypeface(typeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHideSoftKeysRunnable.run();

        if (mGameSetup.isSinglePlayer()) {
            mGameAI = new GameAI(mGameController.getGameField(), mGameSetup.getAiDifficulty());
            mGameAI.start();
            mGameAI.pause(true);
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PAUSE_GAME));
        mDrawingView.getHandler().removeCallbacks(mHideSoftKeysRunnable);

        if (mGameAI != null) {
            mGameAI.stopAI();
            mGameAI = null;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(mPlayerTouchListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(GAME_STATE, new GameState(mGameController).toJson());
        outState.putString(GAME_SETUP, mGameSetup.toJson());
    }

    @Override
    public void onBackPressed() {
        postHideSoftKeys();

        if (mOutAnimationRunning) {
            scalePlayButtonIn();

        } else if (mGameController.isGameRunning()) {
            scalePlayButtonIn();
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PAUSE_GAME));

        } else {
            askForConfirmation();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(GameEvent event) {
        switch (event.getAction()) {
            case CONTINUE_GAME:
                mGameController.setGameRunning(true);
                break;

            case PAUSE_GAME:
                mGameController.setGameRunning(false);
                break;

            case BALL_OUTSIDE_LEFT:
                mGameController.setGameRunning(false);
                mGameController.setPlayerRightScore(1 + mGameController.getPlayerRightScore());
                mGameController.resetGameSpeed();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scalePlayButtonIn();
                        mTextViewScoreRight.setText(String.valueOf(mGameController.getPlayerRightScore()));
                    }
                });
                break;

            case BALL_OUTSIDE_RIGHT:
                mGameController.setGameRunning(false);
                mGameController.setPlayerLeftScore(1 + mGameController.getPlayerLeftScore());
                mGameController.resetGameSpeed();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scalePlayButtonIn();
                        mTextViewScoreLeft.setText(String.valueOf(mGameController.getPlayerLeftScore()));
                    }
                });
                break;

            case GAME_CLOSED:
                mGameController.stopGame();
                finish();
                break;

            case GAME_FINISHED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        askForRestart();
                    }
                });
                break;

        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_play:
                    scalePlayButtonOut();
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

    private void askForRestart() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mGameController.setPlayerLeftScore(0);
                        mTextViewScoreLeft.setText(String.valueOf(mGameController.getPlayerLeftScore()));
                        mGameController.setPlayerRightScore(0);
                        mTextViewScoreRight.setText(String.valueOf(mGameController.getPlayerRightScore()));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.GAME_CLOSED));
                }
            }
        };
        new AlertDialog.Builder(this)
                .setMessage("Game finished. Do you want to restart?")
                .setTitle("Finished")
                .setPositiveButton("Restart", onClickListener)
                .setNegativeButton("Close", onClickListener)
                .show();
    }

    private void scalePlayButtonIn() {
        mPlayView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1.0f).scaleY(1.0f).setDuration(ANIMATION_DURATION);
    }

    private boolean mOutAnimationRunning;

    private void scalePlayButtonOut() {
        mOutAnimationRunning = true;
        mPlayView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0.0f).scaleY(0.0f).setDuration(ANIMATION_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.CONTINUE_GAME));
                mOutAnimationRunning = false;
            }
        });
    }

    private void postHideSoftKeys() {
        mDrawingView.getHandler().removeCallbacks(mHideSoftKeysRunnable);
        mDrawingView.getHandler().postDelayed(mHideSoftKeysRunnable, 3000l);
    }

    private Runnable mHideSoftKeysRunnable = new Runnable() {
        @Override
        public void run() {
            mDrawingView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    };
}
