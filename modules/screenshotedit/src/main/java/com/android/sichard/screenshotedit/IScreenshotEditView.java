package com.android.sichard.screenshotedit;

import android.graphics.Bitmap;

/**
 * <br>类描述:截屏编辑view需要继承的接口
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-7</b>
 */
public interface IScreenshotEditView {
    /**
     * 设置bitmap
     * @param bitmap 要编辑的bitmap
     */
    void setBitmap(Bitmap bitmap);

    /**
     * 获取编辑后的bitmap
     * @return
     */
    Bitmap getBitmap();
}
