package com.android.sichard.lockscreen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 *<br>类描述：充电过程指示器
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2017/2/20</b>
 */
public class ChargeProgressIndicateView extends View {

	/** 指示器圆球直径 */
	private static final float RADIUS = 32f; // 38是由小球直径像素除以3算出来的，因为适配的是1080p所以除以3
	/** 指示器线条宽度 */
	private static final float STROKE_WIDTH = 1;
	private Paint mPaint = null;
	private float mStartX;
	private float mDensity;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	public ChargeProgressIndicateView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mDensity = getResources().getDisplayMetrics().density;
		mStartX = RADIUS * mDensity;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeWidth(STROKE_WIDTH * mDensity);
		mPaint.setColor(Color.parseColor("#F3F3F3"));
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int mWidth = right - left;
		int mHeight = bottom - top;
		// 如果快高小于等于0，则手动指定宽高，防止因为宽高问题导致创建bitmap是异常，进而导致空指针
		if (mWidth <= 0) {
			mWidth = (int) (getResources().getDisplayMetrics().widthPixels - 55 * mDensity);
		} else if (mHeight <= 0){
			mHeight = (int) (4 * mDensity);
		}
		if (mBitmap == null) {
			try {
				mBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		if (mCanvas == null) {
			// 此处先将要画的直线画在bitmap上，然后再将bitmap画到canvas上的是由于Paint的setStrokeCap(Paint.Cap.ROUND)方法不生效
			mCanvas = new Canvas(mBitmap);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
		super.onDraw(canvas);
	}

	/** <br>功能简述:设置当前电量
	 * @param level 当前电量
	 */
	public void setProgress(int level) {
		float width = getWidth() - RADIUS * 3 * mDensity;
		float harfWidth = width / 2f;
		float mStopX;
		if (level <= 80) {
			mStopX = mStartX + harfWidth * level / 80;
		} else if (level > 80 && level < 100) {
			mStopX = harfWidth + mStartX * 2 + harfWidth * (level - 80) / 20;
		} else {
			mStopX = mStartX * 2 + width;
		}
		if (mCanvas != null) {
			mCanvas.drawLine(mStartX, getHeight() / 2 , mStopX, getHeight() / 2 , mPaint);
		}
		invalidate();
	}
}
