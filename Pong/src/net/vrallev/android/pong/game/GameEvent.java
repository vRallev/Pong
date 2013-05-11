package net.vrallev.android.pong.game;

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

    private GameEvent() {

    }

    public Action getAction() {
        return mAction;
    }

    public static enum Action {

        BALL_OUTSIDE_LEFT,
        BALL_OUTSIDE_RIGHT,
        CONTINUE_GAME,
        PAUSE_GAME,
        GAME_CLOSED,
        GAME_FINISHED

    }

}
