package uz.greenwhite.smartup5_trade.m_deal.variable.stock;// 29.09.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VDealStock extends VariableLike {

    public final ValueBigDecimal stock;
    public final ValueString expireDate;
    public final int orderNo;

    public VDealStock(Product product, BigDecimal stock, String expireDate, int orderNo) {
        this.stock = new ValueBigDecimal(20, Math.min(product.measureScale, 6));
        this.expireDate = new ValueString(20, expireDate);

        this.stock.setValue(stock);
        this.orderNo = orderNo;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(stock, expireDate).toSuper();
    }

    public boolean hasValue() {
        return stock.nonEmpty();
    }
}
