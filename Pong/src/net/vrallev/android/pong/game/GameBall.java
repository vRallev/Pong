package net.vrallev.android.pong.game;

import de.greenrobot.event.EventBus;

/**
 * @author Ralf Wondratschek
 */
public class GameBall {

    public static final int WIDTH = GameField.WIDTH / 30;

    /*package*/ float mX;
    /*package*/ float mY;

    private transient float mPlayerLeft;
    private transient float mPlayerRight;

    private int mDegrees = 340;

    /*package*/ GameBall() {
        resetBallPos();
    }

    /*package*/  void setPlayerLeft(float y) {
        mPlayerLeft = y;
    }

    /*package*/ void setPlayerRight(float y) {
        mPlayerRight = y;
    }

    private void resetBallPos() {
        mX = GameField.WIDTH / 2;
        mY = 2 * WIDTH;
    }

    private int getBounceDegree(float ballY, float playerY) {
        playerY = playerY - 2 * WIDTH - WIDTH / 2;
        ballY = ballY - playerY;
        playerY = 5 * WIDTH;
        float factor = ballY / playerY;

        return (int) (factor * 120 + 30);
    }

    /*package*/ void move(double speed) {

        double difX = Math.cos(Math.toRadians(mDegrees)) * speed;
        double difY = Math.sin(Math.toRadians(mDegrees)) * speed;

        mX += difX;
        mY -= difY;

        if (mX < WIDTH + WIDTH / 2) {
            // outside of left
            if (mY >= mPlayerLeft - WIDTH * 2 - WIDTH / 2 && mY <= mPlayerLeft + WIDTH * 2 + WIDTH / 2) {
                // player hit ball
                mX -= difX;
                mDegrees = (180 - getBounceDegree(mY, mPlayerLeft) + 270) % 360;

            } else {
                EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.BALL_OUTSIDE_LEFT));
                resetBallPos();
            }
        } else if (mX > GameField.WIDTH - WIDTH - WIDTH / 2) {
            // outside of right
            if (mY >= mPlayerRight - WIDTH * 2 - WIDTH / 2 && mY <= mPlayerRight + WIDTH * 2 + WIDTH / 2) {
                // player hit ball
                mX -= difX;
                mDegrees = getBounceDegree(mY, mPlayerRight) + 90;

            } else {
                EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.BALL_OUTSIDE_RIGHT));
                resetBallPos();
            }
        }

        // check y edges
        if (mY < WIDTH / 2) {
            mY += difY;
            mDegrees = 360 - mDegrees;
        } else if (mY + WIDTH / 2 >= GameField.HEIGHT) {
            mY += difY;
            mDegrees = 360 - mDegrees;
        }
    }

}
