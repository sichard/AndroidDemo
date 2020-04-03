package com.sichard.retrofit.mvprequest;

import com.sichard.retrofit.Post;
import com.sichard.retrofit.mvp.IModel;
import com.sichard.retrofit.mvp.IView;

import java.util.List;

/**
 * <br>类表述：业务约束类，单个页面的接口统一放在该约束类中，方便查看
 * <br>详细描述：
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2020/4/2</b></br>
 */
public interface RequestContract {
    interface View extends IView {
        int RETROFIT = 1;
        int RXJAVA = 2;
        int RETROFIT_RXJAVA = 3;

        void showRequesting(int type);

        void showRetrofitResult(int type, List<Post> result);

        void showError(String errMsg);
    }

    interface Model extends IModel {
        void retrofitRequest(int tpye, RequestModel.ResponseCallback callback);
    }

    interface IPresenter {
        void retrofitRequest(int type);
    }

}
