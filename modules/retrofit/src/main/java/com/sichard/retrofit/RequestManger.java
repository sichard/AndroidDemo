package com.sichard.retrofit;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <br>类表述：网络请求管理者
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2019/5/18</b></br>
 */
public class RequestManger {
    private Retrofit mRetrofit;

    private static class Holder {
        private final static RequestManger requestManger = new RequestManger();
    }

    public static RequestManger getInstance() {
        return Holder.requestManger;
    }

    private RequestManger() {
        OkHttpClient okHttpClient = new OkHttpClient();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Config.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }


}
