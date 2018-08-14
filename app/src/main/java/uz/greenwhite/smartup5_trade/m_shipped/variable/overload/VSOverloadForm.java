package uz.greenwhite.smartup5_trade.m_shipped.variable.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSOverloadForm extends VSDealForm{

    public final MyArray<SOverload> overloads;

    public VSOverloadForm(VisitModule module, MyArray<SOverload> overloads) {
        super(module);
        this.overloads = overloads;
    }

    @Override
    public boolean hasValue() {
        return overloads.nonEmpty();
    }

    @Override
    public boolean modified() {
        return overloads.nonEmpty();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
