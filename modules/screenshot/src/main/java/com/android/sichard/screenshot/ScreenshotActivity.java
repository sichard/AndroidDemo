package com.android.sichard.screenshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.common.permission.PermissionConstant;

public class ScreenshotActivity extends FragmentActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 2;
    private boolean mHaveOverlayPermission;
    private boolean mHaveCapturePermission;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSdCardPermission();
        requestCapturePermission();
        requestOverlayPermission();
    }


    private void requestSdCardPermission() {
        PermissionAssist.newInstance(PermissionConstant.PERMISSION_READ_WRITE_STORAGE).requestPermission(this, new PermissionAssist.PermissionListener() {
            @Override
            public void onSuccess(int requestCode, String[] permissions) {
            }

            @Override
            public void finish(int requestCode, String[] permissions) {

            }

            @Override
            public void onFailure(int requestCode, String[] permissions) {
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public void requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            return;
        }

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            mHaveOverlayPermission = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    mHaveCapturePermission = true;
                    FloatWindowsService.setResultData(data);
                }
                break;
            case OVERLAY_PERMISSION_REQ_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    mHaveOverlayPermission = true;
                }
                break;
        }
        if (mHaveCapturePermission && mHaveOverlayPermission) {
            startService(new Intent(getApplicationContext(), FloatWindowsService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionAssist.newInstance(PermissionConstant.PERMISSION_READ_WRITE_STORAGE).permissionResult(requestCode, permissions, grantResults);
    }
}
