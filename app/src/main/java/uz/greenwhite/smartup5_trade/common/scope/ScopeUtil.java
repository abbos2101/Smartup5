package uz.greenwhite.smartup5_trade.common.scope;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ScopeUtil {

    //----------------------------------------------------------------------------------------------

    @Nullable
    public static <V> V execute(@NonNull final ArgSession arg,
                                @NonNull final OnScopeReadyCallback<V> callback) {
        return execute(arg.accountId, arg.filialId, callback);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static <V> V execute(@NonNull final String accountId,
                                @NonNull final String filialId,
                                @NonNull final OnScopeReadyCallback<V> callback) {
        if (TextUtils.isEmpty(accountId) || callback == null) {
            throw AppError.NullPointer();
        }
        final Setter<V> result = new Setter<>();
        ErrorUtil.tryCatch(new OnTryCatchCallback() {
            @Override
            public void onTry() throws Exception {
                Scope scope = DS.getScope(accountId, filialId);
                result.value = callback.onScopeReady(scope);
                callback.onDone(result.value);
            }

            @Override
            public void onCatch(Exception ex) throws Exception {
                callback.onFail(ex);
            }
        });

        return result.value;
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    public static <V> void execute(@NonNull JobMate jobMate,
                                   @NonNull ArgSession arg,
                                   @NonNull OnScopeReadyCallback<V> callback) {
        execute(jobMate, arg.accountId, arg.filialId, callback);
    }

    @SuppressWarnings("ConstantConditions")
    public static <V> void execute(@NonNull JobMate jobMate,
                                   @NonNull final String accountId,
                                   @NonNull final String filialId,
                                   @NonNull final OnScopeReadyCallback<V> callback) {
        if (jobMate == null || TextUtils.isEmpty(accountId) || callback == null) {
            throw AppError.NullPointer();
        }
        final Scope scope = DS.getScope(accountId, filialId);
        jobMate.execute(new ShortJob<V>() {
            @Override
            public V execute() throws Exception {
                return callback.onScopeReady(scope);
            }
        }).always(callback);
    }

    @SuppressWarnings("ConstantConditions")
    public static <V> void executeWithDialog(@NonNull Activity activity,
                                             @NonNull JobMate jobMate,
                                             @NonNull ArgSession arg,
                                             @NonNull OnScopeReadyCallback<V> callback) {
        execute(activity, jobMate, arg.accountId, arg.filialId, callback);
    }

    @SuppressWarnings("ConstantConditions")
    public static <V> void execute(@NonNull Activity activity,
                                   @NonNull JobMate jobMate,
                                   @NonNull final String accountId,
                                   @NonNull final String filialId,
                                   @NonNull final OnScopeReadyCallback<V> callback) {
        if (activity == null || jobMate == null || TextUtils.isEmpty(accountId) || callback == null) {
            throw AppError.NullPointer();
        }
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(uz.greenwhite.lib.R.string.please_wait));
        pd.show();

        final Scope scope = DS.getScope(accountId, filialId);
        jobMate.execute(new ShortJob<V>() {
            @Override
            public V execute() throws Exception {
                return callback.onScopeReady(scope);
            }
        }).always(callback).always(new Promise.OnAlways<V>() {
            @Override
            public void onAlways(boolean b, V v, Throwable throwable) {
                pd.dismiss();
            }
        });
    }
}
