package com.android.sichard.search.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sichard.common.utils.DrawUtils;
import com.android.sichard.search.R;
import com.android.sichard.search.SearchSDK;
import com.android.sichard.search.data.App.AppInfo;
import com.android.sichard.search.data.ISearchItem;
import com.android.sichard.search.recent.IRecentTask;
import com.android.sichard.search.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:搜索app结果的展示View
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-9</b>
 */
public class SearchAppView extends ViewGroup {

    boolean RecentAppView = false;
    private int mSpaceHeight = DrawUtils.dip2px(14);
    public SearchAppView(Context context) {
        super(context);
    }

    public SearchAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchAppView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        // 防止刚好是4的整数倍时，会多出一行的空间。
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

            int startLeft = i % 5 * width + paddingLeft;
            int startTop = i / 5 * height + i / 5 * mSpaceHeight;
            child.layout(startLeft, startTop, startLeft + width, startTop + height);
        }
    }

    public void setChildList(List<AppInfo> appList , boolean fromRecentAppView) {
        this.RecentAppView = fromRecentAppView;
        ArrayList<AppInfo> tempList = new ArrayList<>(appList);
        for (int i = 0; i < tempList.size(); i++) {
            final AppInfo appInfo = tempList.get(i);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            SearchAppItemView appView = (SearchAppItemView) inflater.inflate(R.layout.search_app_item_view, this, false);
            appView.setIcon(appInfo.getIcon());

            //文字。关键字高亮
            SpannableString string = new SpannableString(appInfo.getTitle());
            if(!RecentAppView){
                if (appInfo.mMatchResult.mMatchWords != 0) {
                    string.setSpan(new ForegroundColorSpan(ISearchItem.HIGH_LIGHT_COLOR), appInfo.mMatchResult.mStart, appInfo.mMatchResult.mStart
                            + appInfo.mMatchResult.mMatchWords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            appView.setTitle(string);
            appView.setTag(appInfo);
            appView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = appInfo.getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    if (null == intent) {
                        intent = AppUtil.createLaunchIntent(appInfo.getComponentName());
                    }

                    try {
                        if (SearchSDK.getInstance().getSearchAction() != null) {
                            SearchSDK.getInstance().getSearchAction().onAppOpen(intent);
                        } else {
                            getContext().startActivity(intent);
                        }
                        IRecentTask recentTask = SearchSDK.getInstance().getRecentTask();
                        if (recentTask != null) {
                            recentTask.saveRecent(appInfo.getComponentName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            addView(appView);
        }
    }
}
