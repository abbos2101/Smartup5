package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurAccount;

public class OrderPaymentFragment extends MoldContentRecyclerFragment<OrderCurAccount> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeader(R.layout.order_account_header);
        OrderInfoData data = Mold.getData(getActivity());
        setListItems(data.orderInfo.form.curAccount);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.order_account;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, OrderCurAccount item) {
        vsItem.textView(R.id.price_type).setText(item.currencyName + " \n" + item.paymentTypeName);
        vsItem.textView(R.id.price).setText(item.getAmount());
    }
}
