package com.android.sichard.screenshotedit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.sichard.screenshotedit.R;
import com.android.sichard.screenshotedit.util.MultiTouchListener;

import java.util.ArrayList;
import java.util.List;


public class ScreenshotTextView extends RelativeLayout {

    private static final String TAG = "PhotoEditorView";
    private AppCompatImageView mImgSource;
    private static final int imgSrcId = 1;

    private boolean isTextPinchZoomable = true;
    private List<View> addedViews;
    private LayoutInflater mLayoutInflater;

    public enum ViewType {
        TEXT,
        IMAGE,
        EMOJI
    }


    public ScreenshotTextView(Context context) {
        super(context);
        init(null);
    }

    public ScreenshotTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScreenshotTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScreenshotTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addedViews = new ArrayList<>();

        mImgSource = new AppCompatImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImgSource.setAdjustViewBounds(true);
        LayoutParams imgSrcParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //Add image source
        addView(mImgSource, imgSrcParam);
    }

    //设置背景
    public void setImgSrc(Bitmap bitmap) {
        Drawable mDrawable;
        Bitmap preBitmap;
        //回收上一个 bitmap
        if ((mDrawable = mImgSource.getDrawable()) != null
                && mDrawable instanceof BitmapDrawable
                && (preBitmap = ((BitmapDrawable) mDrawable).getBitmap()) != null
                && !preBitmap.isRecycled()
                && preBitmap != bitmap) {
            preBitmap.recycle();
        }
        mImgSource.setImageBitmap(bitmap);
    }

    /**
     * 获取绘制结果
     *
     * @return
     */
    public Bitmap getDrawResults() {
        return ((BitmapDrawable) mImgSource.getDrawable()).getBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * 复写 本photoeditorView 的大小（和拉伸后的 imageVIEW SIZE 一样）
         * 并把size 传到 childView 去做 measure 。
         * 不能把 没有通过计算得出的 parentsize 传到 child view 去做mesure。
         * 这样子会引起 child view 在draw 内容的时候错位。具体原因不明
         */

        int realW = MeasureSpec.getSize(widthMeasureSpec);
        int realH = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        Drawable drawable = mImgSource.getDrawable();
        int imgW = drawable.getIntrinsicWidth();
        int imgH = drawable.getIntrinsicHeight();


        if (realW * 1.0f / imgW >= realH * 1.0f / imgH) {
            realW = (int) (imgW * realH * 1.0f / imgH);
        } else {
            realH = (int) (imgH * realW * 1.0f / imgW);
        }

        int newWidthSpec = MeasureSpec.makeMeasureSpec(realW, widthMode);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(realH, heightMode);

        super.onMeasure(newWidthSpec, newHeightSpec);
    }

    public void addText(String text, final int colorInt) {
        addText(null, text, colorInt);
    }


    public void addText(@Nullable Typeface textTypeface, final String text, final int colorInt) {
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        textInputTv.setTextColor(colorInt);
        if (textTypeface != null) {
            textInputTv.setTypeface(textTypeface);
        }
        MultiTouchListener multiTouchListener = getMultiTouchListener();


        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
//                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
//                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.screenshot_text_tv_rounded_border);
//                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
//                frmBorder.setTag(!isBackgroundVisible);
                showDialog(textRootView, textInputTv.getText().toString(), textInputTv.getCurrentTextColor());
            }

            @Override
            public void onLongClick() {
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);

        showDialog(textRootView, textInputTv.getText().toString(), textInputTv.getCurrentTextColor());


    }

    private void showDialog(final View textRootView, String textInput, int currentTextColor) {


        ScreenshotTextDialog textEditorDialogFragment = ScreenshotTextDialog.show((FragmentActivity) getContext()
                , textInput, currentTextColor);
        textEditorDialogFragment.setOnTextEditorListener(new ScreenshotTextDialog.TextEditorListener() {

            @Override
            public void onDone(String inputText, int colorCode) {
                editText(textRootView, inputText, colorCode);
            }

            @Override
            public void onCancle() {
                viewUndo(textRootView);
            }

        });
    }


    public void editText(View view, String inputText, int colorCode) {
        editText(view, null, inputText, colorCode);
    }


    public void editText(View view, Typeface textTypeface, String inputText, int colorCode) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (textTypeface != null) {
                inputTextView.setTypeface(textTypeface);
            }
            inputTextView.setTextColor(colorCode);
            updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    private void addViewToParent(View rootView, ViewType viewType) {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(rootView, params);
        addedViews.add(rootView);
    }

    private MultiTouchListener getMultiTouchListener() {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                null,
                this,
                this.mImgSource,
                isTextPinchZoomable,
                null);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    private View getLayout(ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.screenshot_edittext, null);
                break;
        }

        if (rootView != null) {
            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView);
                    }
                });
            }
        }
        return rootView;
    }

    private void viewUndo(View removedView) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                removeView(removedView);
                addedViews.remove(removedView);
            }
        }
    }


    public Bitmap getBitmap() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
        // 此处使用线程池得不偿失
        setDrawingCacheEnabled(true);
        //copy 一个位图，因为 imageView 被remove from parent ，会回收 iamgeView 的 bitmap
        return Bitmap.createBitmap(getDrawingCache());
    }
}
