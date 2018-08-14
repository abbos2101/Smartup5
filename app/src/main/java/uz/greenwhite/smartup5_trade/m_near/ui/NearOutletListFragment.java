package uz.greenwhite.smartup5_trade.m_near.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;
import uz.greenwhite.smartup5_trade.m_near.bean.MapItem;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;

public class NearOutletListFragment extends MoldContentRecyclerFragment<MapItem> {

    public static NearOutletListFragment newInstance(ArgNearOutlet arg) {
        return Mold.parcelableArgumentNewInstance(NearOutletListFragment.class,
                arg, ArgNearOutlet.UZUM_ADAPTER);
    }

    public ArgNearOutlet getArgNearOutlet() {
        return Mold.parcelableArgument(this, ArgNearOutlet.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListItems(NearMapUtil.nearOutletList(getArgNearOutlet()));
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, MapItem item) {
        ArgOutlet argOutlet = new ArgOutlet(getArgNearOutlet(), item.outlet.id);
        OutletIndexFragment.open(argOutlet);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return android.R.layout.simple_list_item_2;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, MapItem item) {
        vsItem.textView(android.R.id.text1).setText(item.title);
        vsItem.textView(android.R.id.text2).setText(item.detail);
    }
}
