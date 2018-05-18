package com.android.sichard.search.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 *<br>类描述：Toast工具
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 18-5-18</b>
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
