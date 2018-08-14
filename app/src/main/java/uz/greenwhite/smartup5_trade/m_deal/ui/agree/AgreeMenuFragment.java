package uz.greenwhite.smartup5_trade.m_deal.ui.agree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.filter.AgreeFilter;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealData;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgree;

public class AgreeMenuFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MoldTuningSectionFragment.Section filterSection = getFilterSection();
        if (filterSection != null) {
            setSections(MyArray.from(filterSection));
        }
    }

    private AgreeFilter getFilter() {
        FragmentActivity activity = getActivity();
        String formCode = DealUtil.getFormCode(Mold.getContentFragment(activity));
        DealData dealData = Mold.getData(activity);
        return dealData.filter.findAgree(formCode);
    }

    private MoldTuningSectionFragment.Section getFilterSection() {
        final AgreeFilter filter = getFilter();
        if (filter == null) {
            return null;
        }
        return new MoldTuningSectionFragment.LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.from(
                        filter.product.groupFilter,
                        filter.product.hasBarcode,
                        filter.product.hasPhoto,
                        filter.hasValue
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters);
                FilterUtil.addClearButton(getActivity(),cnt, views);
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        AgreeFilter filter = getFilter();
        MyPredicate<VDealAgree> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }
        AgreeFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter(predicate);
    }

}
