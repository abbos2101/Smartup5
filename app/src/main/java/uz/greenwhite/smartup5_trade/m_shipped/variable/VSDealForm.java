package uz.greenwhite.smartup5_trade.m_shipped.variable;// 09.09.2016

import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public abstract class VSDealForm extends VForm {

    public VSDealForm(VisitModule module, String code) {
        super(code, module);
    }

    public VSDealForm(VisitModule module) {
        this(module, String.valueOf(module.id));
    }

    public CharSequence getTitle() {
        return ((VisitModule) tag).name;
    }

    public final int getFormId() {
        return ((VisitModule) tag).id;
    }
}
