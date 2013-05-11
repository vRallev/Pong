package net.vrallev.android.pong.game;

import de.greenrobot.event.EventBus;
import net.vrallev.android.base.util.L;

/**
 * This class represents the game field.
 *
 * @author Ralf Wondratschek
 */
public class GameField {

    private static final L L = new L(GameField.class);

    private int mWidth;
    private int mHeight;

    private int mPlayerLeft;
    private int mPlayerRight;

    private Ball mBall;

    /*package*/ GameField() {
        mBall = new Ball();
    }

    // TODO: change to fixed field size
    public void updateBounds(int width, int height) {
        mWidth = width;
        mHeight = height;

        mBall.mBallWidth = height / 20;
        mBall.resetBallPos();

        mPlayerLeft = height / 2;
        mPlayerRight = height / 2;
    }

    /**
     * @return The ball's width.
     */
    public int getBallWidth() {
        return mBall.mBallWidth;
    }

    /**
     * @return The y position of the middle of the left player's stroke.
     */
    public int getPlayerLeftPos() {
        return mPlayerLeft;
    }

    /**
     * Set the left player's y position. Note that the position may be adjusted,
     * so that the player's stroke isn't out of the game field.
     *
     * @param y The y position.
     */
    public void setPlayerLeftPos(int y) {
        if (y - mBall.mBallWidth * 2 < 0) {
            mPlayerLeft = mBall.mBallWidth * 2;
        } else if (y + mBall.mBallWidth * 2 >= mHeight) {
            mPlayerLeft = mHeight - 1 - mBall.mBallWidth * 2;
        } else {
            mPlayerLeft = y;
        }
    }

    /**
     * @return The y position of the middle of the right player's stroke.
     */
    public int getPlayerRightPos() {
        return mPlayerRight;
    }

    /**
     * Set the right player's y position. Note that the position may be adjusted,
     * so that the player's stroke isn't out of the game field.
     *
     * @param y The y position.
     */
    public void setPlayerRightPos(int y) {
        if (y - mBall.mBallWidth * 2 < 0) {
            mPlayerRight = mBall.mBallWidth * 2;
        } else if (y + mBall.mBallWidth * 2 >= mHeight) {
            mPlayerRight = mHeight - 1 - mBall.mBallWidth * 2;
        } else {
            mPlayerRight = y;
        }
    }

    /**
     * @return The center x coordinate of the ball.
     */
    public float getBallX() {
        return mBall.mX;
    }

    public void setBallPosition(float x, float y) {
        mBall.mX = x;
        mBall.mY = y;
    }

    /**
     * @return The center y coordinate of the ball.
     */
    public float getBallY() {
        return mBall.mY;
    }

    /**
     * Resets the ball's position to the middle of the game field. The ball's degree is chosen randomly.
     */
    public void spawnBall() {
        mBall.resetBallPos();
        mBall.mDegrees = (int) (Math.random() * 60 + 150);
        if (Math.random() < 0.5) {
            mBall.mDegrees = (mBall.mDegrees + 180) % 360;
        }
    }

    /**
     * Move the ball with the corresponding speed.
     *
     * @param gameSpeed The movement speed.
     */
    public void moveBall(double gameSpeed) {
        mBall.move(gameSpeed);
    }

    private class Ball {

        private float mX;
        private float mY;
        private int mBallWidth;

        private int mDegrees = 340;

        private void resetBallPos() {
            mX = mWidth / 2;
            mY = 2 * mBallWidth;
        }

        private int getBounceDegree(float ballY, int playerY) {
            playerY = playerY - 2 * mBallWidth - mBallWidth / 2;
            ballY = ballY - playerY;
            playerY = 5 * mBallWidth;
            float factor = ballY / playerY;

            return (int) (factor * 120 + 30);
        }

        private void move(double speed) {

            double difX = Math.cos(Math.toRadians(mDegrees)) * speed;
            double difY = Math.sin(Math.toRadians(mDegrees)) * speed;

            mX += difX;
            mY -= difY;

            if (mX < mBallWidth + mBallWidth / 2) {
                // outside of left
                if (mY >= mPlayerLeft - mBallWidth * 2 - mBallWidth / 2 && mY <= mPlayerLeft + mBallWidth * 2 + mBallWidth / 2) {
                    // player hit ball
                    mX -= difX;
                    mDegrees = (180 - getBounceDegree(mY, mPlayerLeft) + 270) % 360;

                } else {
                    EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.BALL_OUTSIDE_LEFT));
                    resetBallPos();
                }
            } else if (mX > mWidth - mBallWidth - mBallWidth / 2) {
                // outside of right
                if (mY >= mPlayerRight - mBallWidth * 2 - mBallWidth / 2 && mY <= mPlayerRight + mBallWidth * 2 + mBallWidth / 2) {
                    // player hit ball
                    mX -= difX;
                    mDegrees = getBounceDegree(mY, mPlayerRight) + 90;

                } else {
                    EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.BALL_OUTSIDE_RIGHT));
                    resetBallPos();
                }
            }

            // check y edges
            if (mY < mBallWidth / 2) {
                mY += difY;
                mDegrees = 360 - mDegrees;
            } else if (mY + mBallWidth / 2 >= mHeight) {
                mY += difY;
                mDegrees = 360 - mDegrees;
            }

            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.BALL_MOVED));
        }
    }
}