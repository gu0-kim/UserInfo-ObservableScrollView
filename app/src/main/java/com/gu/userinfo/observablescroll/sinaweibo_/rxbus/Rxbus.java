package com.gu.userinfo.observablescroll.sinaweibo_.rxbus;

import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
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
    Log.e("TAG", "new Rxbus!");
    compositeDisposable = new CompositeDisposable();
    mbus = PublishSubject.create();
  }

  public static Rxbus getInstance() {
    if (instance == null) instance = new Rxbus();
    return instance;
  }

  public void sendMsg(Msg msg) {
    mbus.onNext(msg);
  }

  public Disposable registerBus(Consumer<Msg> consumer) {
    Disposable d = mbus.observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    compositeDisposable.add(d);
    return d;
  }

  public void disposeAll() {
    if (compositeDisposable != null) compositeDisposable.dispose();
    instance = null;
  }

  public void dispose(Disposable d) {
    compositeDisposable.delete(d);
    if (!d.isDisposed()) {
      Log.e("TAG", "dispose register!");
      d.dispose();
    }
  }

  public enum MsgType {
    START,
    FIN,
    DEF
  }

  public static class Msg {

    private int index;
    private MsgType type;
    private String content;

    public Msg() {
      this(-1, MsgType.DEF, "default");
    }

    public Msg(int index, MsgType type, String content) {
      this.index = index;
      this.type = type;
      this.content = content;
    }

    public int getIndex() {
      return index;
    }

    public MsgType getType() {
      return type;
    }

    public String getContent() {
      return content;
    }
  }
}
