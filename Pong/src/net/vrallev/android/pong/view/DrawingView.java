package net.vrallev.android.pong.view;

import net.vrallev.android.pong.game.GameField;
import net.vrallev.android.base.util.L;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * The view drawing a {@link GameField}.
 * 
 * @author Ralf Wondratschek
 *
 */
public class DrawingView extends View {
	
	@SuppressWarnings("unused")
	private static final L L = new L(DrawingView.class);
	
	private Paint mPaint;
	private Paint mPaintMiddleLine;
	
	private int mHeight;
	private int mWidth;
	
	private float mBallWidth;
	
	private GameField mGameField;
	
	@SuppressWarnings("UnusedDeclaration")
    public DrawingView(Context context) {
		super(context);
		construtor();
	}

    @SuppressWarnings("UnusedDeclaration")
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		construtor();
	}

    @SuppressWarnings("UnusedDeclaration")
	public DrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		construtor();
	}

	private void construtor() {
		mPaint = new Paint();
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
		
		mPaintMiddleLine = new Paint();
		mPaintMiddleLine.setStyle(Style.FILL_AND_STROKE);
		mPaintMiddleLine.setAntiAlias(true);
		mPaintMiddleLine.setColor(Color.WHITE);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;
		
		setGameFieldBounds(w, h);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mGameField == null) {
			return;
		}
		
		onDrawBall(canvas, mGameField.getBallX(), mGameField.getBallY());
		
		onDrawMiddleLine(canvas);

        float leftY = mGameField.getPlayerLeftPos();
        float rightY = mGameField.getPlayerRightPos();
		
		onDrawPlayer(canvas, mBallWidth / 2, leftY);
		onDrawPlayer(canvas, mWidth - mBallWidth / 2, rightY);
		
		invalidate();
		
	}
	
	protected void onDrawMiddleLine(Canvas canvas) {
		int x = mWidth / 2;
		float step = mHeight / 41f;
		
		for (float y = 0; y < mHeight; y += step * 2) {
			canvas.drawLine(x, y, x, y + step, mPaintMiddleLine);
		}
	}
	
	protected void onDrawPlayer(Canvas canvas, float x, float y) {
		canvas.drawLine(x, y - 2 * mBallWidth, x, y + 2 * mBallWidth, mPaint);
	}
	
	protected void onDrawBall(Canvas canvas, float x, float y) {
		canvas.drawPoint(x, y, mPaint);
	}
	
	/**
	 * @param gameField The field, which should be drawn.
	 */
	public void setGameField(GameField gameField) {
		mGameField = gameField;
		setGameFieldBounds(mWidth, mHeight);
	}
	
	private void setGameFieldBounds(int w, int h) {
		if (mGameField != null && w > 0 && h > 0) {
			mGameField.setDisplayBounds(w, h);

			mPaint.setStrokeWidth(mGameField.getBallWidth());
			mPaintMiddleLine.setStrokeWidth(mGameField.getBallWidth() / 4);

			mBallWidth = mGameField.getBallWidth();
		}
	}
}
