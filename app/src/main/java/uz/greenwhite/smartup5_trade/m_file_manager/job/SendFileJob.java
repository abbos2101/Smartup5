package uz.greenwhite.smartup5_trade.m_file_manager.job;// 09.08.2016

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.http.HttpRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.bean.admin.Account;

public class SendFileJob implements ShortJob<String> {

    private final Account account;
    private final File file;
    private final String uri;

    public SendFileJob(Account account, String uri, File file) {
        this.account = account;
        this.uri = uri;
        this.file = file;
    }

    public String getMimeType() {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        if (TextUtils.isEmpty(extension)) {
            String url = file.getPath();
            int filenamePos = url.lastIndexOf('/');
            String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;
            int dotPos = filename.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = filename.substring(dotPos + 1);
            }
        }

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public String execute() throws Exception {
        return HttpUtil.post(account.server.url + uri, new HttpRequest<String>() {
            String sha = "";

            @Override
            public void header(HttpURLConnection conn) throws Exception {
                super.header(conn);
                conn.setRequestProperty("BiruniUpload", "alone");
                conn.setRequestProperty("Content-Type", getMimeType());
                conn.setRequestProperty("filename", file.getName());
                conn.setRequestProperty("token", account.uc.token);
            }

            @Override
            public void send(OutputStream os) throws Exception {
                try {
                    InputStream is = new FileInputStream(file);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(b)) != -1) {
                        buffer.write(b, 0, bytesRead);
                    }
                    byte[] bytes = buffer.toByteArray();
                    sha = Util.calcSHA(bytes);
                    os.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String receive(InputStream is) throws Exception {
                return sha;
            }
        });
    }
}
