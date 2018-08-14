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
import uz.greenwhite.smartup5_trade.common.predicate.OutletFilter;

public class OutletTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadContent(getFilter());
    }

    private void reloadContent(OutletFilter filter) {
        if (filter == null) {
            return;
        }
        Section filterSection = getFilterSection(filter);
        setSections(MyArray.from(filterSection).filterNotNull());
    }

    private Section getFilterSection(final OutletFilter filter) {
        return new LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();
                MyArray<FilterValue> filters = MyArray.from(
                        filter.region,
                        filter.speciality,
                        filter.legalPerson,
                        filter.groupFilter,
                        filter.hasDeal,
                        filter.lastVisitDate
                ).filterNotNull();

                MyArray<View> views = FilterUtil.addAll(cnt, filters);
                FilterUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    private OutletFilter getFilter() {
        OutletFragment content = Mold.getContentFragment(getActivity());
        return content.filter;
    }

    @Override
    public void onDrawerClosed() {
        OutletFragment content = Mold.getContentFragment(getActivity());
        content.setFilterValues();
    }

    @Override
    public void onDrawerOpened() {
        reloadContent(getFilter());
    }
}
