package com.android.sichard.screenshotedit.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.sichard.screenshotedit.R;


/**
 * <br>类描述:截屏编辑界面选择颜色的View
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-9</b>
 */
public class ScreenshotColorView extends LinearLayout implements View.OnClickListener {

    private final int WHITE = 1, BLACK = 2, RED = 3, YELLOW = 4, BLUE = 5, GREEN = 6;
    private ImageView mImWhite, mImBlack, mImRed, mImYellow, mImBlue, mImGreen;
    private ImageView mSelectedView;
    private ColorChangedListener mColorChangedListener;

    public ScreenshotColorView(Context context) {
        super(context);
    }

    public ScreenshotColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenshotColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImWhite = findViewById(R.id.screenshot_white);
        mImBlack = findViewById(R.id.screenshot_black);
        mImRed = findViewById(R.id.screenshot_red);
        mImYellow = findViewById(R.id.screenshot_yellow);
        mImBlue = findViewById(R.id.screenshot_blue);
        mImGreen = findViewById(R.id.screenshot_green);

        mImWhite.setOnClickListener(this);
        mImBlack.setOnClickListener(this);
        mImRed.setOnClickListener(this);
        mImYellow.setOnClickListener(this);
        mImBlue.setOnClickListener(this);
        mImGreen.setOnClickListener(this);

        mImWhite.setTag(WHITE);
        mImBlack.setTag(BLACK);
        mImRed.setTag(RED);
        mImYellow.setTag(YELLOW);
        mImBlue.setTag(BLUE);
        mImGreen.setTag(GREEN);
        mSelectedView = mImRed;
    }

    @Override
    public void onClick(View view) {
        if (view == mSelectedView) {
            return;
        }

        if (view == mImWhite) {
            mImWhite.setImageResource(R.drawable.screenshot_white_select);
            reverse(mSelectedView);
            mSelectedView = mImWhite;
            mColorChangedListener.onColorChanged(0xFFFFFFFF);
        } else if (view == mImBlack) {
            mImBlack.setImageResource(R.drawable.screenshot_black_select);
            reverse(mSelectedView);
            mSelectedView = mImBlack;
            mColorChangedListener.onColorChanged(0xFF000000);
        } else if (view == mImRed) {
            mImRed.setImageResource(R.drawable.screenshot_red_select);
            reverse(mSelectedView);
            mSelectedView = mImRed;
            mColorChangedListener.onColorChanged(0xFFFF3232);
        } else if (view == mImYellow) {
            mImYellow.setImageResource(R.drawable.screenshot_yellow_select);
            reverse(mSelectedView);
            mSelectedView = mImYellow;
            mColorChangedListener.onColorChanged(0xFFFFE909);
        } else if (view == mImBlue) {
            mImBlue.setImageResource(R.drawable.screenshot_blue_select);
            reverse(mSelectedView);
            mSelectedView = mImBlue;
            mColorChangedListener.onColorChanged(0xFF01BEFE);
        } else if (view == mImGreen) {
            mImGreen.setImageResource(R.drawable.screenshot_green_select);
            reverse(mSelectedView);
            mSelectedView = mImGreen;
            mColorChangedListener.onColorChanged(0xFF59EF5D);
        }
    }

    private void reverse(ImageView selectedView) {
        int tag = (int) selectedView.getTag();
        switch (tag) {
            case WHITE:
                mImWhite.setImageResource(R.drawable.screenshot_white);
                break;
            case BLACK:
                mImBlack.setImageResource(R.drawable.screenshot_black);
                break;
            case RED:
                mImRed.setImageResource(R.drawable.screenshot_red);
                break;
            case YELLOW:
                mImYellow.setImageResource(R.drawable.screenshot_yellow);
                break;
            case BLUE:
                mImBlue.setImageResource(R.drawable.screenshot_blue);
                break;
            case GREEN:
                mImGreen.setImageResource(R.drawable.screenshot_green);
                break;
        }
    }

    public void setColorChangedListener(ColorChangedListener colorChangedListener) {
        mColorChangedListener = colorChangedListener;
    }


    public interface ColorChangedListener {
        void onColorChanged(int color);
    }
}
