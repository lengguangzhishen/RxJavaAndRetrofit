package com.jyt.rxjavaandretrofitdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://www.baidu.com/")
                .build();
    }

    private void requestData() {
        mRetrofit.create(RequestData.class)
                .getInfo()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        //保存信息逻辑
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        //请求成功的逻辑
                    }
                });

    }

    private void nestRequestData() {
        final NestRequestData nestRequestDataService = mRetrofit.create(NestRequestData.class);
        nestRequestDataService.getToken()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //save token
                    }
                })
                .flatMap(new Func1<String, Observable<UserInfo>>() {
                    @Override
                    public Observable<UserInfo> call(String s) {
                        return nestRequestDataService.getInfo(s);
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        //save userinfo
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //request fail
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        //request success
                    }
                });
    }
}

interface RequestData{
    @GET
    //@GET(".....")
    Observable<UserInfo> getInfo();
}

interface NestRequestData{
    @GET
    Observable<String> getToken();

    @GET
    Observable<UserInfo> getInfo(@Query("token") String token);
}

class UserInfo{
    public String name;
    public String age;
}
