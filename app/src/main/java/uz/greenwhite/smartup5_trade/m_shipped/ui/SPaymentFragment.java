package uz.greenwhite.smartup5_trade.m_shipped.ui;// 30.06.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.dialog.NumberDateDialog;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPayment;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentForm;

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class SPaymentFragment extends MoldContentFragment {

    private VSDealPaymentForm form;
    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.sdeal_payment);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.form = ShippedUtil.getDealForm(this);
        this.form.cacheOrderTotalSum();

        MyArray<VSDealPaymentCurrency> items = form.payment.getItems();

        if (items.nonEmpty()) {
            SDealData data = Mold.getData(getActivity());
            VSDealPaymentCurrency vDealPaymentCurrency = items.get(0);

            paymentView(vDealPaymentCurrency);
            consignView(data, vDealPaymentCurrency);
            pkoView(data, vDealPaymentCurrency);
        }
    }

    private void paymentView(VSDealPaymentCurrency item) {
        final BigDecimal total = form.getOrderTotalSumCache(item.currency.currencyId);

        vsRoot.textView(R.id.tv_service_total_sum).setVisibility(item.service != null ? View.VISIBLE : View.GONE);
        if (item.service != null) {
            vsRoot.textView(R.id.tv_service_total_sum).setText(
                    UI.html().v(DS.getString(R.string.deal_service)).v(" ")
                            .v(NumberUtil.formatMoney(item.service.amount)).html());
        }

        VSDealPayment vsDealPayment = item.payments.getItems().get(0);

        CharSequence totalAmount = UI.html().i().v(DS.getString(R.string.total_sum)).i()
                .v(": ").v(NumberUtil.formatMoney(total)).v(" (").b().v(vsDealPayment.paymentType.name).b().v(")").html();
        vsRoot.textView(R.id.tv_total_amount).setText(totalAmount);
    }

    private void consignView(final SDealData data, final VSDealPaymentCurrency item) {
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_consign_type);
        vg.removeAllViews();

        MyArray<VSDealPayment> multyPayment = item.payments.getItems().filter(new MyPredicate<VSDealPayment>() {
            @Override
            public boolean apply(VSDealPayment p) {
                boolean result = p.hasValue() || p.consignmentAmount.nonZero();
                if (!result) {
                    p.consignmentAmount.setValue(null);
                }
                return result;
            }
        });

        if ((!form.hasConsignmentModule || !data.vDeal.sDealRef.setting.deal.consignment) &&
                !item.hasConsign() || multyPayment.isEmpty()) {
            vg.setVisibility(View.GONE);
            vsRoot.id(R.id.tv_consig_title).setVisibility(View.GONE);
            return;
        } else {
            vg.setVisibility(View.VISIBLE);
            vsRoot.id(R.id.tv_consig_title).setVisibility(View.VISIBLE);
        }

        for (final VSDealPayment p : multyPayment) {
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_consign_row);

            ModelChange onChange = new ModelChange() {
                @Override
                public void onChange() {
                    ErrorResult error = p.getConsignmentError();
                    vs.textView(R.id.tv_error).setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                    vs.textView(R.id.tv_error).setText(UI.toRedText(error.getErrorMessage()));

                    pkoView(data, item);
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
                        String result = p.getPaymentTotalAmount().toPlainString();
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

    public void pkoView(SDealData data, final VSDealPaymentCurrency item) {
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_pko_type);
        vg.removeAllViews();

        if (item.payments.getItems().size() == 1) {
            VSDealPayment vsDealPayment = item.payments.getItems().get(0);

            if (!TextUtils.isEmpty(vsDealPayment.paymentType.kind) &&
                    PaymentType.K_BANK.equals(vsDealPayment.paymentType.kind)) {
                vg.setVisibility(View.GONE);
                return;
            }
        }

        MyArray<VSDealPayment> multyPayment = item.payments.getItems().filter(new MyPredicate<VSDealPayment>() {
            @Override
            public boolean apply(VSDealPayment p) {
                boolean result = (p.hasValue() || p.pkoAmount.nonZero()) &&
                        p.getPaymentTotalAmount().subtract(p.consignmentAmount.getQuantity())
                                .compareTo(BigDecimal.ZERO) > 0;
                if (!result) {
                    p.pkoAmount.setValue(null);
                }
                return result;
            }
        });

        if (!form.hasPKOModule && !item.hasPKO() || multyPayment.isEmpty()) {
            vg.setVisibility(View.GONE);
            vsRoot.id(R.id.tv_pko_titile).setVisibility(View.GONE);
            return;
        } else {
            vg.setVisibility(View.VISIBLE);
            vsRoot.id(R.id.tv_pko_titile).setVisibility(View.VISIBLE);
        }

        for (final VSDealPayment val : multyPayment) {
            if (!TextUtils.isEmpty(val.paymentType.kind) &&
                    PaymentType.K_BANK.equals(val.paymentType.kind)) {
                continue;
            }
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.debtor_row_item);

            vs.textView(R.id.tv_title).setText(val.tvTitle());
            vs.bind(R.id.et_amount, val.pkoAmount);
            vs.id(R.id.et_amount).setEnabled(data.hasEdit());
            vg.addView(vs.view);

            if (data.hasEdit()) {
                vs.id(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vs.editText(R.id.et_amount).setText("");
                    }
                });

                vs.id(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigDecimal subtractAmount = val.getPaymentTotalAmount().subtract(val.consignmentAmount.getQuantity());
                        vs.editText(R.id.et_amount).setText(subtractAmount.toPlainString());
                    }
                });
            }

            vs.model(R.id.et_amount).add(new ModelChange() {
                @Override
                public void onChange() {
                    ErrorResult error = item.getErrorPKO();
                    TextView tvError = vs.textView(R.id.tv_error);
                    if (error.isError()) {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(error.getErrorMessage());
                    } else {
                        tvError.setVisibility(View.GONE);
                    }
                }
            }).notifyListeners();
        }


    }

}
