package com.android.sichard.screenshotedit.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.sichard.screenshotedit.R;


/**
 * Created by jiong  2018.8.10
 */

public class ScreenshotTextDialog extends DialogFragment implements View.OnClickListener {

    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";

    private TextEditorListener mTextEditorListener;
    private EditText mEditInput;
    private InputMethodManager mInputMethodManager;
    private int colorInt;
    private String inputString;
    private ScreenshotColorView mTextBar;
    private ImageView mTvCancle, mTvDone;


    public interface TextEditorListener {
        void onDone(String inputText, int colorCode);

        void onCancle();
    }

    public void setOnTextEditorListener(TextEditorListener textEditor) {
        mTextEditorListener = textEditor;
    }

    public static ScreenshotTextDialog show(@NonNull FragmentActivity appCompatActivity,
                                            @NonNull String inputText,
                                            @ColorInt int colorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        ScreenshotTextDialog fragment = new ScreenshotTextDialog();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), ScreenshotTextDialog.class.getSimpleName());
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
//        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screenshot_dialog_add_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //键盘能顶起布局
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        inputString = getArguments().getString(EXTRA_INPUT_TEXT);
        colorInt = getArguments().getInt(EXTRA_COLOR_CODE);


        mTvDone = view.findViewById(R.id.screenshot_edit_cancel);
        mTvCancle = view.findViewById(R.id.screenshot_edit_ok);
        mTvDone.setOnClickListener(this);
        mTvCancle.setOnClickListener(this);
        view.findViewById(R.id.screenshot_edit_action).setVisibility(View.GONE);

        mEditInput = view.findViewById(R.id.add_text_edit_text);
        mEditInput.setText(inputString);
        mEditInput.setTextColor(colorInt);


        mTextBar = view.findViewById(R.id.screenshot_text_bar);
        mTextBar.setBackgroundColor(Color.TRANSPARENT);
        mTextBar.setColorChangedListener(new ScreenshotColorView.ColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                colorInt = color;
                if (color == 0xFF000000) {
                    mEditInput.setTextColor(0xFF262626);
                } else {
                    mEditInput.setTextColor(color);
                }
            }
        });
        //弹出键盘
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mEditInput.requestFocus();

    }


    @Override
    public void onClick(View v) {
        if (v == mTvCancle) {
            mInputMethodManager.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
            dismiss();
            String inputText = mEditInput.getText().toString();
            if (!TextUtils.isEmpty(inputText) && mTextEditorListener != null) {
                mTextEditorListener.onDone(inputText, colorInt);
            }
        } else if (v == mTvDone) {
            mInputMethodManager.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
            dismiss();
            if (mTextEditorListener != null) {
                mTextEditorListener.onCancle();
            }
        }
    }
}
