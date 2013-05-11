package net.vrallev.android.pong.game;

import de.greenrobot.event.EventBus;

/**
 * @author Ralf Wondratschek
 */
public class GameController {

    private static final double DEFAULT_SPEED = 1.0;

    private final GameField mGameField;
    private final GameLoop mGameLoop;

    private double mGameSpeed;

    private int mPlayerLeftScore;
    private int mPlayerRightScore;

    public GameController() {
        mGameSpeed = DEFAULT_SPEED;

        mGameField = new GameField();

        mGameLoop = new GameLoop(this);
        mGameLoop.start();
    }

    public GameController(GameState state) {
        mGameSpeed = state.mGameSpeed;
        mGameField = state.mGameField;
        mPlayerLeftScore = state.mPlayerLeftScore;
        mPlayerRightScore = state.mPlayerRightScore;

        mGameLoop = new GameLoop(this);
        mGameLoop.start();
    }

    /**
     * Pause or continue the game.
     *
     * @param running {@code true}, if the game should continue, {@code false} if the game should pause.
     */
    public void setGameRunning(boolean running) {
        mGameLoop.setRunning(running);
    }

    /**
     * @return {@code true}, if the game is running, {@code false} if the game is paused.
     */
    public boolean isGameRunning() {
        return mGameLoop.isRunning();
    }

    /**
     * Stops the game. To start a new one, you need to create a new instance.
     */
    public void stopGame() {
        mGameLoop.stopLoop();
    }

    public GameField getGameField() {
        return mGameField;
    }

    public double getGameSpeed() {
        return mGameSpeed;
    }

    public void resetGameSpeed() {
        setGameSpeed(DEFAULT_SPEED);
    }

    public void setGameSpeed(double speed) {
        mGameSpeed = speed;
        mGameLoop.setGameSpeed(speed);
    }

    public int getPlayerRightScore() {
        return mPlayerRightScore;
    }

    public void setPlayerRightScore(int playerRightScore) {
        mPlayerRightScore = playerRightScore;
        checkScore(playerRightScore);
    }

    public int getPlayerLeftScore() {
        return mPlayerLeftScore;
    }

    public void setPlayerLeftScore(int playerLeftScore) {
        mPlayerLeftScore = playerLeftScore;
        checkScore(playerLeftScore);
    }

    private void checkScore(int score) {
        if (score == 10) {
            EventBus.getDefault().post(GameEvent.obtain(GameEvent.Action.GAME_FINISHED));
        }
    }
}
