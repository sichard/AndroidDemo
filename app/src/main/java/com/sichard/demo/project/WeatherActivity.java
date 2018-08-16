package com.sichard.demo.project;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.sichard.common.BaseActivity;
import com.android.sichard.common.framework.SingletonBase;
import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.common.permission.PermissionConstant;
import com.sichard.demo.R;
import com.sichard.weather.WeatherWidgetManager;

/**
 * <br>类描述:天气主界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-15</b>
 */
public class WeatherActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = PermissionAssist.queryPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET);
        if (permissions == null || permissions.length == 0) {
            setContentView(R.layout.weather_widget);
        } else {
            PermissionAssist.newInstance(PermissionConstant.PERMISSION_ALL).requestPermission(this, new PermissionAssist.PermissionListener() {
                @Override
                public void onSuccess(int requestCode, String[] permissions) {
                    setContentView(R.layout.weather_widget);
                }

                @Override
                public void finish(int requestCode, String[] permissions) {

                }

                @Override
                public void onFailure(int requestCode, String[] permissions) {
                    Toast.makeText(WeatherActivity.this, "请允许定位权限,以便获取天气", Toast.LENGTH_SHORT).show();
                }
            }, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionAssist.newInstance(PermissionConstant.PERMISSION_ALL).permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonBase.destroy(WeatherWidgetManager.getsInstance());
    }
}
