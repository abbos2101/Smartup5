package uz.greenwhite.smartup5_trade.m_file_manager.job;// 15.11.2016

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.http.HttpRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil;

public class DownloadFileJob extends HttpRequest<String> implements ShortJob<String> {

    private final Account account;
    private final String sha;
    private final String filename;

    public DownloadFileJob(String accountId, String sha, String filename) {
        this.account = AdminApi.getAccount(accountId);
        this.sha = sha;
        this.filename = filename;
    }

    @Override
    public String execute() throws Exception {
        if (!TextUtils.isEmpty(sha) && !FileManagerUtil.fileExistsInDownloadFolder(account.accountId, filename)) {
            return HttpUtil.post(account.server.url + RT.URI_FILE_DOWNLOAD, this);
        }
        return new File(DS.getServerPath(account.accountId) + "/downloads", filename).getPath();
    }


    @Override
    public void header(HttpURLConnection conn) throws Exception {
        super.header(conn);
        conn.setRequestProperty("token", account.uc.token);
    }

    @Override
    public void send(OutputStream os) throws Exception {
        os.write(("{\"sha\":\"" + sha + "\"}").getBytes());
    }

    @Override
    public String receive(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(b)) != -1) {
            buffer.write(b, 0, bytesRead);
        }
        return FileManagerUtil.downloadFileFromServer(account.accountId, filename, buffer.toByteArray());
    }
}
