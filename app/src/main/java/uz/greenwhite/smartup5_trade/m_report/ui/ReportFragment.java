package uz.greenwhite.smartup5_trade.m_report.ui;// 16.11.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleMenu;

public class ReportFragment extends MoldContentFragment {

    private static final MyMapper<Report, Integer> FORM_KEY_ADAPTER = new MyMapper<Report, Integer>() {
        @Override
        public Integer apply(Report item) {
            return item.id;
        }
    };

    protected ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.report);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.report);
    }

    protected void onItemClick(Report item) {

    }

    protected void setForms(ArgSession arg, MyArray<Report> items) {
        items = RoleMenu.sortForms(arg, RoleMenu.REPORT, items, FORM_KEY_ADAPTER);

        ViewGroup vg = vsRoot.viewGroup(R.id.ll_report_row);
        vg.removeAllViews();
        for (final Report item : items) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.report_row);
            vs.imageView(R.id.iv_rep_icon).setImageDrawable(item.icon);
            vs.textView(R.id.tv_rep_title).setText(item.title);
            vs.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(item);
                }
            });
            vg.addView(vs.view);
        }
    }
}
