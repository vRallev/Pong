package net.vrallev.android.pong.game;

import net.vrallev.android.base.util.L;

/**
 * @author Ralf Wondratschek
 */
/* package */ class GameLoop extends Thread {

    private static final L L = new L(GameLoop.class);

    private boolean mPaused = true;
    private boolean mFinished = false;

    private final GameController mGameController;
    private final GameField mGameField;
    private double mGameSpeed;

    /*package*/ GameLoop(GameController controller) {
        mGameController = controller;
        mGameField = controller.getGameField();
        mGameSpeed = controller.getGameSpeed();
    }

    /**
     * Pause or continue the loop.
     *
     * @param running {@code true}, if the loop should continue, {@code false} if the loop should pause.
     */
    public void setRunning(boolean running) {
        mPaused = !running;
    }

    /**
     * @return {@code true}, if the loop is running, {@code false} if the loop is paused.
     */
    public boolean isRunning() {
        return !mPaused;
    }

    /**
     * Stops the loop. To start a new one, you need to create a new instance.
     */
    public void stopLoop() {
        mFinished = true;
    }

    public void setGameSpeed(double speed) {
        mGameSpeed = speed;
    }

    @Override
    public void run() {
        try {
            innerRun();
        } catch (InterruptedException e) {
            L.e(e);
        }
    }

    private void innerRun() throws InterruptedException {
        long speedControlTime = System.currentTimeMillis();
        long moveControlTime = System.currentTimeMillis();

        while (!mFinished) {
            if (mPaused) {
                speedControlTime = System.currentTimeMillis();

                Thread.sleep(10);

                moveControlTime = System.currentTimeMillis();
                continue;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - speedControlTime > 150) {
                speedControlTime = currentTime;
                mGameSpeed += 0.01;
                mGameController.setGameSpeed(mGameSpeed);
            }

            while (currentTime - moveControlTime > 0) {
                moveControlTime++;
                mGameField.moveBall(mGameSpeed);
            }

            Thread.sleep(5);
        }
    }
}
