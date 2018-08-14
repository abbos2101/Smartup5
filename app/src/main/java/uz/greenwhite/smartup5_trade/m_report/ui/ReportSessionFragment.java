package uz.greenwhite.smartup5_trade.m_report.ui;// 16.11.2016

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_report.ReportUtil;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ReportSessionFragment extends ReportFragment {

    public static ReportSessionFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(ReportSessionFragment.class, arg, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    public static final MyArray<Report> FORM = MyArray.from(
            new Report(ReportUtil.REP_006, R.drawable.settings_5, DS.getString(R.string.rep_title_006), RT.FMCG_REP_006),
            new Report(ReportUtil.REP_007, R.drawable.settings_5, DS.getString(R.string.rep_title_007), RT.FMCG_REP_007),
            new Report(ReportUtil.REP_008, R.drawable.settings_6, DS.getString(R.string.rep_title_008), RT.FMCG_REP_008),
            new Report(ReportUtil.REP_009, R.drawable.menu_6_active, DS.getString(R.string.rep_title_009), RT.FMCG_REP_009),
            new Report(ReportUtil.REP_010, R.drawable.settings_6, DS.getString(R.string.rep_title_010), RT.FMCG_REP_010),
            new Report(ReportUtil.REP_011, R.drawable.service_2, DS.getString(R.string.rep_title_011), RT.FMCG_REP_011),
            new Report(ReportUtil.REP_012, R.drawable.settings_6, DS.getString(R.string.rep_title_012), RT.FMCG_REP_012)
    );

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setForms(getArgSession(), FORM);
    }

    @Override
    protected void onItemClick(Report item) {
        ArgSession arg = getArgSession();
        switch (item.id) {
            case ReportUtil.REP_008:
            case ReportUtil.REP_009:
                ReportDateRangeFragment.open(new ArgReport(getArgSession(),
                        (String) item.tag, MyArray.from(arg.filialId)));
                return;
            case ReportUtil.REP_006:
            case ReportUtil.REP_007:
            case ReportUtil.REP_010:
                ReportDateRangeFragment.open(new ArgReport(getArgSession(),
                        (String) item.tag, MyArray.from(arg.filialId), true));
                return;
            case ReportUtil.REP_011:
            case ReportUtil.REP_012:
                ReportDateFragment.open(new ArgReport(getArgSession(),
                        (String) item.tag, MyArray.from(arg.filialId), false, true));
                return;
            default:
                throw AppError.Unsupported();
        }
    }
}
