package uz.greenwhite.smartup5_trade.m_shipped.variable.payment;// 09.09.2016

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SService;

public class VSDealPayment extends VariableLike {

    public final SPayment sPayment;
    public final PaymentType paymentType;

    @Nullable
    private final SService sService;

    public final ValueBigDecimal paymentAmount = new ValueBigDecimal(20, 6);
    public final ValueBigDecimal consignmentAmount = new ValueBigDecimal(20, 6);
    public final ValueString consignmentDate = new ValueString(10);
    public final ValueBigDecimal pkoAmount = new ValueBigDecimal(20, 6);

    public VSDealPayment(SPayment sPayment,
                         PaymentType paymentType,
                         @Nullable
                                 SService sService,

                         BigDecimal amount,
                         BigDecimal consignAmount,
                         String consignDate,
                         BigDecimal pkoAmount) {
        this.sPayment = sPayment;
        this.paymentType = paymentType;
        this.sService = sService;
        this.paymentAmount.setValue(amount);
        this.consignmentAmount.setValue(consignAmount);
        this.consignmentDate.setValue(consignDate);

        if (pkoAmount != null && pkoAmount.compareTo(BigDecimal.ZERO) != 0) {
            this.pkoAmount.setValue(pkoAmount);
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(paymentAmount,
                consignmentAmount, consignmentDate, pkoAmount);
    }

    public void setPaymentAmount(BigDecimal amount) {
        paymentAmount.setValue(amount);
    }

    public BigDecimal getPaymentTotalAmount() {
        if (sService != null && sService.amount != null) {
            return paymentAmount.getQuantity().add(sService.amount);
        }
        return paymentAmount.getQuantity();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) return error;

        error = getErrorPKO();
        if (error.isError()) return error;

        error = getConsignmentError();
        if (error.isError()) return error;

        return ErrorResult.NONE;
    }

    public ErrorResult getConsignmentError() {
        ErrorResult error = ErrorResult.NONE;

        if (consignmentAmount.isZero() && consignmentDate.nonEmpty()) {
            error = error.or(ErrorResult.make(DS.getString(R.string.deal_sum_consign)));
        }
        if (consignmentAmount.nonZero() && consignmentDate.isEmpty()) {
            error = error.or(ErrorResult.make(DS.getString(R.string.deal_date_consign)));
        }

        BigDecimal totalAmount = getPaymentTotalAmount();

        if ((totalAmount.compareTo(BigDecimal.ZERO) == 0 && consignmentAmount.nonZero()) ||
                (totalAmount.compareTo(BigDecimal.ZERO) != 0 && consignmentAmount.nonZero() &&
                        totalAmount.compareTo(consignmentAmount.getQuantity()) < 0)) {
            error = error.or(ErrorResult.make(DS.getString(R.string.deal_order_total_sum_not_equal)));
        }
        if (error.isError()) return error;

        return ErrorResult.NONE;
    }

    public ErrorResult getErrorPKO() {
        BigDecimal totalAmount = getPaymentTotalAmount();

        BigDecimal consignmentAmountQuantity = consignmentAmount.getQuantity();
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0 && pkoAmount.nonZero() ||
                totalAmount.subtract(consignmentAmountQuantity).compareTo(pkoAmount.getQuantity()) < 0) {
            return ErrorResult.make(DS.getString(R.string.deal_pko_total_sum_not_equal));
        }
        return ErrorResult.NONE;
    }

    public boolean hasValue() {
        return getPaymentTotalAmount().compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasConsign() {
        return consignmentAmount.nonZero() || consignmentDate.nonEmpty();
    }

    public boolean hasPKO() {
        return pkoAmount.nonZero();
    }

    public static final MyMapper<VSDealPayment, String> KEY_ADAPTER = new MyMapper<VSDealPayment, String>() {
        @Override
        public String apply(VSDealPayment val) {
            return val.paymentType.id;
        }
    };

    public CharSequence tvTitle() {
        BigDecimal amountQuantity = paymentAmount.getQuantity();
        BigDecimal consignmentAmountQuantity = consignmentAmount.getQuantity();
        return UI.html().v(paymentType.name).br()
                .v(DS.getString(R.string.sum)).v(": ")
                .v(NumberUtil.formatMoney(amountQuantity.subtract(consignmentAmountQuantity))).html();
    }

}
