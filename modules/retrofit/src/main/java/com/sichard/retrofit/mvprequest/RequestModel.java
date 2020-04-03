package com.sichard.retrofit.mvprequest;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sichard.retrofit.Post;
import com.sichard.retrofit.RequestInterface;
import com.sichard.retrofit.RequestManger;
import com.sichard.retrofit.RxTransformerHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <br>类表述：请求业务
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2020/4/2</b></br>
 */
public class RequestModel implements RequestContract.Model {
    @Override
    public void retrofitRequest(int type, final ResponseCallback callback) {
        switch (type) {
            case RequestContract.View.RETROFIT:
                requestRetrofit(callback);
                break;
            case RequestContract.View.RXJAVA:
                requestRxJava(callback);
                break;
            case RequestContract.View.RETROFIT_RXJAVA:
                requestRetrofitRxJava(callback);
                break;
        }
    }

    /**
     * 直接通过retrofit自带的队列来完成异步操作请求，并在主线程回调
     * 注意:此处如果直接用OKHttp的Call对象来执行enqueue，回调方法是在非工作线程中执行的，不可以操作UI。
     */
    private void requestRetrofit(final ResponseCallback callback) {

        Retrofit retrofit = RequestManger.getInstance().getRetrofit();
        final RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        requestInterface.index().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                List<Post> postList = response.body();
                Log.i("csc", "onResponse:" + "---success");
                callback.requestSuccess(postList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Log.e("csc", t.getMessage());
                callback.onError("请求失败！");
            }
        });

    }

    /**
     * 用RxJava完成异步请求
     */
    @SuppressLint("CheckResult")
    private void requestRxJava(final ResponseCallback callback) {
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
        }).compose(RxTransformerHelper.<List<Post>>observableIO2Main())
                .subscribe(new Consumer<List<Post>>() {
                    @Override
                    public void accept(List<Post> posts) throws Exception {
                        callback.requestSuccess(posts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onError("请求失败");
                    }
                });
    }

    /**
     * 在Retrofit加入对RxJava的支持，使Retrofit接口直接返回Observable，从而省去了RxJava自己调用create()方法来创建Observable
     */
    @SuppressLint("CheckResult")
    private void requestRetrofitRxJava(final ResponseCallback callback) {
        RequestManger.getInstance().getRetrofit()
                .create(RequestInterface.class)
                .indexToRxJava()
                .compose(RxTransformerHelper.<List<Post>>observableIO2Main())
                .subscribe(new Consumer<List<Post>>() {
                    @Override
                    public void accept(List<Post> posts) throws Exception {
                        callback.requestSuccess(posts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onError("请求失败");
                    }
                });
    }

    interface ResponseCallback {
        void requestSuccess(List<Post> result);

        void onError(String errMessage);
    }
}
