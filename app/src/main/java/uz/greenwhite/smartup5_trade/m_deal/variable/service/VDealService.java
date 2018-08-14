package uz.greenwhite.smartup5_trade.m_deal.variable.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;

public class VDealService extends VariableLike {

    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    public final Product product;
    public final String productUnitId;
    public final RoundModel roundModel;

    public final ValueBigDecimal quant, margin, realPrice;
    public final ValueSpinner discountSpinner;

    public VDealService(@NonNull Product product,
                        @NonNull String productUnitId,
                        @NonNull RoundModel roundModel,
                        @NonNull BigDecimal price,
                        @Nullable BigDecimal quant,
                        @Nullable BigDecimal margin,
                        @Nullable ValueSpinner discountSpinner) {
        this.product = product;
        this.productUnitId = productUnitId;
        this.roundModel = roundModel;
        this.discountSpinner = discountSpinner;

        this.realPrice = new ValueBigDecimal(20, 6);
        this.quant = new ValueBigDecimal(20, 0);
        this.margin = new ValueBigDecimal(10, 6);

        this.realPrice.setValue(roundModel.fixAmount(price));
        this.quant.setValue(quant);
        this.margin.setValue(Util.nvl(margin, BigDecimal.ZERO));
    }

    //----------------------------------------------------------------------------------------------

    public BigDecimal getTotalOrderPrice() {
        return roundModel.fixAmount(quant.getQuantity().multiply(realPrice.getQuantity()));
    }

    @SuppressWarnings({"BigDecimalMethodWithoutRoundingCalled", "UnnecessaryLocalVariable"})
    public BigDecimal getTotalPriceWithMargin() {
        if (margin.nonZero()) {
            BigDecimal orderPrice = getTotalOrderPrice();
            return roundModel.fixAmount(margin.getQuantity().multiply(orderPrice).divide(HUNDRED));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal orderPrice = getTotalOrderPrice();
        BigDecimal marginPrice = getTotalPriceWithMargin();
        return roundModel.fixAmount(orderPrice.add(marginPrice));
    }

    public boolean hasValue() {
        return quant.nonZero();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(quant, realPrice, margin).toSuper();
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence tvTitleInfo() {
        ShortHtml html = UI.html().v(product.name).v(" ");
        if (this.margin.nonZero()) {
            String discount = this.margin.isZero() ? null : this.margin.getText() + "%";
            if (this.margin.getValue().compareTo(BigDecimal.ZERO) > 0) {
                discount = "+" + discount;
            }
            html.b().v("  (").v(discount).v(")").b();
        }
        return html.html();
    }

    public String tvPrice() {
        return NumberUtil.formatMoney(this.realPrice.getQuantity());
    }
}
