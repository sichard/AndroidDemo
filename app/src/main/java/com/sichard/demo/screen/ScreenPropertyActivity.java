package com.sichard.demo.screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;
import com.sichard.demo.databinding.ScreenPropertyBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScreenPropertyActivity extends BaseActivity {
    private ScreenPropertyBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_property);
        test();
    }

    private void test() {

        //屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        DisplayMetrics realMetric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        getWindowManager().getDefaultDisplay().getRealMetrics(realMetric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //媒体音量
        assert audioManager != null;
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        StringBuilder sb = new StringBuilder();
        sb.append("width:");
        sb.append(width);
        sb.append("\n");
        sb.append("height:");
        sb.append(height);
        sb.append("\n");
        sb.append("realHeight:");
        sb.append(realMetric.heightPixels);
        sb.append("\n");
        sb.append("density:");
        sb.append(density);
        sb.append("\n");
        sb.append("densityDpi:");
        sb.append(densityDpi);
        String dpiString;
        if (densityDpi / 160f < 1) {
            dpiString = String.valueOf(densityDpi / 160f);
        } else if (densityDpi / 160f == 1.5f) {
            dpiString = "hdpi";
        } else {
            switch (densityDpi / 160) {
                case 1:
                    dpiString = "mdpi";
                    break;
                case 2:
                    dpiString = "xhdpi";
                    break;
                case 3:
                    dpiString = "xxdpi";
                    break;
                case 4:
                    dpiString = "xxxdpi";
                    break;
                default:
                    dpiString = "未知";
            }
        }
        sb.append(" = ").append(dpiString);
        sb.append("\n");
        sb.append("Model:");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("DEVICE:");
        sb.append(Build.DEVICE);
        sb.append("\n");
        sb.append("ID:");
        sb.append(Build.ID);
        sb.append("\n");
        sb.append("BRAND:");
        sb.append(Build.BRAND);
        sb.append("\n");
        sb.append("AndroidId:");
        sb.append(getAndroidID(this));
        sb.append("\n");
        sb.append("Battery Capacity:");
        sb.append(getBatteryCapacity());
        sb.append("\n");
        sb.append("Stream Volume(current/max):");
        sb.append(current).append("/").append(max);
        sb.append("\n");
        sb.append("Version/API:");
        sb.append(Build.VERSION.RELEASE).append("/").append(Build.VERSION.SDK_INT);
        mDataBinding.textView.setText(sb.toString());

        createJson();

        getDefaultHome();

        testCalender();
    }

    private String getAndroidID(Context context) {
        @SuppressLint("HardwareIds")
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return androidId;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void createJson() {
        JSONArray array = new JSONArray();

        for (int i = 0; i < 5; i++) {
            JSONObject object = new JSONObject();
            try {
                object.putOpt("test", i + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }

        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                if (i != 2) {
                    JSONObject object = (JSONObject) array.get(i);
                    list.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        array = new JSONArray(list);
        Log.i("sichard", "ScreenPropertyActivity|createJson:" + "length:" + array.length());
        Log.i("sichard", "ScreenPropertyActivity|createJson:" + array.toString());
    }

    private void getDefaultHome() {
        PackageManager pkgManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo ri = pkgManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY
                | PackageManager.GET_SHARED_LIBRARY_FILES);
        Log.i("sichardcao", "ScreenPropertyActivity|getDefaultHome:" + ri.activityInfo.applicationInfo.packageName);
    }

    @SuppressLint("PrivateApi")
    private Double getBatteryCapacity() {
        Object mPowerProfile_ = null;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            // Class not found?
            e.printStackTrace();
        }

        try {
            // Invoke PowerProfile method "getAveragePower" with param "battery.capacity"
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            // Something went wrong
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    private void testCalender() {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int interval = calendar.get(Calendar.HOUR_OF_DAY) * 3600 * 1000 + calendar.get(Calendar.MINUTE) * 60 * 1000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
        long zero = now - interval;
        Log.i("sichard", "ScreenPropertyActivity|testCalender:" + "now:" + now);
        Log.i("sichard", "ScreenPropertyActivity|testCalender:" + "interval:" + interval);
        Log.i("sichard", "ScreenPropertyActivity|testCalender:" + "zero:" + zero);
    }
}
