package net.vrallev.android.pong.game;

import com.google.gson.Gson;

/**
 * @author Ralf Wondratschek
 */
public class GameState {

    public static GameState fromJson(String json) {
        return new Gson().fromJson(json, GameState.class);
    }

    /*package*/ final boolean mRunning;
    /*package*/ final double mGameSpeed;
    /*package*/ final GameField mGameField;
    /*package*/ final int mPlayerLeftScore;
    /*package*/ final int mPlayerRightScore;

    public GameState(GameController host) {
        mGameField = host.getGameField();
        mRunning = host.isGameRunning();
        mGameSpeed = host.getGameSpeed();
        mPlayerLeftScore = host.getPlayerLeftScore();
        mPlayerRightScore = host.getPlayerRightScore();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
