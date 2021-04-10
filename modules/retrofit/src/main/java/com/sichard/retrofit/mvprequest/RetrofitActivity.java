package com.sichard.retrofit.mvprequest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sichard.retrofit.Post;
import com.sichard.retrofit.mvp.MvpBaseActivity;
import com.threestudio.retrofit.R;

import java.util.List;

/**
 * <br>类表述：Retrofit测类
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2019/5/18</b></br>
 */
public class RetrofitActivity extends MvpBaseActivity<RequestPresenter> implements RequestContract.View {
    private TextView mTextView;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        mTextView = findViewById(R.id.info);

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


    /*第三种方法：Callback持有activity的引用，但是Callbak在WeakRefHandler中是弱引用，故可以释放*/
    // 注意此处mCallback不可以改成匿名内部类
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

    @Override
    protected RequestPresenter createPresenter() {
        return new RequestPresenter();
    }


    /**
     * 直接通过retrofit自带的队列来完成异步操作请求，并在主线程回调
     */
    public void requestRetrofit(final View view) {
        mPresenter.retrofitRequest(RequestContract.View.RETROFIT);
    }

    /**
     * 用RxJava完成异步请求
     */
    public void requestRxJava(View view) {
        mPresenter.retrofitRequest(RequestContract.View.RXJAVA);
    }

    /**
     * 在Retrofit加入对RxJava的支持，使Retrofit接口直接返回Observable，从而省去了RxJava自己调用create()方法来创建Observable
     */
    public void requestRetrofitRxJava(View view) {
        mPresenter.retrofitRequest(RequestContract.View.RETROFIT_RXJAVA);
    }

    @Override
    public void showRequesting(int type) {
        setTextcolor(type);
        mTextView.setText("正在请求中，请稍后...");
    }

    @Override
    public void showRetrofitResult(int type, List<Post> posts) {
        StringBuilder builder = new StringBuilder();
        if (posts != null) {
            for (Post post : posts) {
                builder.append(post.getBody());
                builder.append("\n---------------------\n");
            }
        }
        setTextcolor(type);
        mTextView.setText(builder.toString());

    }

    private void setTextcolor(int type) {
        switch (type) {
            case RETROFIT:
                mTextView.setTextColor(Color.RED);
                break;
            case RXJAVA:
                mTextView.setTextColor(Color.GREEN);
                break;
            case RETROFIT_RXJAVA:
                mTextView.setTextColor(Color.BLUE);
                break;
        }
    }

    @Override
    public void showError(String errMsg) {
        mTextView.setText(errMsg);
    }
}
