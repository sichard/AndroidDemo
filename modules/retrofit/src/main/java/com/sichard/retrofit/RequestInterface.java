package com.sichard.retrofit;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * <br>类表述：请求接口
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2019/5/18</b></br>
 */
public interface RequestInterface {
    @GET("/posts")
    Call<List<Post>> index();

    @GET("/posts")
    Observable<List<Post>> indexToRxJava();
}
