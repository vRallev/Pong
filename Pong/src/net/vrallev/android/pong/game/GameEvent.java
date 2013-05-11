package net.vrallev.android.pong.game;

import java.net.CacheRequest;
import java.util.HashMap;

/**
 * @author Ralf Wondratschek
 */
public class GameEvent {

    private static HashMap<Action, GameEvent> cache = new HashMap<Action, GameEvent>();

    public static GameEvent obtain(Action action) {
        GameEvent event = cache.get(action);
        if (event == null) {
            event = new GameEvent();
            event.mAction = action;
            cache.put(action, event);
        }

        return event;
    }



    private Action mAction;
    private int mPositionY;

    private GameEvent() {

    }

    public Action getAction() {
        return mAction;
    }

    public int getPositionY() {
        return mPositionY;
    }

    public GameEvent setPositionY(int positionY) {
        mPositionY = positionY;
        return this;
    }

    public static enum Action {

        BALL_OUTSIDE_LEFT,
        BALL_OUTSIDE_RIGHT,
        BALL_MOVED,
        PLAYER_LEFT_MOVED,
        PLAYER_RIGHT_MOVED,
        CONTINUE_GAME,
        PAUSE_GAME,
        GAME_CLOSED

    }

}
