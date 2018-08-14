package uz.greenwhite.smartup5_trade.m_deal.variable.returns;// 06.10.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VDealReturn extends VariableLike {

    public final Product product;
    public final ValueBigDecimal quantity;
    public final ValueBigDecimal price;
    public final ValueString expiryDate;
    public final ValueString cardCode;

    public VDealReturn(Product product,
                       BigDecimal quantity,
                       BigDecimal price,
                       String expiryDate,
                       String cardCode) {
        this.product = product;
        this.quantity = new ValueBigDecimal(20, Math.min(product.measureScale, 6));
        this.price = new ValueBigDecimal(20, 6);
        this.expiryDate = new ValueString(10);
        this.cardCode = new ValueString(50);

        this.quantity.setValue(quantity);
        this.price.setValue(price);
        this.expiryDate.setValue(expiryDate);
        this.cardCode.setValue(cardCode);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(quantity, price, expiryDate, cardCode).toSuper();
    }

    public boolean hasValue() {
        return quantity.nonZero() ||
                price.nonEmpty() ||
                expiryDate.nonEmpty() ||
                cardCode.nonEmpty();
    }

    public boolean hasValueToSave() {
        return quantity.nonZero() && price.nonEmpty();
    }


    @Override
    public ErrorResult getError() {

        if (quantity.nonZero() && price.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.deal_price_return));

        } else if (quantity.isZero() && price.nonEmpty()) {
            return ErrorResult.make(DS.getString(R.string.deal_quantity_return));
        }

        return super.getError();
    }
}
