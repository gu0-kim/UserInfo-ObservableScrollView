package com.gu.userinfo.observablescroll.sinaweibo_.rxbus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */

public class Rxbus {

    private Subject<Msg> mbus;
    private CompositeDisposable compositeDisposable;
    private static Rxbus instance = null;

    private Rxbus() {
        compositeDisposable = new CompositeDisposable();
        mbus = AsyncSubject.create();
    }

    public static Rxbus getInstance() {
        if (instance == null)
            instance = new Rxbus();
        return instance;
    }

    public void sendMsg(Msg msg) {
        mbus.onNext(msg);
    }

    public void registerBus(Consumer<Msg> consumer) {
        compositeDisposable.add(mbus.subscribe(consumer));
    }

    public void disposeAll() {
        if (compositeDisposable != null)
            compositeDisposable.dispose();
    }

    public void dispose(Disposable d) {
        compositeDisposable.delete(d);
        if (!d.isDisposed())
            d.dispose();
    }

    public enum Type {LOAD_SUC, LOAD_ERRO}

    public static class Msg {

        Type msgType;
        String content;

        public Msg() {
            this(Type.LOAD_SUC, "default");
        }

        public Msg(Type type, String content) {
            this.msgType = type;
            this.content = content;
        }
    }

}
