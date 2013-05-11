package net.vrallev.android.pong.game;

/**
 * @author Ralf Wondratschek
 */
public class GameController {

    private final GameField mGameField;
    private final GameLoop mGameLoop;

    private double mGameSpeed;

    private int mPlayerLeftScore;
    private int mPlayerRightScore;

    public GameController() {
        mGameSpeed = 1.0;

        mGameField = new GameField();

        mGameLoop = new GameLoop(mGameField, mGameSpeed);
        mGameLoop.start();
    }

    public GameController(GameState state) {
        mGameSpeed = state.mGameSpeed;
        mGameField = state.mGameField;
        mPlayerLeftScore = state.mPlayerLeftScore;
        mPlayerRightScore = state.mPlayerRightScore;

        mGameLoop = new GameLoop(mGameField, mGameSpeed);
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

    public void setGameSpeed(double gameSpeed) {
        mGameSpeed = gameSpeed;
    }

    public int getPlayerRightScore() {
        return mPlayerRightScore;
    }

    public void setPlayerRightScore(int playerRightScore) {
        mPlayerRightScore = playerRightScore;
    }

    public int getPlayerLeftScore() {
        return mPlayerLeftScore;
    }

    public void setPlayerLeftScore(int playerLeftScore) {
        mPlayerLeftScore = playerLeftScore;
    }
}
