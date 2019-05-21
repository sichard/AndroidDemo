package com.sichard.demo.view.recyclerview.viewpager;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@link SmallCardView}的横向滚动指示器
 */
public class BigCardIndicatorDecoration extends RecyclerView.ItemDecoration {

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    /**
     * 指示器底部空间的高度
     */
    private final int mIndicatorHeight = (int) (DP * 40);

    /**
     * 指示器的长度
     */
    private final float mIndicatorLength = DP * 200;

    private final Paint mPaint = new Paint();
    private float mIndicatorStartX;
    private float mIndicatorPosY;

    BigCardIndicatorDecoration() {
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 指示器笔触宽度
        float indicatorStrokeWidth = DP * 4;
        mPaint.setStrokeWidth(indicatorStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // 水平居中，计算指示器的开始位置
        if (mIndicatorStartX == 0) {
            mIndicatorStartX = (parent.getWidth() - mIndicatorLength) / 2F;
        }

        // 垂直居中
        if (mIndicatorPosY == 0) {
            mIndicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;
        }

        // 绘制指示器的长度
        drawInactiveIndicators(c, mIndicatorStartX, mIndicatorPosY);

        // 找出当前第一个可见的Item的索引
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findFirstVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        // 查找当前进入视野的view
        final View activeChild = layoutManager.findViewByPosition(activePosition);
        int left = activeChild.getLeft();
        int width = activeChild.getWidth();

        // 计算进入视野的View的位移, left的变化范围是[0, -width]
        float progress = left * -1 / (float) width;

        // 计算每个item对应的长度
        float itemLength = mIndicatorLength / itemCount;
        drawHighlights(c, mIndicatorStartX, mIndicatorPosY, activePosition, progress, itemLength);
    }

    /**
     * 绘制指示器的长度
     * @param c 画布
     * @param indicatorStartX 指示器的起始位置
     * @param indicatorPosY 指示器的y坐标
     */
    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY) {
        int colorInactive = 0x66FFFFFF;
        mPaint.setColor(colorInactive);
        c.drawLine(indicatorStartX, indicatorPosY, indicatorStartX + mIndicatorLength, indicatorPosY, mPaint);
    }

    /**
     * 绘制当前滚动位置的指示
     * @param c 画布
     * @param indicatorStartX 指示器的起始位置
     * @param indicatorPosY 指示器的y坐标
     * @param highlightPosition 当前可见的第一个item的所依
     * @param progress 滚动的进度
     * @param itemLength item在指示器中占用的长度
     */
    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY, int highlightPosition,
                                float progress, float itemLength) {
        int colorActive = 0xFFFFFFFF;
        mPaint.setColor(colorActive);
        int noScroller = 0;
        if (progress == noScroller) {
            float highlightStart = indicatorStartX + itemLength * highlightPosition;
            c.drawLine(highlightStart, indicatorPosY, highlightStart + itemLength, indicatorPosY, mPaint);
        } else {
            float highlightStart = indicatorStartX + itemLength * highlightPosition;
            float partialLength = itemLength * progress;
            c.drawLine(highlightStart + partialLength, indicatorPosY,
                    highlightStart + partialLength + itemLength, indicatorPosY, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}
