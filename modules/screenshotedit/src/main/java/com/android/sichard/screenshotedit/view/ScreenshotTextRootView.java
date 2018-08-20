package com.android.sichard.screenshotedit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.android.sichard.screenshotedit.IScreenshotEditView;
import com.android.sichard.screenshotedit.R;


public class ScreenshotTextRootView extends RelativeLayout  implements IScreenshotEditView {

    ScreenshotTextView mPhotoEditorView;
    Bitmap mBitmap = null;
    boolean isFinishInFlated = false;

    public ScreenshotTextRootView(Context context) {
        super(context);
    }

    public ScreenshotTextRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenshotTextRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        if (isFinishInFlated) {
            mPhotoEditorView.setImgSrc(bitmap);
        } else {
            mBitmap = bitmap;
        }
    }

    @Override
    public Bitmap getBitmap() {
        return mPhotoEditorView.getBitmap();
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        isFinishInFlated = true;
    }

    private void initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.addText("", 0xFFFF3232);
        if (mBitmap != null) {
            mPhotoEditorView.setImgSrc(mBitmap);
        }
    }

    public void addText() {
        mPhotoEditorView.addText("", 0xFFFF3232);
    }


}
