package uz.greenwhite.smartup5_trade.m_shipped.variable.order;// 09.09.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SOrder;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSDealOrderModule extends VSDealModule {

    public final ValueArray<VSDealOrderForm> forms;

    public VSDealOrderModule(VisitModule module, ValueArray<VSDealOrderForm> forms) {
        super(module);
        this.forms = forms;
        attachOrderModule();
    }

    public void attachOrderModule() {
        for (VSDealOrderForm f : forms.getItems()) {
            f.orderModule = this;
        }
    }

    public BigDecimal getTotalOrder() {
        return forms.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VSDealOrderForm>() {
            @Override
            public BigDecimal apply(BigDecimal acc, VSDealOrderForm val) {
                return acc.add(val.getTotalSum());
            }
        });
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return forms.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VSDealOrderForm form : forms.getItems()) {
            if (form.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReturn(){
        for (VSDealOrderForm form : forms.getItems()) {
            if (form.hasReturn()) {
                return true;
            }
        }
        return false;
    }

    public MyArray<SOrder> convertSOrder() {
        List<SOrder> result = new ArrayList<>();
        for (VSDealOrderForm f : this.forms.getItems()) {
            for (VSDealOrder o : f.orders.getItems()) {
                SOrder order = o.order;
                result.add(new SOrder(order.productId, order.warehouseId, order.priceTypeId,
                        order.originQuant, o.getDeliverQuantity(), o.getReturnQuantity(),
                        order.soldPrice, order.marginKind, order.marginValue, order.currencyId, order.cardCode));
            }
        }
        return MyArray.from(result);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return forms.getItems().toSuper();
    }
}
