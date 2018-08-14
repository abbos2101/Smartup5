package uz.greenwhite.smartup5_trade.m_duty;

import android.view.View;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.smartup5_trade.m_duty.filter.PriceFilter;

public class PriceTuningFragment extends MoldTuningSectionFragment {

    private void prepareView() {
        MyArray<Section> sections = MyArray.from(getFilterSection()).filterNotNull();
        if (sections.nonEmpty()) {
            setSections(sections);
        }
    }

    private PriceFilter getFilter() {
        PriceFragment contentFragment = Mold.getContentFragment(getActivity());
        return contentFragment.filter;
    }

    private Section getFilterSection() {
        final PriceFilter filter = getFilter();
        if (filter == null) {
            return null;
        }
        return new LinearLayoutSection() {

            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.<FilterValue>from(
                        filter.productFilter.groupFilter
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters.filterNotNull());
                FilterUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    @Override
    public void onDrawerOpened() {
        prepareView();
    }

    @Override
    public void onDrawerClosed() {
        PriceFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter();
    }
}