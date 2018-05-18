package com.tcl.launcherpro.search.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.launcherpro.search.R;

/**
 * <br>类描述:搜索App结果的ItemView
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-9</b>
 */
public class SearchAppItemView extends LinearLayout {
    private ImageView mIcon;
    private TextView mTitle;
    public SearchAppItemView(Context context) {
        super(context);
    }

    public SearchAppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchAppItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.imageView);
        mTitle = (TextView) findViewById(R.id.textView);
    }

    public void setIcon (Drawable drawable) {
        if (drawable != null) {
            mIcon.setImageDrawable(drawable);
        }
    }

    public void setTitle (SpannableString title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }
}
