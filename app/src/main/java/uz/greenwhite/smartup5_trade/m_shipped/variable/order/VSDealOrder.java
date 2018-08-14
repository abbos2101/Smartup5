package uz.greenwhite.smartup5_trade.m_shipped.variable.order;// 09.09.2016

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SOrder;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDBalance;

public class VSDealOrder extends VariableLike {

    public static final String MARGIN_KIND_PERCENT = "P";

    public final Product product;
    public final SOrder order;
    public final RoundModel roundModel;
    public final BigDecimal availQuant;
    public final ValueBigDecimal deliverBox;
    public final ValueBigDecimal deliverQuant;
    public final ValueBigDecimal returnBox;
    public final ValueBigDecimal returnQuant;

    public BigDecimal otherOrdersQuantity = BigDecimal.ZERO;

    public final ValueSpinner spType;

    public VSDealOrder(Product product, SOrder order, RoundModel roundModel, SDBalance sdBalance, ValueSpinner spType) {
        this.product = product;
        this.order = order;
        this.roundModel = roundModel;
        this.spType = spType;

        BigDecimal availQuant = sdBalance.getQuant(this.order.warehouseId, this.product.id);
        this.availQuant = availQuant.add(this.order.originQuant);

        Tuple2 dq = getProductQuant(this.order.deliverQuant);
        Tuple2 rq = getProductQuant(this.order.returnQuant);

        this.deliverBox = (ValueBigDecimal) dq.first;
        this.deliverQuant = (ValueBigDecimal) dq.second;

        this.returnBox = (ValueBigDecimal) rq.first;
        this.returnQuant = (ValueBigDecimal) rq.second;
    }

    private Tuple2 getProductQuant(BigDecimal quantity) {
        boolean isBox = false;
        boolean isQuant = false;
        BigDecimal boxPart = null;
        BigDecimal quantPart;

        if (this.product.isInputBox()) {
            isBox = true;
            boxPart = this.product.getBoxPart(quantity);
            quantPart = this.product.getQuantPart(quantity);
            if (this.product.isInputQuant() || (quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0)) {
                isQuant = true;
            }
        } else {
            isQuant = true;
            quantPart = quantity;
        }

        ValueBigDecimal box = null;
        if (isBox) {
            box = new ValueBigDecimal(20, 0);
            box.setValue(BigDecimal.ZERO.compareTo(boxPart) != 0 ? boxPart : null);
        }

        ValueBigDecimal quant = null;
        if (isQuant) {
            quant = new ValueBigDecimal(20, Math.min(this.product.measureScale, 6));
            quant.setValue(BigDecimal.ZERO.compareTo(quantPart) != 0 ? quantPart : null);
        }

        return new Tuple2(box, quant);
    }

    public String getDiscount() {
        String discount = "";
        if (MARGIN_KIND_PERCENT.equals(order.marginKind)) {
            if (this.order.marginValue.compareTo(BigDecimal.ZERO) != 0) {
                discount = this.order.marginValue.toPlainString() + "%";
            }
        } else {
            discount = this.order.marginValue.toPlainString();
        }

        if (!TextUtils.isEmpty(discount) && this.order.marginValue.compareTo(BigDecimal.ZERO) > 0) {
            discount = "+" + discount;
        }

        return discount;
    }

    public BigDecimal getAvailQuant() {
        return this.availQuant.add(otherOrdersQuantity);
    }

    public BigDecimal getQuantity() {
        BigDecimal r = getDeliverQuantity().subtract(getReturnQuantity());
        r = this.order.originQuant.add(r);
        if (BigDecimal.ZERO.compareTo(r) > 0) {
            r = BigDecimal.ZERO;
        }
        return r;
    }

    public BigDecimal getTotalSum() {
        boolean isPercentMargin = order.marginKind.equalsIgnoreCase(MARGIN_KIND_PERCENT);
        if (isPercentMargin) return getAmount().add(getDiscountAmount());
        else return getAmountWithMargin();
    }

    public BigDecimal getDeliverQuantity() {
        BigDecimal boxPart = BigDecimal.ZERO;
        BigDecimal quantPart = BigDecimal.ZERO;

        if (this.deliverBox != null) {
            boxPart = this.deliverBox.getQuantity();
        }
        if (this.deliverQuant != null) {
            quantPart = this.deliverQuant.getQuantity();
        }

        return product.getBoxQuant(boxPart, quantPart);
    }

    public BigDecimal getReturnQuantity() {
        BigDecimal boxPart = BigDecimal.ZERO;
        BigDecimal quantPart = BigDecimal.ZERO;

        if (this.returnBox != null) {
            boxPart = this.returnBox.getQuantity();
        }
        if (this.returnQuant != null) {
            quantPart = this.returnQuant.getQuantity();
        }

        return product.getBoxQuant(boxPart, quantPart);
    }

    //  gets total amount of sold products  (Ec: Total = Quantity * Price)
    private BigDecimal getAmount() {
        return roundModel.fixAmount(getQuantity().multiply(order.soldPrice));
    }

    //  gets total amount of sold products with margin (Ec: Total = Quantity * (Price + Margin))
    private BigDecimal getAmountWithMargin() {
        return roundModel.fixAmount(getQuantity().multiply((order.soldPrice.add(order.marginValue))));
    }


    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private BigDecimal getDiscountAmount() {
        BigDecimal marginValue = order.marginValue;

        if (marginValue != null && marginValue.compareTo(BigDecimal.ZERO) != 0) {
            return marginValue.multiply(getAmount()).divide(HUNDRED);
            //TODO return roundModel.fixAmount(marginValue.multiply(getAmount()).divide(HUNDRED));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public boolean hasValue() {
        return (this.returnQuant != null && returnQuant.nonZero()) ||
                (this.returnBox != null && returnBox.nonZero()) ||
                (this.deliverQuant != null && deliverQuant.nonZero()) ||
                (this.deliverBox != null && deliverBox.nonZero());
    }

    public boolean hasReturn() {
        return (this.returnQuant != null && returnQuant.nonZero()) ||
                (this.returnBox != null && returnBox.nonZero());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(returnQuant, returnBox, deliverQuant, deliverBox)
                .filterNotNull().toSuper();
    }

    @Override
    public ErrorResult getError() {
        BigDecimal quantity = getDeliverQuantity().subtract(getReturnQuantity());
        if (this.availQuant.compareTo(quantity) < 0) {
            return ErrorResult.make(DS.getString(R.string.sdeal_insufficient_production));
        }
        ErrorResult error = ErrorResult.NONE;
        if (returnQuant != null) {
            error = returnQuant.getError();
        }
        if (returnBox != null) {
            error.or(returnBox.getError());
        }
        if (error.isError()) {
            return error;
        }
        BigDecimal rq = getReturnQuantity();
        if (rq != null && order.originQuant.compareTo(rq) < 0) {
            return ErrorResult.make(DS.getString(R.string.sdeal_return_must_be_equal));
        }
        return ErrorResult.NONE;
    }
}
