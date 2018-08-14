package uz.greenwhite.smartup5_trade.m_deal.variable.service;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealService;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealServiceModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealServiceModule extends VDealModule {

    public final ValueArray<VDealServiceForm> forms;

    public VDealServiceModule(VisitModule module, ValueArray<VDealServiceForm> forms) {
        super(module);
        this.forms = forms;
    }

    @NonNull
    public BigDecimal getTotalOrderSum() {
        return forms.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealServiceForm>() {
            @Override
            public BigDecimal apply(BigDecimal acc, VDealServiceForm val) {
                return acc.add(val.getTotalPrice());
            }
        });
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<DealService> l = new ArrayList<>();
        for (VDealServiceForm f : forms.getItems()) {
            for (VDealService v : f.services.getItems()) {
                if (v.hasValue()) {
                    String productId = v.product.id;
                    String priceTypeId = f.priceType.id;
                    BigDecimal realPrice = v.realPrice.getQuantity();
                    BigDecimal quant = v.quant.getQuantity();
                    BigDecimal margin = v.margin.getQuantity();
                    String currencyId = f.currency.currencyId;
                    l.add(new DealService(productId, priceTypeId, realPrice,
                            quant, margin, currencyId, v.productUnitId));
                }
            }
        }
        return new DealServiceModule(MyArray.from(l));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return forms.getItems().filter(new MyPredicate<VDealServiceForm>() {
            @Override
            public boolean apply(VDealServiceForm vDealServiceForm) {
                return vDealServiceForm.enable;
            }
        }).toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealServiceForm f : forms.getItems()) {
            if (f.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return forms.getItems().toSuper();
    }
}
