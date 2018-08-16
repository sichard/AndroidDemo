package com.android.sichard.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.android.sichard.common.permission.PermissionAssist;

public class SearchDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        SearchSDK.getInstance().checkPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionAssist.newInstance(requestCode).permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //此处没有回收资源是为了再次进入搜索时不用重新扫描联系人和短信信息
//        SearchSDK.getInstance().destroy();
    }
}
