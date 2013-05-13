package net.vrallev.android.pong.game;

import de.greenrobot.event.EventBus;
import net.vrallev.android.base.util.L;

/**
 * @author Ralf Wondratschek
 */
public class GameAI extends Thread {

    private GameField mGameField;
    private float mDifficulty;
    private float mOffset;

    private boolean mRunning;
    private boolean mPaused;

    private float mUnscaledDifficulty;

    public GameAI(GameField field, float difficulty) {
        mGameField = field;
        mUnscaledDifficulty = difficulty;
        mRunning = true;
    }

    public void stopAI() {
        mRunning = false;
    }

    public void pause(boolean pause) {
        mPaused = pause;
    }

    @Override
    public void run() {
        EventBus.getDefault().register(this);

        try {
            innerRun();
        } catch (InterruptedException e) {
            L.error(e);
        }

        EventBus.getDefault().unregister(this);
    }

    public void onEvent(GameEvent event) {
        switch (event.getAction()) {
            case CONTINUE_GAME:
                pause(false);
                break;

            case PAUSE_GAME:
            case BALL_OUTSIDE_LEFT:
            case BALL_OUTSIDE_RIGHT:
                pause(true);
                break;
        }
    }

    private void innerRun() throws InterruptedException {
        float ballY;
        float playerLeftPos;
        float dif;

        long newOffsetTime = System.currentTimeMillis();
        long time;

        while (mRunning) {
            if (mPaused) {
                Thread.sleep(6l);
                newOffsetTime = System.currentTimeMillis();
                continue;
            }

            if (mDifficulty == 0.0f) {
                mDifficulty = mUnscaledDifficulty / mGameField.getScaleY();
            }

            time = System.currentTimeMillis();
            if (time - newOffsetTime > 3000) {
                newOffsetTime = time;
                mOffset = createNewOffset();
            }

            ballY = mGameField.getBallY();
            playerLeftPos = mGameField.getPlayerLeftPos();
            dif = ballY - playerLeftPos + mOffset;

            if (dif > 0) {
                playerLeftPos = playerLeftPos + Math.min(mDifficulty, Math.abs(dif));
            } else {
                playerLeftPos = playerLeftPos - Math.min(mDifficulty, Math.abs(dif));
            }
            mGameField.setPlayerLeftPos(playerLeftPos);

            Thread.sleep(6l);
        }
    }

    private float createNewOffset() {
        float max = mGameField.getBallWidth() * 2;
        return max - (mGameField.getBallWidth() * 4 * (float) Math.random());
    }
}
