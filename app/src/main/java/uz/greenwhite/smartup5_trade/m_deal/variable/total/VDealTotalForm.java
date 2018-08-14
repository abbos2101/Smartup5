package uz.greenwhite.smartup5_trade.m_deal.variable.total;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealTotalForm extends VDealForm {

    public final MyArray<VDealOrderModule> orders;
    public final MyArray<VDealActionModule> actions;
    public final MyArray<VOverloadModule> overloads;
    public final MyArray<VDealGiftModule> gifts;

    public VDealTotalForm(VisitModule module,
                          MyArray<VDealOrderModule> orders,
                          MyArray<VDealActionModule> actions,
                          MyArray<VOverloadModule> overloads,
                          MyArray<VDealGiftModule> gifts) {
        super(module);
        this.orders = orders;
        this.actions = actions;
        this.overloads = overloads;
        this.gifts = gifts;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
