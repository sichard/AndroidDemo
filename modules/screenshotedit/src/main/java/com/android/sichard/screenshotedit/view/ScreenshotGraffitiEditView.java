package com.android.sichard.screenshotedit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.android.sichard.screenshotedit.IScreenshotEditView;
import com.android.sichard.screenshotedit.R;


/**
 * <br>类描述:涂鸦编辑界面(包括涂鸦界面和底部的控制栏)
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-9</b>
 */
public class ScreenshotGraffitiEditView extends LinearLayout implements IScreenshotEditView, ScreenshotColorView.ColorChangedListener {
    private ScreenshotGraffitiView mGraffitiView;

    public ScreenshotGraffitiEditView(Context context) {
        super(context);
    }

    public ScreenshotGraffitiEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenshotGraffitiEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGraffitiView = findViewById(R.id.screenshot_graffiti_view);
        ScreenshotColorView colorView = findViewById(R.id.screenshot_graffiti_bar);
        colorView.setColorChangedListener(this);
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        mGraffitiView.setBitmap(bitmap);
    }

    @Override
    public Bitmap getBitmap() {
        return mGraffitiView.getBitmap();
    }

    public void setGraffitiViewSize(int width, int height) {
        mGraffitiView.setBitmapSize(width, height);
    }

    @Override
    public void onColorChanged(int color) {
        mGraffitiView.setColor(color);
    }
}
