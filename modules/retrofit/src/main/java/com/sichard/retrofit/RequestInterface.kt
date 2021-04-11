package com.sichard.retrofit

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

/**
 * <br></br>类表述：请求接口
 * <br></br>详细描述：
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2019/5/18**
 */
interface RequestInterface {
    @GET("/posts")
    fun index(): Call<List<Post>>

    @GET("/posts")
    fun indexToRxJava(): Observable<List<Post>>
}