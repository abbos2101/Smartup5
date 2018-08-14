package uz.greenwhite.smartup5_trade.m_deal.variable.overload;


import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverload;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VOverloadModule extends VDealModule {

    public final VOverloadForm form;

    public VOverloadModule(VisitModule module, VOverloadForm form) {
        super(module);
        this.form = form;
    }

    @NonNull
    public BigDecimal getTotalSum() {
        if (form == null || form.overloads.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return form.overloads.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VOverload>() {
            @Override
            public BigDecimal apply(BigDecimal acc, VOverload val) {
                return acc.add(val.getTotalSum());
            }
        });
    }


    @Override
    public DealModule convertToDealModule() {
        if (form == null) return null;

        ArrayList<DealOverload> result = new ArrayList<>();
        for (VOverload overload : form.overloads.getItems()) {
            for (VOverloadRule rule : overload.rules.getItems()) {
                if (rule.isCanUse()) {
                    for (VOverloadLoad load : rule.loads.getItems()) {
                        if (load.isTaken.getValue()) {
                            for (VOverloadProduct product : load.getProducts()) {
                                PriceType priceType = product.getPriceType();
                                ProductPrice productPrice = product.getProductPrice();

                                MyArray<CardQuantity> charges = product.getBalanceOfWarehouse().
                                        getCharges(product.card, product.overloadKey);

                                result.add(new DealOverload(
                                        overload.overload.overloadId,
                                        product.warehouseId.getValue(),
                                        priceType.id,
                                        productPrice.cardCode,
                                        load.load.loadId,
                                        product.loadProduct.productId,
                                        product.getQuantity(),
                                        productPrice.price,
                                        charges,
                                        priceType.currencyId,
                                        product.productUnitId));
                            }
                        }
                    }
                }
            }
        }
        return new DealOverloadModule(MyArray.from(result));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form != null && form.overloads.getItems().isEmpty()) {
            return MyArray.emptyArray();
        }
        return MyArray.from(form).filterNotNull().toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }


    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).filterNotNull().toSuper();
    }
}
