package net.vrallev.android.pong.game;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author Ralf Wondratschek
 */
public class GameTouchListener implements View.OnTouchListener {

    private boolean mFirstTouchLeft;
    private boolean mOnePlayer;
    private GameField mGameField;

    public GameTouchListener(boolean onePlayer, GameField gameField) {
        mOnePlayer = onePlayer;
        mGameField = gameField;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mFirstTouchLeft = event.getX() < v.getWidth() / 2;
        }

        int pointerCount = event.getPointerCount();

        if (!mOnePlayer) {
            int leftY = -1;
            int rightY = -1;

            for (int i = 0; i < pointerCount; i++) {
                if (event.getPointerId(i) == 0) {
                    if (mFirstTouchLeft) {
                        leftY = (int) event.getY(i);
                    } else {
                        rightY = (int) event.getY(i);
                    }
                } else if (event.getPointerId(i) == 1) {
                    if (mFirstTouchLeft) {
                        rightY = (int) event.getY(i);
                    } else {
                        leftY = (int) event.getY(i);
                    }
                }
            }

            if (leftY >= 0) {
                mGameField.setPlayerLeftPos(leftY);
            }
            if (rightY >= 0) {
                mGameField.setPlayerRightPos(rightY);
            }

        } else {
            mGameField.setPlayerRightPos((int) event.getY());
        }

        return true;
    }
}
