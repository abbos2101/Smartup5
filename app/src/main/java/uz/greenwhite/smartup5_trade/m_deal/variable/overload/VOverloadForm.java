package uz.greenwhite.smartup5_trade.m_deal.variable.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VOverloadForm extends VDealForm {

    public final ValueArray<VOverload> overloads;

    public VOverloadForm(VisitModule module, ValueArray<VOverload> overloads) {
        super(module);
        this.overloads = overloads;
    }

    @Override
    public boolean hasValue() {
        return overloads.getItems().contains(new MyPredicate<VOverload>() {
            @Override
            public boolean apply(VOverload vOverload) {
                return vOverload.hasValue();
            }
        });
    }

    public void applyOverload() {
        for (VOverload overload : overloads.getItems()) {
            for (VOverloadRule rule : overload.rules.getItems()) {
                rule.unBookQuantity();
                if (rule.isCanUse()) {
                    for (VOverloadLoad load : rule.loads.getItems()) {
                        if (load.isTaken.getValue()) {
                            rule.bookQuantity();
                        }
                    }
                }
            }
        }
    }


    @Override
    protected MyArray<Variable> gatherVariables() {
        return overloads.getItems().toSuper();
    }
}
