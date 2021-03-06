package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBooleanList;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.filter.StockFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.SettingDeal;

public class StockTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyArray<Section> sections = MyArray.from(getFilterSection());
        setSections(sections.filterNotNull());
    }

    private StockFilter getFilter() {
        FragmentActivity activity = getActivity();
        String formCode = DealUtil.getFormCode(Mold.getContentFragment(activity));
        DealData dealData = Mold.getData(activity);
        return dealData.filter.findStock(formCode);
    }

    private Section getFilterSection() {
        final StockFilter filter = getFilter();
        if (filter == null) {
            return null;
        }
        DealData dealData = Mold.getData(getActivity());
        final SettingDeal deal = dealData.vDeal.dealRef.setting.deal;
        return new LinearLayoutSection() {

            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.from(
                        filter.product.groupFilter,
                        new FilterBooleanList(MyArray.from(
                                filter.product.hasBarcode,
                                filter.product.hasPhoto,
                                filter.product.hasFile,
                                deal.mml ? null : filter.product.mml,
                                filter.hasValue
                        ).filterNotNull())
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters.filterNotNull());
                DealUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        StockFilter filter = getFilter();
        MyPredicate<VDealStockProduct> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }
        StockFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter(predicate);
    }
}