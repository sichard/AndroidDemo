package com.sichard.retrofit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.threestudio.retrofit.R;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <br>类表述：Retrofit测类
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2019/5/18</b></br>
 */
public class RetrofitActivity extends BaseActivity {
    private TextView mTextView;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        mTextView = findViewById(R.id.info);
    }

    /**
     * 直接通过retrofit自带的队列来完成异步操作请求，并在主线程回调
     */
    public void requestRetrofit(final View view) {
        mView = view;
        Retrofit retrofit = RequestManger.getInstance().getRetrofit();
        final RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        requestInterface.index().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                List<Post> postList = response.body();
                showResult(postList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 用RxJava完成异步请求
     */
    public void requestRxJava(View view) {
        mView = view;
        Observable.create(new ObservableOnSubscribe<List<Post>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Post>> emitter) throws Exception {
                Retrofit retrofit = RequestManger.getInstance().getRetrofit();
                Call<List<Post>> index = retrofit.create(RequestInterface.class).index();
                Response<List<Post>> response = index.execute();
                if (response != null && response.isSuccessful()) {
                    emitter.onNext(response.body());
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver() {
                });
    }

    /**
     * 在Retrofit加入对RxJava的支持，使Retrofit接口直接返回Observable，从而省去了RxJava自己调用create()方法来创建Observable
     */
    public void requestRetrofitRxJava(View view) {
        mView = view;
        Observable<List<Post>> observable = RequestManger.getInstance().getRetrofit().create(RequestInterface.class).indexToRxJava();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver());
    }

    private void showResult(List<Post> posts) {
        StringBuilder builder = new StringBuilder();
        if (posts != null) {
            for (Post post : posts) {
                builder.append(post.getBody());
                builder.append("\n---------------------\n");
            }
        }
        switch ((String) mView.getTag()) {
            case "1":
                mTextView.setTextColor(Color.RED);
                break;
            case "2":
                mTextView.setTextColor(Color.GREEN);
                break;
            case "3":
                mTextView.setTextColor(Color.BLUE);
                break;

        }
        mTextView.setText(builder.toString());
    }

    private class ResultObserver implements Observer<List<Post>> {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<Post> posts) {
            showResult(posts);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.i("csc", "onComplete:" + "----------complete");
        }
    }
}
