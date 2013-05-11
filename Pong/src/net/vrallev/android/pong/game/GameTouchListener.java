package net.vrallev.android.pong.game;

import android.view.MotionEvent;
import android.view.View;
import de.greenrobot.event.EventBus;

/**
 * @author Ralf Wondratschek
 */
public class GameTouchListener implements View.OnTouchListener {

    private boolean mFirstTouchLeft;
    private boolean mOnePlayer;

    public GameTouchListener(boolean onePlayer) {
        mOnePlayer = onePlayer;
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
                EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PLAYER_LEFT_MOVED).setPositionY(leftY));
                //mGameField.setPlayerLeftPos(leftY);
            }
            if (rightY >= 0) {
                EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PLAYER_RIGHT_MOVED).setPositionY(rightY));
                //mGameField.setPlayerRightPos(rightY);
            }

        } else {
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.PLAYER_RIGHT_MOVED).setPositionY((int) event.getY()));
        }

        return true;
    }
}
