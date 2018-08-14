package uz.greenwhite.smartup5_trade.m_shipped.ui;// 27.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.variable.attach.VSAttachForm;

public class SAttachFragment extends MoldContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.sdeal_attach);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VSAttachForm form = ShippedUtil.getDealForm(this);

        vsRoot.bind(R.id.cb_contract, form.contract);
        vsRoot.bind(R.id.cb_invoce, form.invoice);
        vsRoot.bind(R.id.cb_power_of_attorney, form.powerOfAttorney);

        SDealData data = Mold.getData(getActivity());
        boolean hasEdit = data.hasEdit();

        vsRoot.id(R.id.cb_contract).setEnabled(hasEdit);
        vsRoot.id(R.id.cb_invoce).setEnabled(hasEdit);
        vsRoot.id(R.id.cb_power_of_attorney).setEnabled(hasEdit);
    }
}
