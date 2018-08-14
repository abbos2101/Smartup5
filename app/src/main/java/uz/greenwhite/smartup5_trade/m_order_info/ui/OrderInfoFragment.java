package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderHeader;

public class OrderInfoFragment extends MoldContentRecyclerFragment<Tuple2> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OrderInfoData infoData = Mold.getData(getActivity());
        OrderHeader oi = infoData.orderInfo.header;
        setListItems(MyArray.from(
                new Tuple2(DS.getString(R.string.order_outlet_name), oi.name),
                new Tuple2(DS.getString(R.string.order_outlet_category), oi.category),
                new Tuple2(DS.getString(R.string.order_outlet_address), oi.address),
                new Tuple2(DS.getString(R.string.order_deal_amount), oi.amount),
                new Tuple2(DS.getString(R.string.order_user), oi.userName),
                new Tuple2(DS.getString(R.string.order_visit_period), oi.visitPeriod)
        ));

    }

    @Override
    protected int adapterGetLayoutResource() {
        return android.R.layout.simple_list_item_2;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, Tuple2 item) {
        vsItem.textView(android.R.id.text1).setText((String) item.first);
        vsItem.textView(android.R.id.text2).setText((String) item.second);
    }
}
