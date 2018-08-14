package uz.greenwhite.smartup5_trade.m_shipped.variable.attach;// 27.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSAttachForm extends VSDealForm {

    public final ValueBoolean contract;
    public final ValueBoolean invoice;
    public final ValueBoolean powerOfAttorney;

    public VSAttachForm(VisitModule module,
                        ValueBoolean contract,
                        ValueBoolean invoice,
                        ValueBoolean powerOfAttorney) {
        super(module);
        this.contract = contract;
        this.invoice = invoice;
        this.powerOfAttorney = powerOfAttorney;
    }

    @Override
    public boolean hasValue() {
        return contract.getValue() || invoice.getValue() || powerOfAttorney.getValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(contract, invoice, powerOfAttorney).toSuper();
    }
}
