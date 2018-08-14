package uz.greenwhite.smartup5_trade.m_report.ui;// 26.10.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_report.ReportUtil;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;

public class ReportDateFragment extends MoldContentFragment {

    public static void open(ArgReport arg) {
        Mold.openContent(ReportDateFragment.class, Mold.parcelableArgument(arg, ArgReport.UZUM_ADAPTER));
    }

    public ArgReport getArgReport() {
        return Mold.parcelableArgument(this, ArgReport.UZUM_ADAPTER);
    }

    ValueSpinner sp;
    ViewSetup vsRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.rep_date);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ArgReport arg = getArgReport();
        Mold.setTitle(getActivity(), ReportUtil.getRepTitle(arg.code));

        vsRoot.editText(R.id.to_date).setText(DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE));

        vsRoot.makeDatePicker(R.id.to_date);
        vsRoot.id(R.id.btn_show_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReport();
            }
        });

        if (arg.prepaymentStatus) {
            vsRoot.id(R.id.ll_prepayment_status).setVisibility(View.VISIBLE);
            MyArray<SpinnerOption> options = MyArray.from(
                    new SpinnerOption("all", DS.getString(R.string.show_all)),
                    new SpinnerOption("W", DS.getString(R.string.report_prepayment_waiting)),
                    new SpinnerOption("P", DS.getString(R.string.report_prepayment_posted)),
                    new SpinnerOption("A", DS.getString(R.string.report_prepayment_abort))
            );
            this.sp = new ValueSpinner(options);
            vsRoot.bind(R.id.sp_status, sp);
        }
    }

    private void showReport() {
        ArgReport arg = getArgReport();
        String date = ((EditText) vsRoot.id(R.id.to_date)).getText().toString();
        String prepaymentStatus = ""; // for collector rep_012
        if (arg.prepaymentStatus && sp != null) {
            prepaymentStatus = sp.getValue().code;
        }
        if (TextUtils.isEmpty(date)) {
            Mold.makeSnackBar(getActivity(), R.string.report_all_field_requared).show();
        } else {
            ReportViewFragment.open(new ArgReport(arg, arg.code, arg.data.append(date).append(prepaymentStatus)));
        }
    }
}
