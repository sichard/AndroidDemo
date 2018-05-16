package com.sichard.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * <br>类描述:
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/15</b>
 */

public class WeatherHorizontalScrollView extends HorizontalScrollView {
    public WeatherHorizontalScrollView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);

    }

    public WeatherHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
    }

    public WeatherHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHorizontalScrollBarEnabled(false);
    }
}
