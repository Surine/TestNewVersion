package com.example.surine.testnewversion.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Surine on 2017/12/15.
 * 网络请求工具
 * get/post请求方法
 */

public class HttpUtil {

    //get
    public static Call get(String url){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS);//设置连接超时时间;
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient.newCall(new Request.Builder().url(url).build());
    }

    //post
    public static Call post(String url, FormBody formBody){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS);//设置连接超时时间;
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient.newCall(new Request.Builder().post(formBody).url(url).build());
    }

}
