package com.sichard.retrofit.mvprequest

import com.sichard.retrofit.Post
import com.sichard.retrofit.mvp.IModel
import com.sichard.retrofit.mvp.IView
import com.sichard.retrofit.mvprequest.RequestModel.ResponseCallback

/**
 * <br></br>类表述：业务约束类，单个页面的接口统一放在该约束类中，方便查看
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2020/4/2**
 */
interface RequestContract {
    interface View : IView {
        fun showRequesting(type: Int)
        fun showRetrofitResult(type: Int, result: List<Post>?)
        fun showError(errMsg: String?)

        /**
         * 用来测试内存泄漏的方法
         */
        fun showString()

        companion object {
            const val RETROFIT = 1
            const val RXJAVA = 2
            const val RETROFIT_RXJAVA = 3
        }
    }

    interface Model : IModel {
        fun retrofitRequest(type: Int, callback: ResponseCallback)
    }

    interface IPresenter {
        fun retrofitRequest(type: Int)
    }
}