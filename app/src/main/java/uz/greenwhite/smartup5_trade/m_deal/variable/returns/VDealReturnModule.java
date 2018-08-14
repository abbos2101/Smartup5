package uz.greenwhite.smartup5_trade.m_deal.variable.returns;// 06.10.2016

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealReturn;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealReturnModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealReturnModule extends VDealModule {

    public final ValueArray<VDealReturnForm> forms;

    public VDealReturnModule(VisitModule module, ValueArray<VDealReturnForm> forms) {
        super(module);
        this.forms = forms;
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealReturn> result = new ArrayList<>();
        for (VDealReturnForm f : forms.getItems()) {
            for (VDealReturn r : f.returns.getItems()) {
                if (r.hasValueToSave()) {
                    String quantity = r.quantity.getQuantity().toPlainString();
                    String price = r.price.getQuantity().toPlainString();
                    String expiryDate = r.expiryDate.getText();
                    String cardCode = r.cardCode.getText();
                    result.add(new DealReturn(f.warehouse.id, r.product.id, quantity, price, expiryDate, cardCode));
                }
            }
        }
        return new DealReturnModule(MyArray.from(result));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return forms.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealReturnForm f : forms.getItems()) {
            if (f.hasValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return forms.getItems().toSuper();
    }

    @Override
    public ErrorResult getError() {
        boolean hasValue = false;
        for (VDealReturnForm f : forms.getItems()) {
            boolean formHasValue = f.hasValue();
            if (!hasValue && formHasValue) {
                hasValue = true;
            } else if (hasValue && formHasValue) {
                return ErrorResult.make("Error return order");
            }
        }
        return super.getError();
    }
}
