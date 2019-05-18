package com.sichard.retrofit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.threestudio.retrofit.R;

import java.util.List;

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
    private TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        tv = findViewById(R.id.info);
    }

    public void loadData(View view) {
        Retrofit retrofit = RequestManger.getInstance().getmRetrofit();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        requestInterface.index().enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(@NonNull Call<List<Posts>> call, @NonNull Response<List<Posts>> response) {
                List<Posts> postsList = response.body();
                StringBuffer buffer = new StringBuffer();
                if (postsList != null) {
                    for (Posts posts : postsList) {
                        buffer.append(posts.getBody());
                    }
                }
                tv.setText(buffer.toString());
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {

            }
        });
//        try {
//            Response<List<Posts>> response = index.execute();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
