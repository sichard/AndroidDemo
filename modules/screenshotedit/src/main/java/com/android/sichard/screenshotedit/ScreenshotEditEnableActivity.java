package com.android.sichard.screenshotedit;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.common.permission.PermissionConstant;

/**
 * <br>类描述:开启截图编辑的Activity
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-17</b>
 */
public class ScreenshotEditEnableActivity extends BaseActivity {
    private ScreenShotEditor mScreenShotEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_enable);
        if (PermissionAssist.havePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mScreenShotEditor = new ScreenShotEditor(this);
            mScreenShotEditor.registerScreenshotObserver();
        } else {
            PermissionAssist.newInstance(PermissionConstant.PERMISSION_READ_WRITE_STORAGE).requestPermission(this, new PermissionAssist.PermissionListener() {
                @Override
                public void onSuccess(int requestCode, String[] permissions) {
                    mScreenShotEditor = new ScreenShotEditor(ScreenshotEditEnableActivity.this);
                    mScreenShotEditor.registerScreenshotObserver();
                }

                @Override
                public void finish(int requestCode, String[] permissions) {

                }

                @Override
                public void onFailure(int requestCode, String[] permissions) {
                    ((TextView)findViewById(R.id.screenshot_hint)).setText("请允许读写SDCard权限");
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        FloatViewUtil floatViewUtil = new FloatViewUtil(getApplicationContext());
        if(!floatViewUtil.hasAlertWindowPermission()){
            floatViewUtil.requestAlertWindowPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionAssist.newInstance(PermissionConstant.PERMISSION_READ_WRITE_STORAGE).permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mScreenShotEditor != null) {
//            mScreenShotEditor.unregisterScreenshotObserver();
//        }
    }
}
