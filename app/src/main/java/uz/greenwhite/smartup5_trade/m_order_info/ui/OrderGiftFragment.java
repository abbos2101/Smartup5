package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Comparator;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurGift;

public class OrderGiftFragment extends MoldContentRecyclerFragment<OrderCurGift> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeader(R.layout.order_gift_header);
        OrderInfoData data = Mold.getData(getActivity());
        setListItems(data.orderInfo.form.curGift.sort(new Comparator<OrderCurGift>() {
            @Override
            public int compare(OrderCurGift l, OrderCurGift r) {
                int compare = CharSequenceUtil.compareToIgnoreCase(l.warehouseName, r.warehouseName);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.productName, r.productName);
                }
                return compare;
            }
        }));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.order_gift;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, OrderCurGift item) {
        vsItem.textView(R.id.product_name).setText(item.productName);
        vsItem.textView(R.id.warehouse_name).setText(item.warehouseName);
        vsItem.textView(R.id.product_count).setText(item.solidQuant + "/" + item.deliverQuant);
    }
}
