package cn.share.can.cyghttp.callback;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 *
 */

public interface BaseImpl {

    boolean addDisposable(Disposable disposable);

    Context getContext();

}
