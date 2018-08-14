package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPayment;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPaymentForm;

public class ReturnPaymentFragment extends DealFormRecyclerFragment<VDealRPayment> {

    public DealData data;
    public VDealRPaymentForm form;
    public ModelChange onChange;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.payment);
        this.data = Mold.getData(getActivity());
        this.form = DealUtil.getDealForm(this);
        this.form.cacheOrderTotalSum();

        ErrorResult error = form.paymentModule.orderModule.getError();
        if (error.isError()) {
            UI.alertError(getActivity(), error.getErrorMessage());
            setListItems(MyArray.<VDealRPayment>emptyArray());
            return;
        }

        final ViewSetup vsHeader = setHeader(R.layout.deal_payment_header);
        BigDecimal returnTotalSum = form.getReturnTotalSum();

        CharSequence total = UI.html().v(getString(R.string.total_sum)).v(" ")
                .v(NumberUtil.formatMoney(returnTotalSum)).html();

        vsHeader.textView(R.id.tv_total).setText(total);

        this.onChange = new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = form.getError();
                ShortHtml val = UI.html();
                if (error.isError()) {
                    val.fRed();
                }

                BigDecimal remainTotal = form.getRemainTotalSum();
                val.v(DS.getString(R.string.remain_total_sum)).v(": ").v(NumberUtil.formatMoney(remainTotal));

                if (error.isError()) {
                    val.fRed();
                }
                vsHeader.textView(R.id.tv_remain).setText(val.html());
            }
        };

        setListItems(form.payments.getItems());
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_payment_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealRPayment item) {
        vsItem.textView(R.id.tv_payment_name).setText(item.paymentType.name);

        vsItem.bind(R.id.et_amount, item.amount);
        vsItem.model(R.id.et_amount).add(onChange).notifyListeners();

        int editVisibility = data.hasEdit() ? View.VISIBLE : View.GONE;
        vsItem.id(R.id.btn_all).setVisibility(editVisibility);
        vsItem.id(R.id.btn_clear).setVisibility(editVisibility);


        if (data.hasEdit()) {
            vsItem.id(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vsItem.editText(R.id.et_amount).setText("");
                }
            });

            vsItem.id(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BigDecimal remainAmount = form.getRemainTotalSum();
                    remainAmount = remainAmount.add(item.amount.getQuantity());
                    String result = remainAmount.toPlainString();
                    result = "0".equals(result) ? "" : result;
                    vsItem.editText(R.id.et_amount).setText(result);
                }
            });
        }
    }
}
