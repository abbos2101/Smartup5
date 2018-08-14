package uz.greenwhite.smartup5_trade.m_deal.ui;// 03.10.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.OnItemClickListener;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.m_deal.ui.adapter.DealModuleAdapter;

public abstract class DealModuleFragment<F extends VForm>
        extends MoldContentFragment
        implements OnItemClickListener<VForm> {

    private static final String K_SCROLL_X = "DealModuleFragment:scrollX";
    private static final String K_SCROLL_Y = "DealModuleFragment:scrollY";
    private static final String K_OPENING_FORMS = "DealModuleFragment:opening_forms";

    protected ViewSetup vsRoot;

    private DealModuleAdapter adapter;
    private int scrollX = 0, scrollY = 0;
    private MyArray<String> openingForms = MyArray.emptyArray();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.z_deal_module_index);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.scrollX = savedInstanceState.getInt(K_SCROLL_X, 0);
            this.scrollY = savedInstanceState.getInt(K_SCROLL_Y, 0);
            this.openingForms = Uzum.toValue(savedInstanceState.getString(K_OPENING_FORMS, "[]"), UzumAdapter.STRING_ARRAY);
        }

        this.adapter = new DealModuleAdapter(this, this);
        this.adapter.openingModule.addAll(openingForms.asList());

        NestedScrollView scrollView = vsRoot.id(R.id.nsv_scroll);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                DealModuleFragment.this.scrollX = scrollX;
                DealModuleFragment.this.scrollY = scrollY;
            }
        });
    }

    protected ViewSetup setFooter(int layout) {
        ViewSetup vsFooter = new ViewSetup(getActivity(), layout);
        BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return vsFooter;
    }

    @Override
    public void onStop() {
        super.onStop();
        this.openingForms = MyArray.from(adapter.openingModule);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(K_SCROLL_X, this.scrollX);
        outState.putInt(K_SCROLL_Y, this.scrollY);
        outState.putString(K_OPENING_FORMS, Uzum.toJson(MyArray.from(adapter.openingModule), UzumAdapter.STRING_ARRAY));
    }

    @SuppressWarnings("unchecked")
    protected <M extends VModule> void setListItems(MyArray<M> items) {
        adapter.setItems((MyArray<VModule>) items);
        reloadContent();
    }

    @Override
    public void reloadContent() {
        MyArray<VModule> items = adapter.getFilteredItems()
                .filter(new MyPredicate<VModule>() {
                    @Override
                    public boolean apply(VModule vModule) {
                        return vModule.getModuleForms().nonEmpty();
                    }
                });
        ViewGroup vg = vsRoot.viewGroup(R.id.rv_list);
        vg.removeAllViews();
        for (VModule module : items) {
            RecyclerAdapter.ViewHolder holder = adapter.createViewHolder(null, -1);
            adapter.populate(holder.vsItem, module);
            vg.addView(holder.vsItem.view);
        }

        try {
            NestedScrollView scrollView = vsRoot.id(R.id.nsv_scroll);
            if (scrollX != 0 || scrollY != 0) {
                scrollView.smoothScrollTo(scrollX, scrollY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyChanged();
        reloadContent();
    }
}
