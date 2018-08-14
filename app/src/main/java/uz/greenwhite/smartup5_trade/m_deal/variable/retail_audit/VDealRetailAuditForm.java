package uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditSet;

public class VDealRetailAuditForm extends VDealForm {

    public final RetailAuditSet retailAuditSet;
    public final ValueArray<VDealRetailAudit> retailAudits;

    public VDealRetailAuditForm(VisitModule module, RetailAuditSet retailAuditSet, ValueArray<VDealRetailAudit> retailAudits) {
        super(module, "" + module.id + ":" + retailAuditSet.retailAuditSetId);
        this.retailAuditSet = retailAuditSet;
        this.retailAudits = retailAudits;
    }

    @Override
    public CharSequence getTitle() {
        return retailAuditSet.name;
    }

    @Override
    public boolean hasValue() {
        return retailAudits.getItems().contains(new MyPredicate<VDealRetailAudit>() {
            @Override
            public boolean apply(VDealRetailAudit vDealRetailAudit) {
                return vDealRetailAudit.hasValue();
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return retailAudits.getItems().toSuper();
    }
}
