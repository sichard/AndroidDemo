package com.android.sichard.search.view;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sichard.search.R;
import com.android.sichard.search.data.hotSite.HotSiteInfo;
import com.android.sichard.search.data.hotgame.HotGameInfo;
import com.android.sichard.search.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:热点网站view
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/7</b>
 */

public class HotSiteView extends ViewGroup {

    private int mSpaceHeight = DrawUtils.dip2px(10);
    private SearchWebView mSearchWebView;

    public HotSiteView(Context context) {
        super(context);
    }

    public HotSiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HotSiteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int childWidth = (widthSize - paddingLeft - paddingRight) / 5;
        final int childHeight = (int) getResources().getDimension(R.dimen.search_app_item_view_height);

        int row = 0;
        // 防止刚好是5的整数倍时，会多出一行的空间。
        if (count >= 5 && count % 5 == 0) {
            row = count / 5;
        } else {
            row = count / 5 + 1;
        }

        int heightSize = row * childHeight + row * mSpaceHeight;
        setMeasuredDimension(widthSize, heightSize);
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            int startLeft = paddingLeft + i % 5 * width;
            int startTop = i / 5 * height + i / 5 * mSpaceHeight;
            child.layout(startLeft, startTop, startLeft + width, startTop + height);
        }
    }

    public void setHotSiteList(List<HotSiteInfo> appList) {
        ArrayList<HotSiteInfo> tempList = new ArrayList<>(appList);
        for (int i = 0; i < tempList.size(); i++) {
            final HotSiteInfo hotSiteInfo = tempList.get(i);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            HotSiteItemView appView = (HotSiteItemView) inflater.inflate(R.layout.hot_site_item_view, this, false);
            appView.setIcon(hotSiteInfo.mIcon);

            //文字。关键字高亮
            SpannableString string = new SpannableString(hotSiteInfo.mTitle);
            appView.setTitle(string);
            appView.setTag(hotSiteInfo);
            appView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果搜索WebView为空，则启动浏览器加载网页
                    if (mSearchWebView == null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(hotSiteInfo.mUrl);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            try {
                                // 对特殊机型进行ship
                                intent = new Intent();
                                intent.setComponent(new ComponentName("com.lenovo.browser", "com.lenovo.browser.LeMainActivity"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        mSearchWebView.loadUrl(hotSiteInfo.mUrl);
                    }
                }
            });
            addView(appView);
        }
    }

    public void setGameList(List<HotGameInfo> appList) {
        ArrayList<HotGameInfo> tempList = new ArrayList<>(appList);
        for (int i = 0; i < tempList.size(); i++) {
            final HotGameInfo hotGameInfo = tempList.get(i);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            HotSiteItemView appView = (HotSiteItemView) inflater.inflate(R.layout.hot_site_item_view, this, false);
            appView.setIcon(hotGameInfo.mIcon);

            //文字。关键字高亮
            SpannableString string = new SpannableString(hotGameInfo.mTitle);
            appView.setTitle(string);
            appView.setTag(hotGameInfo);
            appView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果搜索WebView为空，则启动浏览器加载网页
//                    if (mSearchWebView == null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(hotGameInfo.mUrl);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            try {
                                // 对特殊机型进行ship
                                intent = new Intent();
                                intent.setComponent(new ComponentName("com.lenovo.browser", "com.lenovo.browser.LeMainActivity"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }
//                    } else {
//                        mSearchWebView.loadUrl(hotGameInfo.mUrl);
//                    }
                }
            });
            addView(appView);
        }
    }

    /**
     * 设置搜索WebView
     * @param searchWebView
     */
    public void setSearchWebView(SearchWebView searchWebView) {
        this.mSearchWebView = searchWebView;
    }
}
