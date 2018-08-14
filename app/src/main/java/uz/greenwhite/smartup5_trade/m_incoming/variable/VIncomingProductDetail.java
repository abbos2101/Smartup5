package uz.greenwhite.smartup5_trade.m_incoming.variable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class VIncomingProductDetail extends VariableLike {

    public int number = 0;

    public final ValueString cardNumber;
    public final ValueBigDecimal manufacturePrice;
    public final ValueBigDecimal quantity;
    public final ValueString expireDate;
    public final ValueBigDecimal price;

    public VIncomingProductDetail(String cardNumber,
                                  BigDecimal manufacturePrice,
                                  BigDecimal quantity,
                                  String expireDate,
                                  BigDecimal price) {
        this.cardNumber = new ValueString(100, cardNumber);
        this.manufacturePrice = new ValueBigDecimal(20, 9);
        this.quantity = new ValueBigDecimal(20, 9);
        this.expireDate = new ValueString(20, expireDate);
        this.price = new ValueBigDecimal(20, 9);

        this.manufacturePrice.setValue(manufacturePrice);
        this.quantity.setValue(quantity);
        this.price.setValue(price);
    }

    public boolean hasValue() {
        return quantity.nonZero() && price.nonEmpty();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult errorResult = super.getError();
        if (errorResult.isError()) return errorResult;

        if ((quantity.nonZero() && price.isEmpty()) || (quantity.isZero() && price.nonEmpty())) {
            return ErrorResult.make(DS.getString(R.string.incoming_error_1));
        }

        return ErrorResult.NONE;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(cardNumber, manufacturePrice, quantity, expireDate, price).toSuper();
    }
}
