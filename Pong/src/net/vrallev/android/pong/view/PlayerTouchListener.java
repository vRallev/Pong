package net.vrallev.android.pong.view;

import android.view.MotionEvent;
import android.view.View;
import net.vrallev.android.pong.game.GameEvent;
import net.vrallev.android.pong.game.GameField;

/**
 * @author Ralf Wondratschek
 */
public class PlayerTouchListener implements View.OnTouchListener {

    private boolean mOnePlayer;
    private GameField mGameField;

    private boolean mFirstTouchLeft;
    private boolean mIgnoreTouch;

    public PlayerTouchListener(boolean onePlayer, GameField gameField) {
        mOnePlayer = onePlayer;
        mGameField = gameField;

        mIgnoreTouch = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mFirstTouchLeft = event.getX() < v.getWidth() / 2;
        }

        int pointerCount = event.getPointerCount();

        if (!mOnePlayer) {
            float leftY = -1;
            float rightY = -1;

            for (int i = 0; i < pointerCount; i++) {
                if (event.getPointerId(i) == 0) {
                    if (mFirstTouchLeft) {
                        leftY = event.getY(i);
                    } else {
                        rightY = event.getY(i);
                    }
                } else if (event.getPointerId(i) == 1) {
                    if (mFirstTouchLeft) {
                        rightY = event.getY(i);
                    } else {
                        leftY = event.getY(i);
                    }
                }
            }

            if (leftY >= 0 && !mIgnoreTouch) {
                mGameField.setPlayerLeftPos(leftY);
            }
            if (rightY >= 0 && !mIgnoreTouch) {
                mGameField.setPlayerRightPos(rightY);
            }

        } else {
            if (!mIgnoreTouch) {
                mGameField.setPlayerRightPos(event.getY());
            }
        }

        return true;
    }

    public void onEvent(GameEvent event) {
        switch (event.getAction()) {
            case CONTINUE_GAME:
                mIgnoreTouch = false;
                break;

            case PAUSE_GAME:
            case BALL_OUTSIDE_LEFT:
            case BALL_OUTSIDE_RIGHT:
                mIgnoreTouch = true;
                break;
        }
    }
}
