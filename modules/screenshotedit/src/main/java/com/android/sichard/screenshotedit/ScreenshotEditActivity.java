package com.android.sichard.screenshotedit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.screenshotedit.view.ScreenshotCropView;
import com.android.sichard.screenshotedit.view.ScreenshotGraffitiEditView;
import com.android.sichard.screenshotedit.view.ScreenshotTextRootView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <br>类描述:截屏编辑主界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-3</b>
 */
public class ScreenshotEditActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * 主界面的根容器
     */
    private LinearLayout mRootContainer;
    /**
     * 截图编辑的主界面预览view
     */
    private LinearLayout mScreenshotEditPreview;
    /**
     * 预览ImageView的容器
     */
    private FrameLayout mContainerView;
    /**
     * 截屏预览的ImageView
     */
    private ImageView mScreenshotPreview;
    private Bitmap mBitmap;
    /** 是否进行过编辑 */
    private boolean mIsEdited = false;
    private LayoutInflater mInflater;
    //当前操作的 view，经常会被替换
    private IScreenshotEditView mScreenshotEditView;
    private ImageView mBtCrop, mBtText, mBtGraffiti, mBtShare;
    private ImageView mBtCancel, mBtOk;
    private String mSavePath;
    private final int REQUEST_CODE = 0x0001;
    private Dialog mSaveDialog;
    private boolean mSaveFromShare;
    /** 在保存图片时,用来判定是否已经保存过图片 */
    private Bitmap mOldBitmap;

    private enum SelectedStatu {
        None, Crop, Graffiti, Text, Shared
    }

    private SelectedStatu mStatu = SelectedStatu.None;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screenshot_edit);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(this);
        mRootContainer = findViewById(R.id.layout_container);
        mScreenshotEditPreview = findViewById(R.id.screenshot_edit_main);
        mContainerView = findViewById(R.id.screenshot_container);
        mScreenshotPreview = findViewById(R.id.screenshot_preview);
        String path = getIntent().getStringExtra(ScreenShotEditor.SCREENSHOT_PATH);
        mScreenshotPreview.setImageBitmap(getBitmap(path));
        mBtCrop = findViewById(R.id.screenshot_crop);
        mBtText = findViewById(R.id.screenshot_text);
        mBtGraffiti = findViewById(R.id.screenshot_graffiti);
        mBtShare = findViewById(R.id.screenshot_share);
        mBtCancel = findViewById(R.id.screenshot_edit_cancel);
        mBtOk = findViewById(R.id.screenshot_edit_ok);

        mBtCrop.setOnClickListener(this);
        mBtText.setOnClickListener(this);
        mBtGraffiti.setOnClickListener(this);
        mBtShare.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
        mBtOk.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionAssist.havePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mBitmap == null) {
            return;
        }
        if (view == mBtCrop) {
            if (mStatu != SelectedStatu.Crop) {
                reSetAllView();
                //设置 Crop 状态的界面
                mBtCrop.setImageResource(R.mipmap.screenshot_crop_selected);
                ScreenshotCropView screenshotCropView = (ScreenshotCropView) mInflater.inflate(R.layout.screenshot_crop, mContainerView, false);
                changeEditView(screenshotCropView);
                mStatu = SelectedStatu.Crop;
            }
        } else if (view == mBtGraffiti) {
            if (mStatu != SelectedStatu.Graffiti) {
                reSetAllView();
                //设置 Graffiti 状态的界面
                mScreenshotEditPreview.setVisibility(View.GONE);
                ScreenshotGraffitiEditView screenshotGraffitiEditView = (ScreenshotGraffitiEditView) mInflater.inflate(R.layout.screenshot_graffiti, mRootContainer, false);
                screenshotGraffitiEditView.setGraffitiViewSize(mContainerView.getWidth(), mContainerView.getHeight());
                changeEditViewWithColorBar(screenshotGraffitiEditView);
                mStatu = SelectedStatu.Graffiti;
            }
        } else if (view == mBtText) {
            if (mStatu != SelectedStatu.Text) {
                reSetAllView();
                //设置 Text 状态的界面
                mBtText.setImageResource(R.mipmap.screenshot_text_selected);
                ScreenshotTextRootView screenshotTextView = (ScreenshotTextRootView) mInflater.inflate(R.layout.screenshot_root_text, mContainerView, false);
                changeEditView(screenshotTextView);
                mStatu = SelectedStatu.Text;
            } else {
                //重复点击需要添加框
                if (mScreenshotEditView != null
                        && mScreenshotEditView instanceof ScreenshotTextRootView) {
                    ((ScreenshotTextRootView) mScreenshotEditView).addText();
                }
            }
        } else if (view == mBtShare) {
            if (mIsEdited && mOldBitmap != mBitmap) {
                mSaveFromShare = true;
                showSaveDialog();
                saveBitmapToSDCard(mBitmap);
            } else {
                shareBitmap();
            }
        } else if (view == mBtOk) {
            //此处代码逻辑复杂的主要原因是共用了编辑界面上面的ActionBar,后面可以用Fragment来承接业务重构代码
            if (mStatu == SelectedStatu.None) {
                if (mIsEdited && mOldBitmap != mBitmap) {
                    showSaveDialog();
                    saveBitmapToSDCard(mBitmap);
                } else {
                    String path = getResources().getString(R.string.screenshot_save_path, mSavePath);
                    Toast.makeText(ScreenshotEditActivity.this, path, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                mBitmap = mScreenshotEditView.getBitmap();
                //重置布局
                reSetAllView();
                mIsEdited = true;
            }
        } else if (view == mBtCancel) {
            if (mStatu == SelectedStatu.None) {
                if (mIsEdited) {
                    showExitDialog();
                } else {
                    finish();
                }
            } else {
                reSetAllView();
            }
        }
    }

    private void changeEditView(View view) {
        mBtCancel.setImageResource(R.mipmap.screenshot_edit_cancel);
        mScreenshotPreview.setVisibility(View.GONE);
        mContainerView.addView(view);
        mScreenshotEditView = (IScreenshotEditView) view;
        mScreenshotEditView.setBitmap(mBitmap);
    }

    private void changeEditViewWithColorBar(View view) {
        mBtCancel.setImageResource(R.mipmap.screenshot_edit_cancel);
        mScreenshotEditPreview.setVisibility(View.GONE);
        mRootContainer.addView(view);
        mScreenshotEditView = (IScreenshotEditView) view;
        mScreenshotEditView.setBitmap(mBitmap);
    }

    private Bitmap getBitmap(String filePath) {
        mSavePath = filePath;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            mBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    private void shareBitmap() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(mSavePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Share screen shot");
        startActivity(chooser);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.screenshotHintDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.screenshot_save_dialog, null);
        TextView textView = view.findViewById(R.id.screenshot_dialog_text);
        String text = getResources().getString(R.string.save_pic) + "...";
        textView.setText(text);
        builder.setView(view);
        mSaveDialog = builder.create();
        mSaveDialog.show();
    }

    private void showExitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.screenshotHintDialog);
        dialog.setMessage(R.string.screenshot_exit_edit);
        dialog.setPositiveButton(R.string.screenshot_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton(R.string.screenshot_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void saveBitmapToSDCard(final Bitmap bitmap) {
        mOldBitmap = bitmap;
        if (!mSavePath.contains("_edit")) {
            String path = getIntent().getStringExtra(ScreenShotEditor.SCREENSHOT_PATH);
            String[] split = path.split("\\u002E");
            if (split.length > 1) {
                mSavePath = split[0] + "_edit.png";
            }
        }

        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                FileOutputStream outStream = null;
                // 打开指定文件对应的输出流
                File file = new File(mSavePath);
                outStream = new FileOutputStream(file);
                // 把位图输出到指定文件中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (!(mSaveDialog == null)) {
                            mSaveDialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!(mSaveDialog == null)) {
                            mSaveDialog.dismiss();
                        }

                        if (mSaveFromShare) {
                            mSaveFromShare = false;
                            shareBitmap();
                        } else {
                            String path = getResources().getString(R.string.screenshot_save_path, mSavePath);
                            Toast.makeText(ScreenshotEditActivity.this, path, Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        //如果有在编辑转态重置view
        if (mStatu != SelectedStatu.None) {
            reSetAllView();
        } else {
            super.onBackPressed();
        }
    }

    //重置所有的View
    private void reSetAllView() {
        mStatu = SelectedStatu.None;
        mScreenshotPreview.setVisibility(View.VISIBLE);
        mScreenshotPreview.setImageBitmap(mBitmap);
        mScreenshotEditPreview.setVisibility(View.VISIBLE);
        mContainerView.removeView((View) mScreenshotEditView);
        mRootContainer.removeView((View) mScreenshotEditView);
        mBtCrop.setImageResource(R.mipmap.screenshot_crop);
        mBtText.setImageResource(R.mipmap.screenshot_text);
        mBtCancel.setImageResource(R.mipmap.screenshot_back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }
}
