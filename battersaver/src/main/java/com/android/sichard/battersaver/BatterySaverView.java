package com.android.sichard.battersaver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * <br>类描述:省电容器view
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-2</b>
 */

public class BatterySaverView extends LinearLayout implements BatterySaverCleanView.BatterySaverProgressListener {
    private BatterySaverCleanView mBatterySaverView;
    private TextView mProgress, mPercent;
    private LinearLayout mIcons;
    private List<Drawable> mDrawables;
    private boolean mIsTranslateAnimation;
    /**
     * 动画时间临界点
     */
    private float mCriticalValue = 0.15f;

    public BatterySaverView(@NonNull Context context) {
        super(context);
        initData();
    }

    public BatterySaverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public BatterySaverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        mDrawables = new ArrayList<>();
        Random random = new Random();
        PackageManager pkgManager = getContext().getPackageManager();
        List<ResolveInfo> appList = getAppList(getContext());
        if (appList.size() > 4) {
            HashSet<Integer> hashSet = new HashSet<>();
            // 随机抽取四个
            while (hashSet.size() < 4) {
                hashSet.add((int) (appList.size() * random.nextFloat()));
            }
            Object[] objects = hashSet.toArray();
            for (int i = 0; i < 4; i++) {
                ResolveInfo resolveInfo = appList.get((Integer) objects[i]);
                Drawable drawable = resolveInfo.loadIcon(pkgManager);
                if (drawable == null) {
                    drawable = pkgManager.getDefaultActivityIcon();
                }
                mDrawables.add(drawable);
            }
        } else {
            for (int i = 0; i < appList.size(); i++) {
                ResolveInfo resolveInfo = appList.get(i);
                Drawable drawable = resolveInfo.loadIcon(pkgManager);
                if (drawable == null) {
                    drawable = pkgManager.getDefaultActivityIcon();
                }
                mDrawables.add(drawable);
            }
        }
    }

    /**
     * <br>功能简述:获取系统所有启动app
     * <br>功能详细描述:通过PackageManager查询获取
     * <br>注意:
     *
     * @param context
     * @return
     */
    private List<ResolveInfo> getAppList(Context context) {
        PackageManager pkgManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // ResolveInfo类:根据<intent>节点来获取其上一层目录的信息，通常是<activity>、<receiver>、<service>节点信息。
        List<ResolveInfo> appList = pkgManager.queryIntentActivities(mainIntent, 0);
        return appList;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBatterySaverView = findViewById(R.id.battery_saver_clean_view);
        mBatterySaverView.setBatterySaverProgressListener(this);
        mProgress = findViewById(R.id.battery_saver_progress);
        mPercent = findViewById(R.id.battery_saver_progress_percent);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mBatterySaverView.startCleanAnim();
            }
        }, 150);
        mIcons = (LinearLayout) findViewById(R.id.battery_saver_icons);
        mIcons.setAlpha(0);
        for (int i = 0; i < mDrawables.size(); i++) {
            ((ImageView) (mIcons.getChildAt(i))).setImageDrawable(mDrawables.get(i));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void batterySaverProgressChanged(float progress) {
        mProgress.setText((int) (progress * 100) + "");
        float progressTextSize = getResources().getDimension(R.dimen.battery_clean_progress);
        float percentTextSize = getResources().getDimension(R.dimen.battery_clean_percent);
        mProgress.setTextSize(TypedValue.COMPLEX_UNIT_PX, progressTextSize - progressTextSize * progress);
        mPercent.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentTextSize - percentTextSize * progress);
        if (progress <= mCriticalValue) {
            mIcons.setAlpha(progress / mCriticalValue);
        } else {
            mIcons.setAlpha(1 - (progress - mCriticalValue) / 0.7f);
            if (!mIsTranslateAnimation) {
                mIsTranslateAnimation = true;
                float dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, dimension);
                translateAnimation.setDuration(2000);
                translateAnimation.setFillAfter(true);
                mIcons.startAnimation(translateAnimation);
            }
        }
    }

    public void setBatterySaverFinishListener(BatterySaverCleanView.BatterySaverFinishListener finishListener) {
        if (mBatterySaverView != null) {
            mBatterySaverView.setBatterySaverFinishListener(finishListener);
        }
    }

    public void onDestroy() {
        if (mBatterySaverView != null) {
            mBatterySaverView.onDestroy();
        }

        if (mDrawables != null && !mDrawables.isEmpty()) {
            mDrawables.clear();
        }
    }
}
