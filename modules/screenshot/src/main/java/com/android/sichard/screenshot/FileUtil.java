package com.android.sichard.screenshot;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *<br>类描述：截图路径
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 18-5-23</b>
 */
public class FileUtil {

    //系统保存截图的路径
    private static final String SCREENCAPTURE_PATH = "ScreenCapture" + File.separator + "Screenshots" + File.separator;
    private static final String SCREENSHOT_NAME = "Screenshot";

    private static String getAppPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return context.getFilesDir().toString();
        }
    }

    private static String getScreenShots(Context context) {
        StringBuffer stringBuffer = new StringBuffer(getAppPath(context));
        stringBuffer.append(File.separator);
        stringBuffer.append(SCREENCAPTURE_PATH);
        File file = new File(stringBuffer.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return stringBuffer.toString();
    }

    public static String getScreenShotsName(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = simpleDateFormat.format(new Date());
        StringBuffer stringBuffer = new StringBuffer(getScreenShots(context));
        stringBuffer.append(SCREENSHOT_NAME);
        stringBuffer.append("_");
        stringBuffer.append(date);
        stringBuffer.append(".png");
        return stringBuffer.toString();
    }
}
