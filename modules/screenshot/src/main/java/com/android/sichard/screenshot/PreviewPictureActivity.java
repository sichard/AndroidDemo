package com.android.sichard.screenshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 *<br>类描述：展示截图预览图的Activity
 *<br>详细描述：此处现将截图转到{@link GlobalScreenShot#takeScreenshot(Bitmap, GlobalScreenShot.onScreenShotListener, boolean, boolean)}中展示，
 * 之后通过回调 {@link GlobalScreenShot.onScreenShotListener#onFinishShot(boolean)}
 *<br><b>Author sichard</b>
 *<br><b>Date 18-5-23</b>
 */
public class PreviewPictureActivity extends FragmentActivity implements GlobalScreenShot.onScreenShotListener {

    private ImageView mPreviewImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_layout);
        mPreviewImageView = (ImageView) findViewById(R.id.preview_image);

        GlobalScreenShot screenshot = new GlobalScreenShot(getApplicationContext());

        String filePath = getIntent().getStringExtra("file_path");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        Log.e("ryze", "预览图片");
        mPreviewImageView.setImageBitmap(bitmap);
        mPreviewImageView.setVisibility(View.GONE);

        if (bitmap != null) {
            screenshot.takeScreenshot(bitmap, this, true, true);
        }

    }

    @Override
    public void onStartShot() {

    }

    @Override
    public void onFinishShot(boolean success) {
        Log.i("sichardcao", "PreviewPictureActivity|onFinishShot:" + "===截图结束回调");
        mPreviewImageView.setVisibility(View.VISIBLE);
    }
}
