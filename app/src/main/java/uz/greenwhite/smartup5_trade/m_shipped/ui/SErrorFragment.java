package uz.greenwhite.smartup5_trade.m_shipped.ui;// 07.07.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.variable.error.VSDealErrorForm;

public class SErrorFragment extends MoldContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_error);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        VSDealErrorForm form = ShippedUtil.getDealForm(this);
        this.vsRoot.textView(R.id.tv_error).setText(form.error);
    }
}