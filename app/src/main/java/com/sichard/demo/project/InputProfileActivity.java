package com.sichard.demo.project;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.project.profilegpurendering.InputProfileView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2019-7-18
 */
public class InputProfileActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.profile_container)
    ViewGroup mContainerView;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.inputProfileView)
    InputProfileView mProfileView;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_input)
    Switch mInputSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_measure)
    Switch mMeasureSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_draw)
    Switch mDrawSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_upload)
    Switch mUploadSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_issue)
    Switch mIssueSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_swap)
    Switch mSwapSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_anim)
    Switch mAnimSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_miscellaneous)
    Switch mMiscellaneousSwitch;
    @BindView(com.sichard.demo.project.profilegpurendering.R.id.gpu_draw_point)
    Switch mDrawPointSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sichard.demo.project.profilegpurendering.R.layout.layout_profile_input);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mInputSwitch.setOnCheckedChangeListener(this);
        mMeasureSwitch.setOnCheckedChangeListener(this);
        mDrawSwitch.setOnCheckedChangeListener(this);
        mUploadSwitch.setOnCheckedChangeListener(this);
        mIssueSwitch.setOnCheckedChangeListener(this);
        mSwapSwitch.setOnCheckedChangeListener(this);
        mAnimSwitch.setOnCheckedChangeListener(this);
        mMiscellaneousSwitch.setOnCheckedChangeListener(this);
        mDrawPointSwitch.setOnCheckedChangeListener(this);
    }

    @OnClick({com.sichard.demo.project.profilegpurendering.R.id.profile_container})
    public void onViewClicked() {
        mProfileView.refreshCurrentTime();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mInputSwitch) {
            mProfileView.enableInput(isChecked);
        } else if (buttonView == mMeasureSwitch) {
            mProfileView.enableMeasure(isChecked);
        } else if (buttonView == mDrawSwitch) {
            mProfileView.enableDraw(isChecked);
        } else if (buttonView == mUploadSwitch) {
            mProfileView.enableUpload(isChecked);
        } else if (buttonView == mIssueSwitch) {
            mProfileView.enableIssue(isChecked);
        } else if (buttonView == mSwapSwitch) {
            mProfileView.enableMiscellaneous(isChecked);
        } else if (buttonView == mAnimSwitch) {
            if (isChecked) {
                doAnimation();
            } else {
                cancelAnimation();
            }
        } else if (buttonView == mMiscellaneousSwitch) {
            mProfileView.enableMiscellaneous(isChecked);
        } else if (buttonView == mDrawPointSwitch) {
            mProfileView.enableDrawPoint(isChecked);
        }
    }

    private void cancelAnimation() {
        final int childCount = mContainerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (mContainerView.getChildAt(i) instanceof Switch) {
                mContainerView.getChildAt(i).animate().setDuration(1200).translationY(0).start();
            }
        }
    }

    private void doAnimation() {
        final int childCount = mContainerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childAt = mContainerView.getChildAt(i);
            if (childAt instanceof Switch) {
                childAt.animate()
                        .setDuration(1200)
                        .translationY(500)
                        .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ViewPropertyAnimator animate = childAt.animate();
                                animate.setUpdateListener(null);
                            }
                        }).start();
            }
        }
    }
}
