package uz.greenwhite.smartup5_trade.m_shipped.ui;// 27.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_outlet.bean.ReturnReason;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReason;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReasonForm;

public class SReturnReasonFragment extends MoldContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.sdeal_reason);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VSReturnReasonForm form = ShippedUtil.getDealForm(this);

        RadioGroup rg = new RadioGroup(getActivity());
        rg.setOrientation(RadioGroup.VERTICAL);

        radioButton(rg, new VSReturnReason(ReturnReason.NOT_SELECT, !form.hasValue()));

        for (VSReturnReason item : form.reasones.getItems()) {
            radioButton(rg, item);
        }
        vsRoot.viewGroup(R.id.ll_reason_content).addView(rg);
    }

    private void radioButton(RadioGroup rg, VSReturnReason item) {
        int id = Integer.parseInt(item.returnReason.id);
        RadioButton rb = new RadioButton(getActivity());
        UI.bind(rb, item.check);
        rb.setId(id + (id < 0 ? 10 : 100));
        rb.setText(item.returnReason.name);
        rb.setTag(item);
        rg.addView(rb);
    }
}
