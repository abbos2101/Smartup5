package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Comparator;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurStock;

public class OrderStockFragment extends MoldContentRecyclerFragment<OrderCurStock> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeader(R.layout.order_stock_header);
        OrderInfoData data = Mold.getData(getActivity());
        setListItems(data.orderInfo.form.curStocks.sort(new Comparator<OrderCurStock>() {
            @Override
            public int compare(OrderCurStock l, OrderCurStock r) {
                return CharSequenceUtil.compareToIgnoreCase(l.productName, r.productName);
            }
        }));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.order_stock;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, OrderCurStock item) {
        vsItem.textView(R.id.tv_product_name).setText(item.productName);
        vsItem.textView(R.id.tv_market).setText(item.moneyFormat(item.market));
        vsItem.textView(R.id.tv_stock).setText(item.moneyFormat(item.stock));
    }
}
