package com.sichard.retrofit.mvprequest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.sichard.retrofit.Post
import com.sichard.retrofit.mvp.MvpBaseActivity
import com.threestudio.retrofit.R

/**
 * <br></br>类表述：Retrofit测类
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2019/5/18**
 */
class RetrofitActivity : MvpBaseActivity<RequestPresenter, RequestContract.View>(), RequestContract.View {
    private var mTextView: TextView? = null
    private val mView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        mTextView = findViewById(R.id.info)
//        anr();
    }



    //    /*第一种方式：runnable持有activity的引用*/
    //    private Handler mHandler = new Handler();
    //
    //    private void testHandler() {
    //        mHandler.postDelayed(new Runnable() {
    //            @Override
    //            public void run() {
    //            }
    //        }, 5 * 60 * 1000);
    //
    //    }
    //    /*第二种方式：Handler持有activity应用*/
    //    private Handler mHandler = new Handler() {
    //        @Override
    //        public void handleMessage(Message msg) {
    //        }
    //    };
    //
    //    private void testHandler() {
    //        mHandler.sendEmptyMessageDelayed(1, 5 * 60 * 1000);
    //    }
    /*第三种方法：Callback持有activity的引用，但是Callbak在WeakRefHandler中是弱引用，故可以释放*/ // 注意此处mCallback不可以改成匿名内部类
    //    private Handler.Callback mCallback = new Handler.Callback() {
    //        @Override
    //        public boolean handleMessage(Message msg) {
    //            mTextView.setText("收到消息！");
    //            return true;
    //        }
    //    };
    //    private Handler mHandler = new WeakRefHandler(mCallback);
    //    private void testHandler() {
    //        mHandler.sendEmptyMessageDelayed(1, 5 * 1000);
    //    }
    override fun createPresenter(): RequestPresenter {
        return RequestPresenter()
    }

    /**
     * 直接通过retrofit自带的队列来完成异步操作请求，并在主线程回调
     */
    fun requestRetrofit(view: View?) {
        mPresenter.retrofitRequest(RequestContract.View.RETROFIT)
    }

    /**
     * 用RxJava完成异步请求
     */
    fun requestRxJava(view: View?) {
        mPresenter.retrofitRequest(RequestContract.View.RXJAVA)
    }

    /**
     * 在Retrofit加入对RxJava的支持，使Retrofit接口直接返回Observable，从而省去了RxJava自己调用create()方法来创建Observable
     */
    fun requestRetrofitRxJava(view: View?) {
        mPresenter.retrofitRequest(RequestContract.View.RETROFIT_RXJAVA)
    }

    override fun showRequesting(type: Int) {
        setTextcolor(type)
        mTextView!!.text = "正在请求中，请稍后..."
    }

    override fun showRetrofitResult(type: Int, result: List<Post>?) {
        val builder = StringBuilder()
        if (result != null) {
            for (post in result) {
                builder.append(post.body)
                builder.append("\n---------------------\n")
            }
        }
        setTextcolor(type)
        mTextView!!.text = builder.toString()
    }

    private fun setTextcolor(type: Int) {
        when (type) {
            RequestContract.View.RETROFIT -> mTextView!!.setTextColor(Color.RED)
            RequestContract.View.RXJAVA -> mTextView!!.setTextColor(Color.GREEN)
            RequestContract.View.RETROFIT_RXJAVA -> mTextView!!.setTextColor(Color.BLUE)
        }
    }

    override fun showError(errMsg: String?) {
        mTextView!!.text = errMsg
    }

    override fun showString() {
        Log.d("sichardcao", "我入参和出参都是空")
    }

    override fun showString(string: String) {
        Log.d("sichardcao", "入参 = $string")
    }

    override fun showString1(): String {
        return "我是出参".apply { Log.d("sichardcao", this) }
    }

    override fun showString2(string: String): String {
        return string.plus("+我是返回值").apply { Log.d("sichardcao", this) }
    }
}