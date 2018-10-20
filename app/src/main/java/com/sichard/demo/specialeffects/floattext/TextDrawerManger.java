package com.sichard.demo.specialeffects.floattext;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Text绘制的管理者
 */
public class TextDrawerManger {
    private List<Text> mTextList = new ArrayList<>();


    public TextDrawerManger() {

    }


    public void setViewSize(int width, int height) {
        initTextList(width, height);
    }

    private void initTextList(int width, int height) {
        mTextList.add(new Text((int) (0.4f * width), height / 2, 50, 1000));
        mTextList.add(new Text((int) (0.1f * width), (int) (0.3f * height), 40, 1200));
        mTextList.add(new Text((int) (0.6f * width), (int) (0.4f * height), 40, 800));
        mTextList.add(new Text((int) (0.2f * width), (int) (0.7f * height), 30, 1200));
        mTextList.add(new Text((int) (0.7f * width), (int) (0.8f * height), 30, 1600));
    }

    public void drawText(Canvas canvas) {
        for (int i = 0; i < mTextList.size(); i++) {
            mTextList.get(i).draw(canvas);
        }
    }

    public void setData(ArrayList<String> textList, Bitmap bitmap) {
        for (int i = 0; i < textList.size(); i++) {
            mTextList.get(i).setText(textList.get(i));
            mTextList.get(i).setBitmap(bitmap);
        }
    }
}
