package uz.greenwhite.smartup5_trade.m_report.ui;// 02.11.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.MultiSelectionSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_report.ReportUtil;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class ReportDateRangeFragment extends MoldContentFragment {

    public static void open(ArgReport arg) {
        Mold.openContent(ReportDateRangeFragment.class, Mold.parcelableArgument(arg, ArgReport.UZUM_ADAPTER));
    }

    public ArgReport getArgReport() {
        return Mold.parcelableArgument(this, ArgReport.UZUM_ADAPTER);
    }

    ViewSetup vsRoot;
    MultiSelectionSpinner msSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.rep_date_range);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ArgReport arg = getArgReport();
        Mold.setTitle(getActivity(), ReportUtil.getRepTitle(arg.code));

        vsRoot.editText(R.id.from_date).setText(DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE));
        vsRoot.editText(R.id.to_date).setText(DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE));

        vsRoot.makeDatePicker(R.id.from_date);
        vsRoot.makeDatePicker(R.id.to_date);
        vsRoot.id(R.id.btn_show_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReport();
            }
        });
        multiSpinner();
    }

    @SuppressWarnings("ConstantConditions")
    private void multiSpinner() {
        ArgReport arg = getArgReport();
        if (!arg.withSpinner) {
            return;
        }

        Scope scope = arg.getScope();
        MyArray<Warehouse> warehouses = scope.ref.getWarehouses();
        MyArray<MultiSelectionSpinner.MultiSelection> wMs = warehouses.map(new MyMapper<Warehouse, MultiSelectionSpinner.MultiSelection>() {
            @Override
            public MultiSelectionSpinner.MultiSelection apply(Warehouse warehouse) {
                return new MultiSelectionSpinner.MultiSelection(Integer.parseInt(warehouse.id), warehouse.name);
            }
        });

        LinearLayout ll = vsRoot.id(R.id.top_ll);
        ll.removeAllViews();
        ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msSpinner = new MultiSelectionSpinner(getActivity());
        msSpinner.setLayoutParams(lpView);
        msSpinner.setItems(wMs);
        msSpinner.selectedAll(true);
        ll.addView(msSpinner);
        vsRoot.id(R.id.ll_warehouse).setVisibility(View.VISIBLE);
    }

    private void showReport() {
        String fromDate = ((EditText) vsRoot.id(R.id.from_date)).getText().toString();
        String toDate = ((EditText) vsRoot.id(R.id.to_date)).getText().toString();
        if (TextUtils.isEmpty(fromDate) || TextUtils.isEmpty(toDate)) {
            Mold.makeSnackBar(getActivity(), R.string.report_all_field_requared).show();
        } else {
            String wIds = "";
            if (msSpinner != null) {
                wIds = msSpinner.getSelectedIds().mkString(",");
            }

            ArgReport arg = getArgReport();
            MyArray<String> dates = arg.data.append(MyArray.from(fromDate, toDate, wIds));
            ReportViewFragment.open(new ArgReport(arg, arg.code, dates));
        }
    }
}
