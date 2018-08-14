package uz.greenwhite.smartup5_trade.m_outlet.ui;// 11.11.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.smartup5_trade.common.predicate.OptionDateView;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.OShippedFilter;

public class ShippedTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadTuning();
    }

    public void reloadTuning() {
        ShippedFragment content = Mold.getContentFragment(getActivity());
        if (content.filter != null) {
            setSections(MyArray.from(getFilterDate(content.filter)));
        }
    }

    public Section getFilterDate(final OShippedFilter filter) {
        return new LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();
                cnt.addView(new OptionDateView(getActivity(), filter.deliveryDate).getView());
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        ShippedFragment content = Mold.getContentFragment(getActivity());
        content.setFilterValues();
    }

    @Override
    public void onDrawerOpened() {
        reloadTuning();
    }
}
