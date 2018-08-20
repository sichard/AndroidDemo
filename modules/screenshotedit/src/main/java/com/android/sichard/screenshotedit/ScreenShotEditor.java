package com.android.sichard.screenshotedit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * <br>类描述:截屏编辑控制类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-7-31</b>
 */
public class ScreenShotEditor {
    private static final String EXTERNAL_CONTENT_URI_MATCHER = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED};
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    public static final String SCREENSHOT_PATH = "screenshot_path";
    private final Activity mActivity;
    private ContentObserver mContentObserver;

    public ScreenShotEditor(Activity activity) {
        mActivity = activity;
    }

    public void registerScreenshotObserver() {
        final ContentResolver contentResolver = mActivity.getContentResolver();
        mContentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (uri.toString().contains(EXTERNAL_CONTENT_URI_MATCHER)) {
                    Cursor cursor = null;
                    try {
                        cursor = contentResolver.query(uri, PROJECTION, null, null, SORT_ORDER);
                        if (cursor != null && cursor.moveToFirst()) {
                            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                            long currentTime = System.currentTimeMillis() / 1000;
                            if ((path.toLowerCase().contains("screenshot") || path.toLowerCase().contains("截屏")) &&
                                    Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS) {

                                // 收到CREATE事件后马上获取并不能获取到，需要延迟一段时间
//                                int tryTimes = 0;
//                                while (true) {
//                                    try {
//                                        Thread.sleep(600);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    try {
//                                        BitmapFactory.decodeFile(path);
//                                        break;
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        tryTimes++;
//                                        if (tryTimes >= 2) { // 尝试MAX_TRYS次失败后，放弃
//                                            return;
//                                        }
//                                    }
//                                }

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        final FloatViewUtil floatViewUtil = new FloatViewUtil(mActivity.getApplicationContext());
                                        if(!floatViewUtil.hasAlertWindowPermission()){
                                            floatViewUtil.requestAlertWindowPermission();
                                        }else {
                                            Drawable d = Drawable.createFromPath(path);
                                            LayoutInflater inflater = LayoutInflater.from(mActivity.getApplicationContext());
                                            View v = inflater.inflate(R.layout.screen_shot_thambnail, null);
                                            ImageView img = v.findViewById(R.id.screen_shot_thanmbnail_img);
                                            img.setBackground(d);
                                            v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    floatViewUtil.removeFloatView(mActivity.getApplicationContext());
                                                    Intent intent = new Intent(mActivity, ScreenshotEditActivity.class);
                                                    intent.putExtra(SCREENSHOT_PATH, path);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mActivity.startActivity(intent);
                                                }
                                            });
                                            floatViewUtil.addFloatView(v);
                                        }
                                    }
                                });

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                super.onChange(selfChange, uri);
            }
        };
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mContentObserver);
    }

    public void unregisterScreenshotObserver() {
        if (mContentObserver != null) {
            mActivity.getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }
}
