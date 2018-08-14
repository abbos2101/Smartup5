package uz.greenwhite.smartup5_trade.m_deal.variable.attach;// 25.10.2016

import android.support.annotation.Nullable;
import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VAttachForm extends VDealForm {

    @Nullable
    private VDeal vDeal;

    public VAttachForm(VisitModule module) {
        super(module);
    }

    public void setVariableDeal(VDeal vDeal) {
        if (this.vDeal == null)
            this.vDeal = vDeal;
    }

    @Override
    public boolean hasValue() {
        if (vDeal != null) {
            if (vDeal.header.contractNumber != null && !TextUtils.isEmpty(vDeal.header.contractNumber.getValue().code)) {
                return true;

            } else if (vDeal.header.deliveryDate.nonEmpty()) {
                return true;

            } else if (vDeal.header.expeditor != null && !TextUtils.isEmpty(vDeal.header.expeditor.getValue().code)) {
                return true;

            } else if (vDeal.header.agents != null && !TextUtils.isEmpty(vDeal.header.agents.getValue().code)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
