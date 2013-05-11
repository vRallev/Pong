package net.vrallev.android.pong.game;

import com.google.gson.Gson;

/**
 * @author Ralf Wondratschek
 */
public class GameState {

    public static GameState fromJson(String json) {
        return new Gson().fromJson(json, GameState.class);
    }

    private final boolean mRunning;
    private final double mGameSpeed;
    private final GameField mGameField;

    public GameState(GameHost host) {
        mGameField = host.getGameField();
        mRunning = host.isGameRunning();
        mGameSpeed = host.getGameSpeed();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
