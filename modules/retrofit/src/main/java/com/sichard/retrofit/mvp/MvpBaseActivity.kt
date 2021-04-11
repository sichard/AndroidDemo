package com.sichard.retrofit.mvp

import android.os.Bundle
import com.android.sichard.common.BaseActivity

/**
 * <br></br>类表述：
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2020/4/2**
 */
abstract class MvpBaseActivity<P : BasePresenter<*, V>, V : IView> : BaseActivity(), IView {
    lateinit var mPresenter: P
    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter.attachView(this as V)
    }

    protected abstract fun createPresenter(): P
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}