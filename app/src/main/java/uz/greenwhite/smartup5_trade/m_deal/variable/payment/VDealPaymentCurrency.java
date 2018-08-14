package uz.greenwhite.smartup5_trade.m_deal.variable.payment;// 15.07.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;

public class VDealPaymentCurrency extends VariableLike {

    private final DealRef dealRef;
    public final Currency currency;
    private final ValueArray<VDealPayment> payments;
    private ValueSpinner singlePayment;
    private BigDecimal orderSum;
    private MyArray<String> paymentIds;

    public VDealPaymentCurrency(DealRef dealRef, Currency currency, ValueArray<VDealPayment> payments) {
        AppError.checkNull(currency, payments);
        this.dealRef = dealRef;
        this.currency = currency;
        this.payments = payments;
        this.paymentIds = MyArray.emptyArray();
        this.singlePayment = getSinglePayment(BigDecimal.ZERO, payments.getItems());
    }

    public void setPaymentIds(MyArray<String> paymentIds) {
        this.paymentIds = MyArray.nvl(paymentIds);
    }

    public MyArray<VDealPayment> getMultyPayment() {
        return payments.getItems().filter(new MyPredicate<VDealPayment>() {
            @Override
            public boolean apply(VDealPayment val) {
                if (paymentIds.nonEmpty() && !paymentIds.contains(val.paymentType.id, MyMapper.<String>identity())) {
                    val.amount.setText("");
                    val.consignmentAmount.setText("");
                    val.consignmentDate.setText("");
                    return false;
                }
                return true;
            }
        });
    }

    public ValueSpinner getSinglePayment(BigDecimal total) {
        MyArray<VDealPayment> multyPayment = getMultyPayment();
        singlePayment = getSinglePayment(total, multyPayment);

        return singlePayment;
    }

    public void setOrderCurrencySum(BigDecimal orderSum) {
        AppError.checkNull(orderSum);
        this.orderSum = orderSum;
    }

    public BigDecimal getOrderCurrencySum() {
        return orderSum;
    }

    public BigDecimal getQuantity() {
        return this.payments.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealPayment>() {
            @Override
            public BigDecimal apply(BigDecimal total, VDealPayment vDealPayment) {
                return total.add(vDealPayment.amount.getQuantity());
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(this.payments);
    }

    public boolean hasValue() {
        for (VDealPayment p : payments.getItems()) {
            if (p.hasValue()) return true;
        }
        return false;
    }

    public boolean hasConsign() {
        for (VDealPayment p : payments.getItems()) {
            if (p.hasConsign()) return true;
        }
        return false;
    }

    private ValueSpinner getSinglePayment(BigDecimal total, MyArray<VDealPayment> vDealPayments) {
        MyArray<Ban> bans = dealRef.violationBans.filter(new MyPredicate<Ban>() {
            @Override
            public boolean apply(Ban ban) {
                return Ban.K_PAYMENT_TYPE.equals(ban.kind);
            }
        });

        final Ban ban = bans.isEmpty() ? null : bans.get(0);


        MyArray<SpinnerOption> options = vDealPayments.map(new MyMapper<VDealPayment, SpinnerOption>() {
            @Override
            public SpinnerOption apply(VDealPayment val) {
                if (ban != null && ban.kindSourceIds.contains(val.paymentType.id, MyMapper.<String>identity())) {
                    val.amount.setText("");
                    val.consignmentAmount.setText("");
                    val.consignmentDate.setText("");
                    return null;
                }
                if (paymentIds.nonEmpty() && !paymentIds.contains(val.paymentType.id, MyMapper.<String>identity())) {
                    val.amount.setText("");
                    val.consignmentAmount.setText("");
                    val.consignmentDate.setText("");
                    return null;
                }
                return new SpinnerOption(val.paymentType.id, val.paymentType.name, val);
            }
        }).filterNotNull();

        options = options.prepend(new SpinnerOption("#null", DS.getString(R.string.not_selected), null));

        VDealPayment first = vDealPayments.findFirst(new MyPredicate<VDealPayment>() {
            @Override
            public boolean apply(VDealPayment vDealPayment) {
                return vDealPayment.amount.nonZero();
            }
        });
        SpinnerOption option = options.get(0);
        if (first != null) {
            option = options.find(first.paymentType.id, SpinnerOption.KEY_ADAPTER);
        }
        return new ValueSpinner(options, option);
    }
}
