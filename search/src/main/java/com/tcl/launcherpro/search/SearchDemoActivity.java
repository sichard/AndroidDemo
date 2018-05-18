package com.tcl.launcherpro.search;

import android.app.Activity;
import android.os.Bundle;

public class SearchDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        SearchSDK.getInstance().checkPermission(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //此处没有回收资源是为了再次进入搜索时不用重新扫描联系人和短信信息
//        SearchSDK.getInstance().destroy();
    }
}
