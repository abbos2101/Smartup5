package uz.greenwhite.smartup5_trade.m_deal.variable.agree;


import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VDealAgree extends VariableLike {

    public final Product product;
    public final String oldCurValue;
    public final String oldNewValue;
    public final String oldPeriod;
    public final ValueBigDecimal curValue;
    public final ValueBigDecimal newValue;
    public final ValueSpinner period;
    public final CharSequence title;

    public VDealAgree(Product product,
                      CharSequence title,
                      String oldCurValue,
                      String oldNewValue,
                      String oldPeriod,
                      BigDecimal curValue,
                      BigDecimal newValue,
                      ValueSpinner period) {
        if (product == null) {
            throw AppError.NullPointer();
        }

        this.product = product;
        this.title = title;

        this.oldCurValue = oldCurValue;
        this.oldNewValue = oldNewValue;
        this.oldPeriod = oldPeriod;
        this.curValue = new ValueBigDecimal(10, product.measureScale);
        this.newValue = new ValueBigDecimal(10, product.measureScale);
        this.period = period;

        this.curValue.setValue(curValue);
        this.newValue.setValue(newValue);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(curValue, newValue, period).toSuper();
    }

    public boolean hasValue() {
        return curValue.nonEmpty() || newValue.nonEmpty();
    }

    public static final MyMapper<VDealAgree, String> KEY_ADAPTER = new MyMapper<VDealAgree, String>() {
        @Override
        public String apply(VDealAgree val) {
            return val.product.id;
        }
    };

}
