package com.sichard.retrofit.mvprequest;

import com.sichard.retrofit.Post;
import com.sichard.retrofit.mvp.BasePresenter;

import java.util.List;

/**
 * <br>类表述：业务Presenter，需要继承BasePresenter，实现约束类Contract的IPresenter接口
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2020/4/2</b></br>
 */
public class RequestPresenter extends BasePresenter<RequestContract.Model, RequestContract.View> implements RequestContract.IPresenter {

    @Override
    protected RequestContract.Model createMode() {
        return new RequestModel();
    }

    @Override
    public void retrofitRequest(final int type) {
        mProxyView.showRequesting(type);
        mMode.retrofitRequest(type, new RequestModel.ResponseCallback() {
            @Override
            public void requestSuccess(List<Post> result) {
                mProxyView.showRetrofitResult(type, result);
            }

            @Override
            public void onError(String errMessage) {
                mProxyView.showError(errMessage);
            }
        });
    }

}
