package uz.greenwhite.smartup5_trade.m_session.ui.person;// 05.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.smartup5_trade.common.predicate.OptionDateView;
import uz.greenwhite.smartup5_trade.m_session.filter.ShippedFilter;

public class ShippedTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadContent(getFilter());
    }

    public void reloadContent(ShippedFilter filter) {
        if (filter == null) {
            return;
        }
        Section filterDate = getFilterDate(filter);
        Section filterSection = getFilterSection(filter);
        setSections(MyArray.from(filterDate, filterSection).filterNotNull());
    }

    public Section getFilterDate(final ShippedFilter filter) {
        if (filter.deliveryDate == null) {
            return null;
        }
        return new LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();
                cnt.addView(new OptionDateView(getActivity(), filter.deliveryDate).getView());
            }
        };
    }

    public Section getFilterSection(final ShippedFilter filter) {
        if (filter.groupFilter == null) {
            return null;
        }
        return new LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();
                MyArray<FilterValue> filters = MyArray.<FilterValue>from(
                        filter.groupFilter,
                        filter.rooms,
                        filter.regions
                ).filterNotNull();

                MyArray<View> views = FilterUtil.addAll(cnt, filters);
                FilterUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    private ShippedFilter getFilter() {
        ShippedFragment content = Mold.getContentFragment(getActivity());
        return content.filter;
    }

    @Override
    public void onDrawerClosed() {
        ShippedFragment content = Mold.getContentFragment(getActivity());
        content.setFilterValues();
    }

    @Override
    public void onDrawerOpened() {
        reloadContent(getFilter());
    }
}
