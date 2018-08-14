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
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup5_trade.m_product.bean.FileInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;

public class FetchProductFileJob extends LongJob<ProgressValue> {

    public static String key(String accountId) {
        return "fetch_product_file:" + accountId;
    }

    private final Account account;
    private final String filialId;
    private final ProgressValue progressValue;

    public FetchProductFileJob(Account account, String filialId, ProgressValue progressValue) {
        super(key(account.accountId));
        this.account = account;
        this.filialId = filialId;
        this.progressValue = progressValue;
    }

    @Override
    public void execute(Progress<ProgressValue> progress) throws Exception {
        DS.initScope(account.accountId, filialId);
        Scope scope = DS.getScope(account.accountId, filialId);
        MyArray<ProductFile> results = scope.ref.getProductFiles();

        if (results == null || results.isEmpty()) {
            return;
        }

        Set<String> ps = getFileShas(results);
        if (ps.isEmpty()) {
            return;
        }
        File root = getFileRootDir();
        Set<String> esp = Utils.elevateExistingFiles(ps, root);
        if (esp.isEmpty()) {
            return;
        }

        if (!esp.isEmpty()) {
            progressValue.downloadFile.download = true;
            progressValue.downloadFile.total = esp.size();
            progress.notify(progressValue);

            for (String sha : esp) {
                try {
                    progressValue.downloadFile.current++;
                    progress.notify(progressValue);

                    fetchPhoto(sha, root);

                    progressValue.downloadFile.success++;
                    progress.notify(progressValue);

                } catch (Exception ex) {
                    ErrorUtil.saveThrowable(ex);

                    progressValue.downloadFile.error++;
                    progress.notify(progressValue);
                }
            }
        }
    }

    private void fetchPhoto(final String sha, final File root) throws Exception {
        String url = account.server.url + RT.URI_FILE_DOWNLOAD;
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

    private Set<String> getFileShas(MyArray<ProductFile> vals) {
        Set<String> result = new HashSet<>();
        for (ProductFile val : vals) {
            for (FileInfo sha : val.files) {
                result.add(sha.fileSha);
            }
        }
        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getFileRootDir() {
        String filePath = ProductFile.getFilePath(account.accountId);
        File root = new File(filePath);
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }
}
