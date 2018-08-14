package uz.greenwhite.smartup5_trade.m_shipped.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.variable.overload.VSOverloadForm;

public class SOverloadFragment extends MoldContentRecyclerFragment<SOverload> {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHeader(R.layout.sdeal_overload_header);

        final VSOverloadForm form = ShippedUtil.getDealForm(this);
        setListItems(form.overloads);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.sdeal_product;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, SOverload item) {
        vsItem.textView(R.id.tv_product_name).setText(item.productName);
        vsItem.textView(R.id.tv_quantity).setText(NumberUtil.formatMoney(item.soldQuant));
        vsItem.textView(R.id.tv_price).setText(NumberUtil.formatMoney(item.soldPrice));
        vsItem.textView(R.id.tv_total_price).setText(NumberUtil.formatMoney(item.soldTotalAmount));
    }
}
