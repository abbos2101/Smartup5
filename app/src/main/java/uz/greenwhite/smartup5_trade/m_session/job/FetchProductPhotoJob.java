package uz.greenwhite.smartup5_trade.m_session.job;// 26.08.2016

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.http.HttpRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.LongJob;
import uz.greenwhite.lib.job.Progress;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_product.bean.PhotoInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;

public class FetchProductPhotoJob extends LongJob<ProgressValue> {

    public static String key(String accountId) {
        return "fetch_product_photo:" + accountId;
    }

    private final Account account;
    private final String filialId;
    private final ProgressValue progressValue;

    public FetchProductPhotoJob(Account account, String filialId, ProgressValue progressValue) {
        super(key(account.accountId));
        this.account = account;
        this.filialId = filialId;
        this.progressValue = progressValue;
    }

    @Override
    public void execute(Progress<ProgressValue> progress) throws Exception {
        DS.initScope(account.accountId, filialId);
        Scope scope = DS.getScope(account.accountId, filialId);
        MyArray<ProductPhoto> results = scope.ref.getProductPhotos();

        if (results == null || results.isEmpty()) {
            return;
        }

        Set<String> ps = getPhotoShas(results);
        if (ps.isEmpty()) {
            return;
        }

        File root = getPhotoRootDir();
        Set<String> esp = Utils.elevateExistingFiles(ps, root);
        if (esp.isEmpty()) {
            return;
        }

        if (!esp.isEmpty()) {
            progressValue.downloadPhoto.download = true;
            progressValue.downloadPhoto.total = esp.size();
            progress.notify(progressValue);

            for (String sha : esp) {
                try {
                    progressValue.downloadPhoto.current++;
                    progress.notify(progressValue);

                    fetchPhoto(sha, root);

                    progressValue.downloadPhoto.success++;
                    progress.notify(progressValue);

                } catch (Exception ex) {
                    ErrorUtil.saveThrowable(ex);

                    progressValue.downloadPhoto.error++;
                    progress.notify(progressValue);
                }
            }
        }
    }

    private void fetchPhoto(final String sha, final File root) throws Exception {
        String url = account.server.url + RT.URI_PHOTO_DOWNLOAD;
        HttpUtil.post(url, new HttpRequest<Object>() {
            @Override
            public void header(HttpURLConnection conn) throws Exception {
                super.header(conn);
                conn.setRequestProperty("token", account.uc.token);
            }

            @Override
            public void send(OutputStream os) throws Exception {
                os.write(("{\"sha\":\"" + sha + "\"}").getBytes());
            }

            @SuppressWarnings("TryFinallyCanBeTryWithResources")
            @Override
            public Object receive(InputStream is) throws Exception {
                FileOutputStream out = new FileOutputStream(new File(root, sha));
                try {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } finally {
                    out.close();
                }
                return null;
            }
        });
    }

    private Set<String> getPhotoShas(MyArray<ProductPhoto> photos) {
        Set<String> result = new HashSet<>();
        for (ProductPhoto photo : photos) {
            for (PhotoInfo sha : photo.photos) result.add(sha.fileSha);
        }
        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getPhotoRootDir() {
        String photoPath = ProductPhoto.getPhotoPath(account.accountId);
        File root = new File(photoPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }
}
