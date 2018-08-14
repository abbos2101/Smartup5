package uz.greenwhite.smartup5_trade.m_shipped.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.filter.SOrderFilter;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrder;

public class SOrderTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SOrderFilter filter = getFilter();
        if (filter != null) {
            setSections(MyArray.from(
                    getFilterSection(filter),
                    getFilterReturn(filter)
            ));
        }
    }

    private SOrderFilter getFilter() {
        FragmentActivity activity = getActivity();
        String formCode = ShippedUtil.getFormCode(Mold.getContentFragment(activity));
        SDealData dealData = Mold.getData(activity);
        return dealData.filter.findOrder(formCode);
    }

    private Section getFilterSection(final SOrderFilter filter) {
        return new LinearLayoutSection() {

            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.<FilterValue>from(
                        filter.hasValue
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters.filterNotNull());
                //FilterUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    private Section getFilterReturn(final SOrderFilter filter) {
        return new Section() {
            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent) {
                ViewSetup vs = new ViewSetup(inflater, parent, R.layout.sdeal_filter_return);
                vs.id(R.id.btn_return_all).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SOrderFragment c = Mold.getContentFragment(getActivity());
                                c.returnAllOrder();
                                Mold.closeDrawers(getActivity());
                            }
                        }
                );
                vs.id(R.id.btn_cancel_return).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SOrderFragment c = Mold.getContentFragment(getActivity());
                                c.cancelReturnOrder();
                                Mold.closeDrawers(getActivity());
                            }
                        }
                );
                return vs.view;
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        SOrderFilter filter = getFilter();
        MyPredicate<VSDealOrder> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }
        SOrderFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter(predicate);
    }
}
