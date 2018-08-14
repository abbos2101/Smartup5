package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurOrder;

public class OrderOrderFragment extends MoldContentRecyclerFragment<OrderCurOrder> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeader(R.layout.order_order_header);
        OrderInfoData data = Mold.getData(getActivity());
        setListItems(data.orderInfo.form.curOrder);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.order_order;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, OrderCurOrder item) {
        vsItem.textView(R.id.product_name).setText(item.productName);
        vsItem.textView(R.id.warehouse_name).setText(item.warehouseName + "\n" + item.currencyName + "\n" + item.priceName);
        vsItem.textView(R.id.product_count).setText(item.moneyFormat(item.orderQuant));
        vsItem.textView(R.id.product_price).setText(item.moneyFormat(item.orderPrice));
    }
}
