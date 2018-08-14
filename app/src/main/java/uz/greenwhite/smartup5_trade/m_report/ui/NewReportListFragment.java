package uz.greenwhite.smartup5_trade.m_report.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.MoldSearchQuery;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgNewReport;
import uz.greenwhite.smartup5_trade.m_report.row.ReportList;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class NewReportListFragment extends MoldContentFragment {

    public static void open(Activity activity, ArgSession arg) {
        Mold.openContent(activity, NewReportListFragment.class,
                Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;
    private MyArray<ReportList> reportLists = MyArray.emptyArray();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.report);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.session_report);

        setSearchMenu(new MoldSearchQuery() {
            @Override
            public void onQueryText(final String s) {
                if (reportLists.nonEmpty()) {
                    setForms(reportLists.filter(new MyPredicate<ReportList>() {
                        @Override
                        public boolean apply(ReportList reportList) {
                            return CharSequenceUtil.containsIgnoreCase(reportList.name, s);
                        }
                    }));
                }
            }
        });

        MyArray<String> from = MyArray.from();
        jobMate.executeWithDialog(getActivity(), new ActionJob<>(getArgSession(),
                RT.URI_REPORT_LIST, from, UzumAdapter.STRING.toArray()))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String json) {
                        reportLists = Uzum.toValue(json, ReportList.UZUM_ADAPTER.toArray());
                        setForms(reportLists);
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable throwable) {
                        throwable.printStackTrace();
                        UI.alertError(getActivity(), String.valueOf(ErrorUtil.getErrorMessage(throwable).exceptionMessage));
                    }
                });
    }

    protected void setForms(MyArray<ReportList> items) {
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_report_row);
        vg.removeAllViews();
        for (final ReportList item : items) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.report_row);
            vs.imageView(R.id.iv_rep_icon).setImageResource(R.drawable.menu_6_active);
            vs.textView(R.id.tv_rep_title).setText(item.name);
            vs.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(item);
                }
            });
            vg.addView(vs.view);
        }
    }

    protected void onItemClick(ReportList item) {
        TestReportViewFragment.open(getActivity(), new ArgNewReport(getArgSession(), item.uri));
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }
}
