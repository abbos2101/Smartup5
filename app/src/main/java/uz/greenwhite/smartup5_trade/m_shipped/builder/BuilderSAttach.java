package uz.greenwhite.smartup5_trade.m_shipped.builder;// 09.09.2016

import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SAttach;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.attach.VSAttachForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.attach.VSAttachModule;

public class BuilderSAttach {

    public final SDealRef sDealRef;
    public final VisitModule module;
    public final SAttach initialAttach;

    public BuilderSAttach(SDealRef sDealRef) {
        this.sDealRef = sDealRef;
        this.module = new VisitModule(VisitModule.M_ATTACH, false);
        this.initialAttach = sDealRef.holder.deal.attach;
    }

    private VSAttachForm makeForm() {
        ValueBoolean contract = new ValueBoolean();
        ValueBoolean invoice = new ValueBoolean();
        ValueBoolean powerOfAttorney = new ValueBoolean();
        contract.setValue(this.initialAttach.contract);
        invoice.setValue(this.initialAttach.invoice);
        powerOfAttorney.setValue(this.initialAttach.powerOfAttorney);
        return new VSAttachForm(module, contract, invoice, powerOfAttorney);
    }

    public VSAttachModule build() {
        return new VSAttachModule(makeForm());
    }

}
