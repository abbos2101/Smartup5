package uz.greenwhite.smartup5_trade.m_deal.variable.gift;// 25.10.2016

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGift;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealGiftModule extends VDealModule {

    public final ValueArray<VDealGiftForm> forms;

    public VDealGiftModule(VisitModule module, ValueArray<VDealGiftForm> forms) {
        super(module);
        this.forms = forms;
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<DealGift> result = new ArrayList<>();
        for (VDealGiftForm f : forms.getItems()) {
            for (VDealGift v : f.gifts.getItems()) {
                if (v.hasValue()) {
                    String productId = v.product.id;
                    String warehouseId = f.warehouse.id;
                    BigDecimal quantity = v.getQuantity();
                    MyArray<CardQuantity> charges = v.balanceOfWarehouse.getCharges(v.card, v.formKey);

                    result.add(new DealGift(productId, warehouseId, quantity, charges, v.productUnitId));
                }
            }
        }
        return new DealGiftModule(MyArray.from(result));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return forms.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealGiftForm val : forms.getItems()) {
            if (val.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return forms.getItems().toSuper();
    }
}
