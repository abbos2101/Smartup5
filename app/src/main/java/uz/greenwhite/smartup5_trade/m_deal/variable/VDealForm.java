package uz.greenwhite.smartup5_trade.m_deal.variable;// 30.06.2016

import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public abstract class VDealForm extends VForm {

    public VDealForm(VisitModule module, String code) {
        super(code, module);
    }

    public VDealForm(VisitModule module) {
        this(module, String.valueOf(module.id));
    }

    public CharSequence getTitle() {
        return ((VisitModule) tag).name;
    }

    public CharSequence getSubtitle() {
        return "";
    }

    public int getFormId() {
        return ((VisitModule) tag).id;
    }
}
