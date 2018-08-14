package uz.greenwhite.smartup5_trade.m_session.job;// 09.08.2016

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.http.HttpRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.LongJob;
import uz.greenwhite.lib.job.Progress;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup5_trade.datasource.DataSource;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;

public class SendPhotoJob extends LongJob<ProgressValue> {

    public static String key(String accountId) {
        return "send_photo:" + accountId;
    }

    private final Account account;
    private final DataSource ds;
    private final ProgressValue progressValue;

    public SendPhotoJob(Account account, Scope scope, ProgressValue progressValue) {
        super(key(account.accountId));
        this.account = account;
        this.ds = scope.ds;
        this.progressValue = progressValue;
    }

    @Override
    public void execute(Progress<ProgressValue> progress) throws Exception {
        ds.db.photoChangeAllReadyToLocked();

        MyArray<String> photoIds = ds.db.photoLoadAllLockedIds();

        if (photoIds.nonEmpty()) {
            progressValue.uploadPhoto.upload = true;
            progressValue.uploadPhoto.total = photoIds.size();
            progress.notify(progressValue);

            for (String photoId : photoIds) {
                try {
                    progressValue.uploadPhoto.current++;
                    progress.notify(progressValue);

                    executePhoto(photoId);

                    progressValue.uploadPhoto.success++;
                    progress.notify(progressValue);

                } catch (Exception e) {

                    progressValue.uploadPhoto.error++;
                    progress.notify(progressValue);

                    ds.db.photoUpdateStateById(photoId, EntryState.READY);
                    ErrorUtil.saveThrowable(e);
                }
            }
        }
    }

    private void executePhoto(final String photoId) throws Exception {
        HttpUtil.post(account.server.url + RT.URI_PHOTO, new HttpRequest<Void>() {
            @Override
            public void header(HttpURLConnection conn) throws Exception {
                super.header(conn);
                conn.setRequestProperty("BiruniUpload", "alone");
                conn.setRequestProperty("Content-Type", "image/jpeg");
                conn.setRequestProperty("filename", "image.jpg");
                conn.setRequestProperty("token", account.uc.token);
            }

            @Override
            public void send(OutputStream os) throws Exception {
                try {
                    byte[] bytes = ds.db.loadPhotoFullId(photoId);
                    os.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Void receive(InputStream is) throws Exception {
                ds.db.photoDeleteById(photoId);
                return null;
            }
        });
    }
}
