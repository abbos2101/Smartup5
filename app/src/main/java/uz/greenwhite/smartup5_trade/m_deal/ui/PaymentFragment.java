package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.Date;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.dialog.NumberDateDialog;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPayment;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentForm;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class PaymentFragment extends DealFormContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_payment);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.payment);
        reloadContent();
    }

    @Override
    public void reloadContent() {
        DealData data = Mold.getData(getActivity());

        VDealPaymentForm form = DealUtil.getDealForm(this);
        form.setCurrencyOrderSums(data.vDeal.getCurrencyOrderSums());

        MyArray<VDealPaymentCurrency> payments = form.payment.getItems()
                .filter(DealUtil.getPaymentCurrencyPredicate(this));

        vsRoot.id(R.id.ll_payment_tab).setVisibility(payments.isEmpty() ? View.GONE : View.VISIBLE);
        vsRoot.id(R.id.tv_first_make_order).setVisibility(payments.isEmpty() ? View.VISIBLE : View.GONE);

        if (payments.nonEmpty()) {
            VDealPaymentCurrency vDealPaymentCurrency = payments.get(0);
            paymentView(data, form, vDealPaymentCurrency);
        }
    }

    private void paymentView(final DealData data, final VDealPaymentForm form, final VDealPaymentCurrency item) {
        final BigDecimal total = item.getOrderCurrencySum();

        vsRoot.textView(R.id.tv_total_amount)
                .setText(UI.html().v(DS.getString(R.string.total_sum))
                        .v(": ").v(NumberUtil.formatMoney(total)).html());

        final ValueSpinner singlePayment = item.getSinglePayment(total);

        vsRoot.bind(R.id.sp_payment, singlePayment);

        vsRoot.id(R.id.sp_payment).setEnabled(data.hasEdit());

        final Setter<SpinnerOption> oldSelectedValue = new Setter<>();
        oldSelectedValue.value = singlePayment.getValue();

        vsRoot.model(R.id.sp_payment).add(new ModelChange() {
            @Override
            public void onChange() {
                final SpinnerOption value = singlePayment.getValue();
                final SpinnerOption oldValue = oldSelectedValue.value;

                if (value.code.equals(oldValue.code)) {
                    consignView(data, form, item);
                    return;
                }

                VDealPayment newPayment = (VDealPayment) value.tag;
                VDealPayment oldPayment = (VDealPayment) oldValue.tag;

                if (newPayment != null && oldPayment != null) {
                    newPayment.consignmentAmount.setValue(null);
                    newPayment.consignmentDate.setValue(null);

                    if (oldPayment.hasConsign()) {
                        newPayment.consignmentAmount.setValue(oldPayment.consignmentAmount.getValue());
                        newPayment.consignmentDate.setValue(oldPayment.consignmentDate.getValue());
                    }
                } else if (newPayment != null) {
                    newPayment.consignmentAmount.setValue(null);
                    newPayment.consignmentDate.setValue(null);
                }

                if (oldPayment != null) {
                    oldPayment.consignmentAmount.setValue(null);
                    oldPayment.consignmentDate.setValue(null);
                }

                for (VDealPayment p : item.getMultyPayment()) {

                    p.amount.setValue(BigDecimal.ZERO);

                    if (p.paymentType.id.equals(value.code) &&
                            !"#null".equals(value.code)) {
                        p.amount.setValue(total);
                    }
                }
                oldSelectedValue.value = value;
                consignView(data, form, item);
            }
        }).notifyListeners();
    }

    private void consignView(DealData data, VDealPaymentForm form, final VDealPaymentCurrency item) {
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_consign_type);
        vg.removeAllViews();

        if (data.vDeal.dealRef.violationBans.contains(new MyPredicate<Ban>() {
            @Override
            public boolean apply(Ban ban) {
                return Ban.K_CONSIGNMENT.equals(ban.kind);
            }
        })) {
            return;
        }

        MyArray<VDealPayment> multyPayment = item.getMultyPayment().filter(new MyPredicate<VDealPayment>() {
            @Override
            public boolean apply(VDealPayment p) {
                return p.hasValue() || p.consignmentAmount.nonZero();
            }
        });

        if ((!form.hasConsignmentModule || !data.vDeal.dealRef.setting.deal.consignment) &&
                !item.hasConsign() || multyPayment.isEmpty()) {
            vsRoot.id(R.id.v_payment_line).setVisibility(View.GONE);
            return;
        } else {
            vsRoot.id(R.id.v_payment_line).setVisibility(View.VISIBLE);
        }

        for (final VDealPayment p : multyPayment) {
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_consign_row);

            ModelChange onChange = new ModelChange() {
                @Override
                public void onChange() {
                    ErrorResult error = ErrorResult.NONE;
                    if (p.consignmentAmount.isZero() && p.consignmentDate.nonEmpty()) {
                        error = error.or(ErrorResult.make(DS.getString(R.string.deal_sum_consign)));
                    }
                    if (p.consignmentAmount.nonZero() && p.consignmentDate.isEmpty()) {
                        error = error.or(ErrorResult.make(DS.getString(R.string.deal_date_consign)));
                    }

                    if ((p.amount.isZero() && p.consignmentAmount.nonZero()) ||
                            (p.amount.nonZero() && p.consignmentAmount.nonZero() &&
                                    p.amount.getQuantity().compareTo(p.consignmentAmount.getQuantity()) < 0)) {
                        error = error.or(ErrorResult.make(DS.getString(R.string.deal_order_total_sum_not_equal)));
                    }

                    vs.textView(R.id.tv_error).setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                    vs.textView(R.id.tv_error).setText(UI.toRedText(error.getErrorMessage()));
                }
            };

            onChange.onChange();


            vs.bind(R.id.et_consign_amount, p.consignmentAmount);
            vs.bind(R.id.et_consign_date, p.consignmentDate);

            vs.model(R.id.et_consign_amount).add(onChange);
            vs.model(R.id.et_consign_date).add(onChange);

            numberPikerBind(vs.editText(R.id.et_consign_date));

            vs.id(R.id.et_consign_amount).setEnabled(data.hasEdit());
            vs.id(R.id.et_consign_date).setEnabled(data.hasEdit());

            int editVisibility = data.hasEdit() ? View.VISIBLE : View.GONE;

            vs.id(R.id.btn_consign_all).setVisibility(editVisibility);
            vs.id(R.id.btn_consign_clear).setVisibility(editVisibility);
            vs.id(R.id.btn_consign_date_clear).setVisibility(editVisibility);

            if (data.hasEdit()) {
                vs.id(R.id.btn_consign_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result = p.amount.getQuantity().toPlainString();
                        result = "0".equals(result) ? "" : result;
                        vs.editText(R.id.et_consign_amount).setText(result);
                    }
                });
                vs.id(R.id.btn_consign_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vs.editText(R.id.et_consign_amount).setText("");
                    }
                });
                vs.id(R.id.btn_consign_date_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vs.editText(R.id.et_consign_date).setText("");
                    }
                });
            }

            vg.addView(vs.view);
        }
    }

    public void numberPikerBind(final EditText consignDate) {
        consignDate.setHint(uz.greenwhite.lib.R.string.date_format);
        consignDate.setOnLongClickListener(null);
        consignDate.setKeyListener(null);
        consignDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, uz.greenwhite.lib.R.drawable.gwslib_datepicker, 0);
        consignDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    NumberDateDialog.show(getActivity(), new NumberDateDialog.OnNumberSelect() {
                        @Override
                        public int getValue() {
                            String date = consignDate.getText().toString();
                            if (!TextUtils.isEmpty(date)) {
                                long convert = DateUtil.parse(date).getTime();
                                long millis = convert - System.currentTimeMillis();
                                long result = millis / 86400000L;
                                return (int) result + 1;
                            }
                            return 1;
                        }

                        @Override
                        public void onSelectNumber(int number) {
                            long dayOfMilis = number * 86400000L;
                            Date date = new Date();
                            date.setTime(System.currentTimeMillis() + dayOfMilis);
                            consignDate.setText(DateUtil.format(date, DateUtil.FORMAT_AS_DATE));
                        }
                    });
                }
                return false;
            }
        });
    }
}
