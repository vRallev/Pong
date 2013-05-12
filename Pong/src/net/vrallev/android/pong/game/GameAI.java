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

    public GameAI(GameField field, int difficulty) {
        mGameField = field;
        mDifficulty = difficulty;
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

            time = System.currentTimeMillis();
            if (time - newOffsetTime > 3000) {
                newOffsetTime = time;
                mOffset = createNewOffset();
            }

            ballY = mGameField.getBallY();
            playerLeftPos = mGameField.getPlayerLeftPos();
            dif = ballY - playerLeftPos;
            dif += mOffset;

            if (dif > 0) {
                mGameField.setPlayerLeftPos((int) (playerLeftPos + Math.min(mDifficulty, Math.abs(dif))));
            } else if (dif < 0) {
                mGameField.setPlayerLeftPos((int) (playerLeftPos - Math.min(mDifficulty, Math.abs(dif))));
            }

            Thread.sleep(6l);
        }
    }

    private float createNewOffset() {
        double max = mGameField.getBallWidth() * 2;
        return (float) (max - (mGameField.getBallWidth() * 4 * Math.random()));
    }
}
