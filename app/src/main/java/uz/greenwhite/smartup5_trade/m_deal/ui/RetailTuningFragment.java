package uz.greenwhite.smartup5_trade.m_deal.ui;


import android.content.Context;
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
import uz.greenwhite.smartup5_trade.m_deal.filter.RetailAuditFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;

public class RetailTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareView();
    }

    private void prepareView() {
        MyArray<MoldTuningSectionFragment.Section> sections = MyArray.from(getFilterSection());
        if (sections.nonEmpty()) {
            setSections(sections.filterNotNull());
        }

        RetailAuditFilter filter = getFilter();
        MyPredicate<VDealRetailAudit> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }
        RetailAuditFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter(predicate);
    }

    private RetailAuditFilter getFilter() {
        FragmentActivity activity = getActivity();
        String formCode = DealUtil.getFormCode(Mold.getContentFragment(activity));
        DealData dealData = Mold.getData(activity);
        return dealData.filter.findRetailAudit(formCode);
    }

    private MoldTuningSectionFragment.Section getFilterSection() {
        final RetailAuditFilter filter = getFilter();
        if (filter == null) {
            return null;
        }
        return new MoldTuningSectionFragment.LinearLayoutSection() {
            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.from(
                        filter.product.groupFilter,
                        filter.hasValue
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters);
                FilterUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        prepareView();
    }
}