package uz.greenwhite.smartup5_trade.m_session.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import uz.greenwhite.lib.job.Progress;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class SyncJob extends uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob {

    private final boolean downloadPhoto;
    private final boolean downloadFile;

    public SyncJob(@NonNull Account account,
                   @Nullable String filialId,
                   boolean shortSync,

                   boolean sync,
                   boolean sendPhoto,
                   boolean downloadPhoto,
                   boolean downloadFile) {
        super(account, filialId, shortSync, sync, sendPhoto);
        this.downloadPhoto = downloadPhoto;
        this.downloadFile = downloadFile;
    }

    @Override
    public void execute(final Progress<ProgressValue> progress) throws Exception {
        super.execute(progress);

        ErrorUtil.tryCatch(new OnTryCatchCallback() {
            @Override
            public void onTry() throws Exception {
                if (downloadPhoto && !TextUtils.isEmpty(filialId)) {
                    ErrorUtil.tryCatch(new OnTryCatchCallback() {
                        @Override
                        public void onTry() throws Exception {
                            new FetchProductPhotoJob(account, filialId, progressValue).execute(progress);
                        }
                    });
                }

                if (downloadFile && !TextUtils.isEmpty(filialId)) {
                    ErrorUtil.tryCatch(new OnTryCatchCallback() {
                        @Override
                        public void onTry() throws Exception {
                            new FetchProductFileJob(account, filialId, progressValue).execute(progress);
                        }
                    });
                }
            }

            @Override
            public void onCatch(Exception ex) throws Exception {
                ErrorUtil.saveThrowable(ex, account.accountId);
            }
        });
    }

    @Override
    protected void syncFinally() {
        Manager.handler.post(new Runnable() {
            @Override
            public void run() {
                DS.clearScopeCache(account.accountId);
            }
        });
    }
}
