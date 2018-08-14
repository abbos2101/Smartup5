package uz.greenwhite.smartup5_trade.common.scope;

import android.support.annotation.CallSuper;

import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.datasource.Scope;

public abstract class OnScopeReadyCallback<E> implements Promise.OnAlways<E> {

    public abstract E onScopeReady(Scope scope);

    public void onDone(E e) {
    }

    @CallSuper
    public void onFail(Throwable throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace();
        ErrorUtil.saveThrowable(throwable);
    }

    @CallSuper
    @Override
    public void onAlways(boolean resolve, E result, Throwable error) {
        if (resolve) onDone(result);
        else onFail(error);
    }
}
