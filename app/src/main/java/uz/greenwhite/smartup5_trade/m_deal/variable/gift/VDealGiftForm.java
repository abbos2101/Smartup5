package uz.greenwhite.smartup5_trade.m_deal.variable.gift;// 25.10.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class VDealGiftForm extends VDealForm {

    public final Warehouse warehouse;
    public final ValueArray<VDealGift> gifts;

    public VDealGiftForm(VisitModule module, Warehouse warehouse, ValueArray<VDealGift> gifts) {
        super(module, "" + module.id + ":" + warehouse.id);
        this.warehouse = warehouse;
        this.gifts = gifts;
    }

    @Override
    public CharSequence getTitle() {
        return warehouse.name;
    }

    @Override
    public boolean hasValue() {
        for (VDealGift val : gifts.getItems()) {
            if (val.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return gifts.getItems().toSuper();
    }
}
