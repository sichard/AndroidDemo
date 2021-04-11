package com.sichard.retrofit.mvprequest

import android.annotation.SuppressLint
import android.util.Log
import com.sichard.retrofit.Post
import com.sichard.retrofit.RequestInterface
import com.sichard.retrofit.RequestManger
import com.sichard.retrofit.RxTransformerHelper
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * <br></br>类表述：请求业务
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2020/4/2**
 */
class RequestModel : RequestContract.Model {
    override fun retrofitRequest(type: Int, callback: ResponseCallback) {
        when (type) {
            RequestContract.View.RETROFIT -> requestRetrofit(callback)
            RequestContract.View.RXJAVA -> requestRxJava(callback)
            RequestContract.View.RETROFIT_RXJAVA -> requestRetrofitRxJava(callback)
        }
    }

    /**
     * 直接通过retrofit自带的队列来完成异步操作请求，并在主线程回调
     * 注意:此处如果直接用OKHttp的Call对象来执行enqueue，回调方法是在非工作线程中执行的，不可以操作UI。
     */
    private fun requestRetrofit(callback: ResponseCallback) {
        val retrofit = RequestManger.getInstance().retrofit
        val requestInterface = retrofit.create(RequestInterface::class.java)
        requestInterface.index().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val postList = response.body()
                Log.i("csc", "onResponse:" + "---success")
                callback.requestSuccess(postList)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("csc", t.message)
                callback.onError("请求失败！")
            }
        })
    }

    /**
     * 用RxJava完成异步请求
     */
    @SuppressLint("CheckResult")
    private fun requestRxJava(callback: ResponseCallback) {
        Observable.create<List<Post>> { emitter ->
            val retrofit = RequestManger.getInstance().retrofit
            val index = retrofit.create(RequestInterface::class.java).index()
            val response = index.execute()
            if (response != null && response.isSuccessful) {
                emitter.onNext(response.body()!!)
            }
            emitter.onComplete()
        }.compose(RxTransformerHelper.observableIO2Main())
                .subscribe({ posts -> callback.requestSuccess(posts) }) { callback.onError("请求失败") }
    }

    /**
     * 在Retrofit加入对RxJava的支持，使Retrofit接口直接返回Observable，从而省去了RxJava自己调用create()方法来创建Observable
     */
    @SuppressLint("CheckResult")
    private fun requestRetrofitRxJava(callback: ResponseCallback) {
        RequestManger.getInstance().retrofit
                .create(RequestInterface::class.java)
                .indexToRxJava()
                .compose(RxTransformerHelper.observableIO2Main())
                .subscribe({ posts -> callback.requestSuccess(posts) }, { callback.onError("请求失败") })
    }

    interface ResponseCallback {
        fun requestSuccess(result: List<Post>?)
        fun onError(errMessage: String?)
    }
}