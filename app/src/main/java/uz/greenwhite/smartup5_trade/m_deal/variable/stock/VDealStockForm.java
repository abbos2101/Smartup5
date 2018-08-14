package uz.greenwhite.smartup5_trade.m_deal.variable.stock;// 29.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealStockForm extends VDealForm {

    public ValueArray<VDealStockProduct> stockProducts;

    public VDealStockForm(VisitModule module, ValueArray<VDealStockProduct> stockProducts) {
        super(module);
        this.stockProducts = stockProducts;
    }

    @Override
    public boolean hasValue() {
        for (VDealStockProduct stock : stockProducts.getItems()) {
            if (stock.hasValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return stockProducts.getItems().toSuper();
    }
}
