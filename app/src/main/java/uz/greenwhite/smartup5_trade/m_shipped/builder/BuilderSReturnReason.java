package uz.greenwhite.smartup5_trade.m_shipped.builder;// 09.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_outlet.bean.ReturnReason;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReason;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReasonForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReasonModule;

public class BuilderSReturnReason {

    public final SDealRef sDealRef;
    public final VisitModule module;

    public BuilderSReturnReason(SDealRef sDealRef) {
        this.sDealRef = sDealRef;
        this.module = new VisitModule(VisitModule.M_REASON, false);
    }

    private VSReturnReasonForm makeForm() {
        MyArray<ReturnReason> reasons = sDealRef.getReasons();
        MyArray<VSReturnReason> result = reasons.map(new MyMapper<ReturnReason, VSReturnReason>() {
            @Override
            public VSReturnReason apply(ReturnReason reason) {
                return new VSReturnReason(reason, sDealRef.holder.deal.returnReasonId.equals(reason.id));
            }
        });
        return new VSReturnReasonForm(module, new ValueArray<>(result));
    }

    public VSReturnReasonModule build() {
        return new VSReturnReasonModule(makeForm());
    }

}
