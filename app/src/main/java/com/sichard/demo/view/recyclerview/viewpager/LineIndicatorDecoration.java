package com.sichard.demo.view.recyclerview.viewpager;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@link SmallCardView}的横向滚动指示器
 */
public class LineIndicatorDecoration extends RecyclerView.ItemDecoration {

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    LineIndicatorDecoration() {
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        if (position == 0) {
            outRect.left = (int) (20 * DP);
            outRect.right = (int) (10 * DP);
        } else if (position == state.getItemCount() - 1) {
            outRect.right = (int) (20 * DP);
        } else {
            outRect.right = (int) (10 * DP);
        }
    }
}
