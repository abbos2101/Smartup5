package uz.greenwhite.smartup5_trade.m_report.ui;// 05.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_report.ReportUtil;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;

public class ReportOutletFragment extends ReportFragment {

    public static ReportOutletFragment newInstance(ArgOutlet arg) {
        return Mold.parcelableArgumentNewInstance(ReportOutletFragment.class, arg, ArgOutlet.UZUM_ADAPTER);
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    public static final MyArray<Report> FORM = MyArray.from(
            new Report(ReportUtil.REP_001, R.drawable.settings_6, DS.getString(R.string.rep_title_001), RT.FMCG_REP_001),
            new Report(ReportUtil.REP_002, R.drawable.menu_6_active, DS.getString(R.string.rep_title_002), RT.FMCG_REP_002),
            new Report(ReportUtil.REP_003, R.drawable.settings_6, DS.getString(R.string.rep_title_003), RT.FMCG_REP_003),
            new Report(ReportUtil.REP_004, R.drawable.service_2, DS.getString(R.string.rep_title_004), RT.FMCG_REP_004),
            new Report(ReportUtil.REP_005, R.drawable.settings_5, DS.getString(R.string.rep_title_005), RT.FMCG_REP_005)
    );

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setForms(getArgOutlet(), FORM);
    }

    @Override
    protected void onItemClick(Report item) {
        MyArray<String> data;
        ArgOutlet arg = getArgOutlet();
        switch (item.id) {
            case ReportUtil.REP_001:
            case ReportUtil.REP_005:
                data = MyArray.from(arg.filialId, arg.outletId);
                break;
            case ReportUtil.REP_002:
                ReportDateFragment.open(new ArgReport(getArgOutlet(),
                        (String) item.tag, MyArray.from(arg.filialId, arg.outletId)));
                return;
            case ReportUtil.REP_003:
                ReportDateRangeFragment.open(new ArgReport(getArgOutlet(),
                        (String) item.tag, MyArray.from(arg.filialId, arg.outletId)));
                return;
            case ReportUtil.REP_004:
                ReportDateRangeFragment.open(new ArgReport(getArgOutlet(),
                        (String) item.tag, MyArray.from(arg.filialId, arg.outletId), true));
                return;
            default:
                throw AppError.Unsupported();
        }
        ReportViewFragment.open(new ArgReport(getArgOutlet(), (String) item.tag, data));
    }
}
