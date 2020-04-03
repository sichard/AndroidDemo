package com.sichard.retrofit.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;

/**
 * <br>类表述：
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2020/4/2</b></br>
 */
public abstract class MvpBaseActivity<P extends BasePresenter> extends BaseActivity implements IView{
    public P mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
