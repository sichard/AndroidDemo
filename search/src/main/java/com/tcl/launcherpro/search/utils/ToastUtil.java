package com.tcl.launcherpro.search.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * @author htoall
 * @Description:
 * @date 2016/11/9 下午8:15
 * @copyright TCL-MIE
 */
public class ToastUtil {
    public static Toast mToast;

    public static void show(Context context, @StringRes int id, int duration) {
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, id, duration);
        mToast.show();
    }

    public static void show(Context context, String str, int duration) {
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, str, duration);
        mToast.show();
    }
}
