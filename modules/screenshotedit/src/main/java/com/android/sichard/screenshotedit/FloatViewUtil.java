package com.android.sichard.screenshotedit;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.android.sichard.common.utils.DrawUtils;


/**
 * Created by 王心觉 on 2018/8/15.
 */

public class FloatViewUtil {
    private Context mContext;
    private Handler mRemoveViewHandler;
    private Runnable mRemoveViewRunnalbe;
    private View mFloatView;

    public FloatViewUtil(Context context) {
        mContext = context;
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //TYPE_PHONE只要Activity不销毁，就有悬浮框，在屏幕层
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        lp.gravity = Gravity.LEFT|Gravity.BOTTOM;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = DrawUtils.dip2px(20);
        lp.y = DrawUtils.dip2px(80);
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        return lp;
    }

    public void addFloatView(View v) {
        mFloatView = v;

        WindowManager.LayoutParams lp = createLayoutParams();
        WindowManager mWindwwManager = (WindowManager) mContext.getSystemService(
                Context.WINDOW_SERVICE);
        mWindwwManager.addView(mFloatView, lp);

        mRemoveViewHandler = new Handler(Looper.getMainLooper());
        mRemoveViewRunnalbe = new Runnable() {
            @Override
            public void run() {
                removeFloatView(mContext);
            }
        };
        mRemoveViewHandler.postDelayed(mRemoveViewRunnalbe,4000);
    }

    public  void removeFloatView(Context context){
        mRemoveViewHandler.removeCallbacks(mRemoveViewRunnalbe);
        WindowManager mWindwwManager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        mWindwwManager.removeView(mFloatView);
    }

    public void requestAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    public boolean hasAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                return false;
            }
        }
        return true;
    }
}