package uz.greenwhite.smartup5_trade.m_deal.variable.stock;// 29.09.2016

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealStock;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealStockModule extends VDealModule {

    public final VDealStockForm form;

    public VDealStockModule(VisitModule module, VDealStockForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealStock> r = new ArrayList<>();

        for (VDealStockProduct s : form.stockProducts.getItems()) {
            String productId = s.product.id;
            for (VDealStock val : s.stocksRows.getItems()) {
                if (val.hasValue()) {
                    String stock = val.stock.getText();
                    String expireDate = val.expireDate.getText();
                    r.add(new DealStock(productId, stock, "", expireDate));
                }
            }
        }
        return new DealStockModule(MyArray.from(r));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form.stockProducts.getItems().isEmpty()) {
            return MyArray.emptyArray();
        }
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }
}
