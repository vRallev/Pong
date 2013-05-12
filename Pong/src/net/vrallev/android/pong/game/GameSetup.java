package net.vrallev.android.pong.game;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import net.vrallev.android.pong.activity.GameActivity;

/**
 * @author Ralf Wondratschek
 */
public class GameSetup {

    public static GameSetup fromJson(String json) {
        return new Gson().fromJson(json, GameSetup.class);
    }

    public static GameSetup fromIntent(Intent intent) {
        return fromJson(intent.getStringExtra(GameActivity.GAME_SETUP));
    }

    private double mGameSpeed;
    private boolean mSinglePlayer;
    private int mAiDifficulty;

    public GameSetup() {
        mGameSpeed = 1.0;
        mSinglePlayer = true;
        mAiDifficulty = 2;
    }

    public double getGameSpeed() {
        return mGameSpeed;
    }

    public GameSetup setGameSpeed(double gameSpeed) {
        mGameSpeed = gameSpeed;
        return this;
    }

    public boolean isSinglePlayer() {
        return mSinglePlayer;
    }

    public GameSetup setSinglePlayer(boolean singlePlayer) {
        mSinglePlayer = singlePlayer;
        return this;
    }

    public int getAiDifficulty() {
        return mAiDifficulty;
    }

    public GameSetup setAiDifficulty(int aiDifficulty) {
        mAiDifficulty = aiDifficulty;
        return this;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Intent createIntent(Context context) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GameActivity.GAME_SETUP, toJson());
        return intent;
    }
}
