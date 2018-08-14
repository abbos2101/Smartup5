package uz.greenwhite.smartup5_trade.m_report.ui;// 05.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_report.ReportUtil;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;
import uz.greenwhite.smartup5_trade.m_report.job.ReportJob;

public class ReportViewFragment extends MoldContentFragment {

    public static void open(ArgReport arg) {
        Mold.openContent(ReportViewFragment.class, Mold.parcelableArgument(arg, ArgReport.UZUM_ADAPTER));
    }

    public ArgReport getArgReport() {
        return Mold.parcelableArgument(this, ArgReport.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.webview);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArgReport arg = getArgReport();
        Mold.setTitle(getActivity(), ReportUtil.getRepTitle(arg.code));

        addMenu(R.drawable.ic_sync_black_24dp, R.string.update, new Command() {
            @Override
            public void apply() {
                reloadContent(arg);
            }
        });

        reloadContent(arg);
    }

    public void reloadContent(ArgReport arg) {
        jobMate.stopListening();
        jobMate.execute(new ReportJob(arg))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String result) {
                        showReport(result);
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable error) {
                        Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(error).message).show();
                    }
                });
    }

    private void showReport(String result) {
        WebView wv = vsRoot.id(R.id.webview);
        WebSettings settings = wv.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        wv.loadDataWithBaseURL(null, result, "text/html", "uft-8", null);
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }
}
