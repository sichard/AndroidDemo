/**  
 * @author: caoshichao  
 * @date: 2015-5-13 上午11:26:53
 */
package com.android.sichard.lockscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 *充电进度指示的view
 *<br><b>Author sichard</b>
 *<br><b>Date 2017/2/20</b>
 */
public class ChargeProgressClipImageView extends AppCompatImageView {

	private Rect mRect = new Rect(0, 0, 0, 0);
	/** 曲线摇摆的幅度 */
	private int range = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

	/**
	 * @param context
	 * @param attrs
	 */
	public ChargeProgressClipImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.clipRect(mRect);
		super.onDraw(canvas);
	}

	/** <br>功能简述:裁剪图片，显示对应绿色圆点的个数
	 * @param i 表示显示绿色圆点的个数
	 */
	public void clipBound(int i) {
		mRect.bottom = getHeight();
		switch (i) {
			case 1 :
				mRect.right = getWidth() / 2 - range;
				break;
			case 2 :
				mRect.right = getWidth() / 2 + range;
				break;
			case 3 :
				mRect.right = getWidth();
				break;

			default :
				break;
		}
		invalidate();
	}

    /**
     * 根据电池电量来显示对应绿色圆点的个数
     * @param batteryLevel 当前电池电量
     */
    public void setProgress(int batteryLevel) {
        mRect.bottom = getHeight();
        if (batteryLevel <= 80) {
            mRect.right = getWidth() / 2 - range;
        } else if (batteryLevel > 80 && batteryLevel < 100) {
            mRect.right = getWidth() / 2 + range;
        } else {
            mRect.right = getWidth();
        }
        invalidate();
    }
}
