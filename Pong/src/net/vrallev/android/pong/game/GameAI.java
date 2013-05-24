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

    public GameAI(GameField field, float difficulty) {
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
        long offsetControlTime = System.currentTimeMillis();
        long moveControlTime = offsetControlTime;

        while (mRunning) {
            if (mPaused) {
                Thread.sleep(6l);
                offsetControlTime = System.currentTimeMillis();
                moveControlTime = offsetControlTime;
                continue;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - offsetControlTime > 3000) {
                offsetControlTime = currentTime;
                mOffset = createNewOffset();
            }

            while (currentTime - moveControlTime > 0) {
                moveControlTime++;
                moveKI();
            }

            Thread.sleep(5l);
        }
    }

    private void moveKI() {
        float scaleY = mGameField.getScaleY();
        float ballY = mGameField.getBallY() / scaleY;
        float playerLeftPos = mGameField.getPlayerLeftPos() / scaleY;
        float dif = ballY - playerLeftPos + mOffset;

        if (dif > 0) {
            playerLeftPos = playerLeftPos + Math.min(mDifficulty, Math.abs(dif));
        } else {
            playerLeftPos = playerLeftPos - Math.min(mDifficulty, Math.abs(dif));
        }
        mGameField.setPlayerLeftPos(playerLeftPos * scaleY);
    }

    private float createNewOffset() {
        float max = mGameField.getBallWidth() * 2;
        return max - (mGameField.getBallWidth() * 4 * (float) Math.random());
    }
}
