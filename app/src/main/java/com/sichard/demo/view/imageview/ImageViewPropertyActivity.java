package com.sichard.demo.view.imageview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sichard.demo.R;

/**
 * <br>类描述:
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-2</b>
 */
public class ImageViewPropertyActivity extends Activity {
    private ImageView mImageView;
    private float mRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_imageview_property);
        mImageView = findViewById(R.id.image_property);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mImageView.setMaxHeight(displayMetrics.heightPixels);
        mImageView.setMaxWidth(displayMetrics.widthPixels);

        mRate = displayMetrics.heightPixels * 1.0f /displayMetrics.widthPixels;
    }

    public void onNormalClick(View view) {
        mImageView.setImageBitmap(getBitmap(R.mipmap.image_normal));
        showImageSize();
    }

    public void onHeightClick(View view) {
        mImageView.setImageBitmap(getBitmap(R.mipmap.image_height));
        showImageSize();
    }

    public void onWidthClick(View view) {
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setImageBitmap(getBitmap(R.mipmap.image_width));
        showImageSize();
    }

    private Bitmap getBitmap(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        float bitmapRate = bitmap.getHeight() * 1.0f / bitmap.getWidth();
        int height = 0;
        int width = 0;
        if (mRate > bitmapRate) {
            height = 1920;
            width = (int) (1920f / bitmap.getHeight() * bitmap.getWidth());
        } else {
            height = (int) (1080f / bitmap.getWidth() * bitmap.getHeight());
            width = 1080;
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void showImageSize() {
        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("sichardcao", "ImageViewPropertyActivity|onNormalClick:" + mImageView.getHeight() + "*" + mImageView.getWidth());
            }
        }, 200);
    }
}