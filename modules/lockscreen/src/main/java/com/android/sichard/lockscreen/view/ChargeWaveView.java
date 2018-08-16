package com.android.sichard.lockscreen.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.android.sichard.lockscreen.utils.BatteryInfo;

/**
 *<br>类描述：充电波形的view
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 18-5-22</b>
 */
public class ChargeWaveView extends View {

	private static final float FLOAT_0_POINT_01 = 0.01f;
	private static final int RED_IMG_LINE = 14;
	private static final int YELLOW_IMG_LINE = 29;

	private Path mAboveWavePath = new Path();
	private Path mBlowWavePath = new Path();
	private Path mPath = new Path();
	private Paint mAboveWavePaint = new Paint();
	private Paint mBlowWavePaint = new Paint();

	private static final int DEFAULT_ABOVE_WAVE_ALPHA = 128;
	private static final int DEFAULT_BLOW_WAVE_ALPHA = 128;

	private static final int RED_COLOR = Color.parseColor("#E31100");
	private static final int GREEN_COLOR = Color.parseColor("#00bcd4");
	private static final int YELLOW_COLOR = Color.parseColor("#D5C113");
	/** 当前电量 */
	private float mProgress;

	/** y轴的位移量(用来指示当前电量) */
	private int mWaveToTop;
	/** 余弦波x轴的刻度 */
	private int mX_zoom = 0/*(int) (100 * DENSITY)*/;
	/** 振幅 */
	private final int mY_zoom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
	/** 控制波形的周期(波形频率) */
	private float mAnimOffset = 0.07f;

	/** x轴方向的增量 */
	private final float mOffset = 0.5f;
	private final float mMax_right = mX_zoom * mOffset;

	// 两个波形的起始位移
	private float mAboveOffset = 0.0f;
	private float mBlowOffset = 4.0f;

	// 刷新线程
	private RefreshProgressRunnable mRefreshProgressRunnable;
	private int mWidthSize;
	private int mHeightSize;

	@SuppressLint({ "NewApi", "InlinedApi" })
	public ChargeWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		mPath.reset();
		mPath.addRect(0, 0, mWidthSize, mHeightSize, Path.Direction.CW);
		canvas.clipPath(mPath, Region.Op.INTERSECT);

		canvas.drawPath(mBlowWavePath, mBlowWavePaint);
		canvas.drawPath(mAboveWavePath, mAboveWavePaint);
		canvas.restore();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mHeightSize = bottom - top;
		mWidthSize = right - left;
		mX_zoom = mWidthSize / 3;
	}
	
	/**
	 * 计算余弦波的轨迹
	 */
	private void calculatePath() {
		mAboveWavePath.reset();
		mBlowWavePath.reset();

		if (isCharging() && mProgress != 1) {
			getWaveOffset();
			mAboveWavePath.moveTo(0, mHeightSize);
			for (float i = 0; mX_zoom * i <= mWidthSize + mMax_right; i += mOffset) {
				mAboveWavePath.lineTo(mX_zoom * i, (float) (mY_zoom * Math.cos(i + mAboveOffset)) + mWaveToTop);
			}
			mAboveWavePath.lineTo(mWidthSize, mHeightSize);
			mAboveWavePath.close();

			mBlowWavePath.moveTo(0, mHeightSize);
			for (float i = 0; mX_zoom * i <= mWidthSize + mMax_right; i += mOffset) {
				mBlowWavePath.lineTo(mX_zoom * i, (float) (mY_zoom * Math.cos(i + mBlowOffset)) + mWaveToTop);
			}
			mBlowWavePath.lineTo(mWidthSize, mHeightSize);
			mBlowWavePath.close();
		} else {
			mAboveWavePath.moveTo(0, mHeightSize);
			mAboveWavePath.lineTo(mWidthSize, mHeightSize);
			mAboveWavePath.lineTo(mWidthSize, mWaveToTop);
			mAboveWavePath.lineTo(0, mWaveToTop);
			mAboveWavePath.close();

			mBlowWavePath.moveTo(0, mHeightSize);
			mBlowWavePath.lineTo(mWidthSize, mHeightSize);
			mBlowWavePath.lineTo(mWidthSize, mWaveToTop);
			mBlowWavePath.lineTo(0, mWaveToTop);
			mBlowWavePath.close();
		}
	}

	/** <br>功能简述：设置充电进度
	 * @param progress 充电进度
	 */
	public void setProgress(float progress) {
		if (progress <= RED_IMG_LINE) {
			setPainters(RED_COLOR);
		} else if (progress >= RED_IMG_LINE && progress <= YELLOW_IMG_LINE) {
			setPainters(YELLOW_COLOR);
		} else {
			setPainters(GREEN_COLOR);
		}
		progress = progress * FLOAT_0_POINT_01;
		this.mProgress = progress > 1 ? 1 : progress;
	}

	/**
	 * <br>功能简述:设置绘制波浪的颜色
	 * @param color 波浪的颜色
	 */
	private void setPainters(int color) {
		mAboveWavePaint.setColor(color);
		mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
		mAboveWavePaint.setStyle(Paint.Style.FILL);
		mAboveWavePaint.setAntiAlias(true);

		mBlowWavePaint.setColor(color);
		mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
		mBlowWavePaint.setStyle(Paint.Style.FILL);
		mBlowWavePaint.setAntiAlias(true);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mRefreshProgressRunnable = new RefreshProgressRunnable();
		post(mRefreshProgressRunnable);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		removeCallbacks(mRefreshProgressRunnable);
	}

	private void getWaveOffset() {
		if (mBlowOffset > Float.MAX_VALUE - 100) {
			mBlowOffset = 0;
		} else {
			mBlowOffset += mAnimOffset;
		}

		if (mAboveOffset > Float.MAX_VALUE - 100) {
			mAboveOffset = 0;
		} else {
			mAboveOffset += mAnimOffset;
		}
	}

	/**
	 *<br>类描述：刷新正弦波的Runnable
	 *<br>详细描述：
	 *<br><b>Author sichard</b>
	 *<br><b>Date 18-5-22</b>
	 */
	private class RefreshProgressRunnable implements Runnable {
		public void run() {
			synchronized (ChargeWaveView.this) {
				mWaveToTop = (int) (mHeightSize * (1f - mProgress));

				calculatePath();

				invalidate();
				
				postDelayed(this, 40);
			}
		}
	}

	/** <br>功能简述:是否正在充电
	 * @return true,正在充电；false，不在充电
	 */
	private boolean isCharging() {
		return BatteryInfo.isElectricity(getContext());
	}
}
