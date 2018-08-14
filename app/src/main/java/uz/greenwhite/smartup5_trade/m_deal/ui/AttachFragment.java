package uz.greenwhite.smartup5_trade.m_deal.ui;// 25.10.2016

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class AttachFragment extends DealFormContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_attach);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.attach);
        DealData data = Mold.getData(getActivity());

        if (data.vDeal.header.contractNumber != null) {
            vsRoot.bind(R.id.sp_contract, data.vDeal.header.contractNumber);
            vsRoot.id(R.id.ll_contract).setVisibility(View.VISIBLE);
        } else {
            vsRoot.id(R.id.ll_contract).setVisibility(View.GONE);
        }

        if (data.vDeal.dealRef.setting.deal.requiredDeliveryDate) {
            CharSequence deliveryDateText = UI.html().v(DS.getString(R.string.deal_delivery_date)).fRed().v("*").fRed().html();
            vsRoot.textView(R.id.tv_delivery_date).setText(deliveryDateText);
        }

        if (data.vDeal.header.expeditor != null) {
            vsRoot.id(R.id.ll_expeditor).setVisibility(View.VISIBLE);

            final ValueSpinner expeditor = data.vDeal.header.expeditor;
            vsRoot.textView(R.id.tv_expeditor).setText(expeditor.getValue().name);
            vsRoot.id(R.id.tv_expeditor).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View textView) {
                    UI.popup()
                            .option(expeditor.options, new PopupBuilder.CommandFacade<SpinnerOption>() {
                                @NonNull
                                @Override
                                public CharSequence getName(SpinnerOption val) {
                                    return val.name;
                                }

                                @Override
                                public void apply(SpinnerOption val) {
                                    expeditor.setValue(val);
                                    ((TextView) textView).setText(val.name);
                                }
                            }).show(textView);
                }
            });
        } else {
            vsRoot.id(R.id.ll_expeditor).setVisibility(View.GONE);
        }

        if (data.vDeal.header.agents != null) {
            vsRoot.id(R.id.ll_agent).setVisibility(View.VISIBLE);
            final ValueSpinner agents = data.vDeal.header.agents;

            vsRoot.textView(R.id.tv_agent).setText(agents.getValue().name);
            vsRoot.id(R.id.tv_agent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View textView) {
                    UI.popup()
                            .option(agents.options, new PopupBuilder.CommandFacade<SpinnerOption>() {
                                @NonNull
                                @Override
                                public CharSequence getName(SpinnerOption val) {
                                    return val.name;
                                }

                                @Override
                                public void apply(SpinnerOption val) {
                                    agents.setValue(val);
                                    ((TextView) textView).setText(val.name);
                                }
                            }).show(textView);
                }
            });

        } else {
            vsRoot.id(R.id.ll_agent).setVisibility(View.GONE);
        }

        if (data.vDeal.dealRef.setting.deal.deliveryDateAllow || data.vDeal.header.deliveryDate.nonEmpty()) {
            vsRoot.bind(R.id.et_shipped_deal, data.vDeal.header.deliveryDate);
            vsRoot.makeDatePicker(R.id.et_shipped_deal, true);

            vsRoot.id(R.id.ll_delivery_date).setVisibility(View.VISIBLE);
        } else {
            vsRoot.id(R.id.ll_delivery_date).setVisibility(View.GONE);
        }
    }
}
