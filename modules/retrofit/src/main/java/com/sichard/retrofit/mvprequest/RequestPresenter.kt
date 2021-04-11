package com.sichard.retrofit.mvprequest

import android.util.Log
import com.sichard.retrofit.Post
import com.sichard.retrofit.mvp.BasePresenter
import com.sichard.retrofit.mvprequest.RequestContract.IPresenter
import com.sichard.retrofit.mvprequest.RequestModel.ResponseCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * <br></br>类表述：业务Presenter，需要继承BasePresenter，实现约束类Contract的IPresenter接口
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2020/4/2**
 */
class RequestPresenter : BasePresenter<RequestContract.Model, RequestContract.View>(), IPresenter {
    override fun createMode(): RequestContract.Model {
        return RequestModel()
    }

    override fun retrofitRequest(type: Int) {
        mProxyView.showRequesting(type)
        mMode!!.retrofitRequest(type, object : ResponseCallback {
            override fun requestSuccess(result: List<Post>?) {
                mProxyView.showRetrofitResult(type, result)
                Log.i("sichardcao", mProxyView.showString().toString())
            }

            override fun onError(errMessage: String?) {
                mProxyView.showError(errMessage)
            }
        })

        //如果父类mMainScope.cancel()方法不调用，将会导致内存泄漏
        mMainScope.launch {
            delay(30000)
            mProxyView.showString()
        }
    }
}