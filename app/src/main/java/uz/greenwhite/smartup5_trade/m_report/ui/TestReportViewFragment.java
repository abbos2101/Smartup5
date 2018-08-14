package uz.greenwhite.smartup5_trade.m_report.ui;// 05.09.2016

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgNewReport;

import static android.content.Context.DOWNLOAD_SERVICE;

public class TestReportViewFragment extends MoldContentFragment {

    public static void open(Activity activity, ArgNewReport arg) {
        Mold.openContent(activity, TestReportViewFragment.class,
                Mold.parcelableArgument(arg, ArgNewReport.UZUM_ADAPTER));
    }

    public ArgNewReport getArgNewReport() {
        return Mold.parcelableArgument(this, ArgNewReport.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.z_test_webview);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.report);
        logonWithToken();
    }


    public void logonWithToken() {
        try {
            jobMate.execute(new ShortJob<String>() {
                @Override
                public String execute() throws Exception {
                    ArgNewReport arg = getArgNewReport();
                    Account account = arg.getAccount();

                    URL u = new URL(account.server.url + RT.URI_LOGON_WITH_TOKEN);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("token", account.uc.token);
                    InputStream inputStream = conn.getInputStream();
                    return conn.getHeaderField("Set-Cookie");
                }
            }).done(new Promise.OnDone<String>() {
                @Override
                public void onDone(String s) {
                    showReport(s);
                }
            }).fail(new Promise.OnFail() {
                @Override
                public void onFail(Throwable throwable) {
                    throwable.printStackTrace();
                    UI.alertError(getActivity(), throwable);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showReport(String setCookie) {
        final WebView wv = vsRoot.id(R.id.webview);
        wv.clearHistory();

        ArgNewReport arg = getArgNewReport();
        Account account = arg.getAccount();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(account.server.url, setCookie);
        CookieSyncManager.getInstance().sync();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(wv, true);
        }

        WebSettings settings = wv.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                String message = DS.getString(R.string.report_ssl_cert_error);
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = DS.getString(R.string.report_cert_authority_not_trusted);
                        break;
                    case SslError.SSL_EXPIRED:
                        message = DS.getString(R.string.report_cert_has_expired);
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = DS.getString(R.string.report_cert_hostname_mismatch);
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = DS.getString(R.string.report_cert_is_not_yet_valid);
                        break;
                }

                UI.dialog().title(DS.getString(R.string.report_ssl_cert_error))
                        .message(message)
                        .positive(R.string.ok, new Command() {
                            @Override
                            public void apply() {
                                handler.proceed(); // Ignore SSL certificate errors
                            }
                        })
                        .negative(R.string.cancel, new Command() {
                            @Override
                            public void apply() {

                                handler.cancel();
                            }
                        }).show(getActivity());
            }
        });

        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimetype);

                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------

                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription(DS.getString(R.string.report_file_download));
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });

        String url = String.format("%s/#/M/%s/%s", account.server.url, arg.filialId, arg.uri);
        System.out.println(url);
        wv.loadUrl(url, new HashMap<String, String>());
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    public boolean onBackPressed() {
        final WebView wv = vsRoot.id(R.id.webview);
        if (wv.canGoBack()) {
            wv.goBack();
            return true;
        }
        return super.onBackPressed();
    }
}
