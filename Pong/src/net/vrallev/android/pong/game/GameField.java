package net.vrallev.android.pong.game;

import android.graphics.Matrix;

/**
 * This class represents the game field.
 *
 * @author Ralf Wondratschek
 */
public class GameField {

    public static final int WIDTH = 2560;
    public static final int HEIGHT = 1440;

    private Matrix mMatrix;
    private float mScaleX;
    private float mScaleY;

    private int mPlayerLeft;
    private int mPlayerRight;

    private GameBall mBall;

    /*package*/ GameField() {
        mMatrix = new Matrix();

        mPlayerLeft = HEIGHT / 2;
        mPlayerRight = HEIGHT / 2;

        mBall = new GameBall();
        mBall.setPlayerLeft(mPlayerLeft);
        mBall.setPlayerRight(mPlayerRight);
    }

    public void setDisplayBounds(int width, int height) {
        mScaleX = width / (float) WIDTH;
        mScaleY = height / (float) HEIGHT;

        mMatrix.setScale(mScaleX, mScaleY);
    }

    /**
     * @return The ball's width.
     */
    public int getBallWidth() {
        return (int) mMatrix.mapRadius(GameBall.WIDTH);
    }

    /**
     * @return The y position of the middle of the left player's stroke.
     */
    public int getPlayerLeftPos() {
        return (int) (mPlayerLeft * mScaleY);
    }

    /**
     * Set the left player's y position. Note that the position may be adjusted,
     * so that the player's stroke isn't out of the game field.
     *
     * @param y The y position.
     */
    public void setPlayerLeftPos(int y) {
        y = (int) (y / mScaleY);

        if (y - GameBall.WIDTH * 2 < 0) {
            mPlayerLeft = GameBall.WIDTH * 2;
        } else if (y + GameBall.WIDTH * 2 >= HEIGHT) {
            mPlayerLeft = HEIGHT - 1 - GameBall.WIDTH * 2;
        } else {
            mPlayerLeft = y;
        }

        mBall.setPlayerLeft(y);
    }

    /**
     * @return The y position of the middle of the right player's stroke.
     */
    public int getPlayerRightPos() {
        return (int) (mPlayerRight * mScaleY);
    }

    /**
     * Set the right player's y position. Note that the position may be adjusted,
     * so that the player's stroke isn't out of the game field.
     *
     * @param y The y position.
     */
    public void setPlayerRightPos(int y) {
        y = (int) (y / mScaleY);

        if (y - GameBall.WIDTH * 2 < 0) {
            mPlayerRight = GameBall.WIDTH * 2;
        } else if (y + GameBall.WIDTH * 2 >= HEIGHT) {
            mPlayerRight = HEIGHT - 1 - GameBall.WIDTH * 2;
        } else {
            mPlayerRight = y;
        }

        mBall.setPlayerRight(y);
    }

    /**
     * @return The center x coordinate of the ball.
     */
    public float getBallX() {
        return mBall.mX * mScaleX;
    }

    /**
     * @return The center y coordinate of the ball.
     */
    public float getBallY() {
        return mBall.mY * mScaleY;
    }

    /**
     * Move the ball with the corresponding speed.
     *
     * @param gameSpeed The movement speed.
     */
    public void moveBall(double gameSpeed) {
        mBall.move(gameSpeed);
    }
}