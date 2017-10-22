package com.gu.userinfo.observablescroll.sinaweibo_.module;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */

public class HttpModule {

    private static HttpModule instance = null;

    public static final String TAG = "TAG";

    private HttpModule() {
    }

    public static HttpModule getInstance() {
        if (instance == null)
            instance = new HttpModule();
        return instance;
    }

    public Observable<List<String>> connectUrl(String url) {
        Log.e(TAG, "connectUrl: url=" + url);
        return Observable.zip(getPageData1(), Observable.timer(3, TimeUnit.SECONDS), (strings, aLong) -> strings);
    }

    public Observable<Long> load() {
        return Observable.timer(3, TimeUnit.SECONDS);
    }


    /**
     * maybe happen erro exception in connect the server
     */
    public Observable<List<String>> getPageData1() {
//        int random = (int) (2 * Math.random());
//        if (random == 1)
        return Observable.just(getDummyData(40)).subscribeOn(Schedulers.io());
//        return Observable.error(() -> new Throwable("erro msg!"));
    }


    private List<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }
}
