package net.vrallev.android.pong.view;

import net.vrallev.android.pong.GameField;
import net.vrallev.android.pong.util.L;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
	
	private int mBallWidth;
	
	private GameField mGameField;
	
	public DrawingView(Context context) {
		super(context);
		construtor();
	}
	
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		construtor();
	}

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
		
		int leftY = mGameField.getPlayerLeftPos();
		int rightY = mGameField.getPlayerRightPos();
		
		onDrawPlayer(canvas, mBallWidth / 2, leftY);
		onDrawPlayer(canvas, mWidth - mBallWidth / 2, rightY);
		
		invalidate();
		
	}
	
	protected void onDrawMiddleLine(Canvas canvas) {
		int x = mWidth / 2;
		float step = mHeight / 41f;
		
		for (float y = 0; y < mHeight; y += step * 2) {
			canvas.drawLine(x, (int)y, x, (int) (y + step), mPaintMiddleLine);
		}
	}
	
	protected void onDrawPlayer(Canvas canvas, int x, int y) {
		canvas.drawLine(x, y - 2 * mBallWidth, x, y + 2 * mBallWidth, mPaint);
	}
	
	protected void onDrawBall(Canvas canvas, float x, float y) {
		canvas.drawPoint(x, y, mPaint);
	}
	
	private boolean mFirstTouchLeft;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mFirstTouchLeft = event.getX() < mWidth / 2;
			
			if (!mGameField.isGameRunning()) {
				mGameField.spawnBall();
				mGameField.setGameRunning(true);
			}
		}
		
		int pointerCount = event.getPointerCount();

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
			mGameField.setPlayerLeftPos(leftY);
		}
		if (rightY >= 0) {
			mGameField.setPlayerRightPos(rightY);
		}
		
		return true;
	}
	
	/**
	 * @param gameField The field, which should be drawn.
	 */
	public void setGameField(GameField gameField) {
		mGameField = gameField;
		setGameFieldBounds(mWidth, mHeight);
	}
	
	private void setGameFieldBounds(int w, int h) {
		if (mGameField != null) {
			mGameField.updateBounds(w, h);
			
			mPaint.setStrokeWidth(mGameField.getBallWidth());
			mPaintMiddleLine.setStrokeWidth(mGameField.getBallWidth() / 4);
			
			mBallWidth = mGameField.getBallWidth();
		}
	}
}
