package uz.greenwhite.smartup5_trade.common.module;// 03.10.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.MoldIndexFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.DividerItemDecoration;
import uz.greenwhite.smartup5_trade.R;

public abstract class ModuleIndexFragment<F extends VForm>
        extends MoldIndexFragment
        implements OnItemClickListener<VForm> {


    private ModuleAdapter adapter;
    private ViewSetup vsRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.z_module_index);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.adapter = new ModuleAdapter(this, this);

        RecyclerView rvList = vsRoot.id(R.id.rv_list);
        rvList.addItemDecoration(new DividerItemDecoration(getActivity()));
        rvList.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvList.setAdapter(adapter);
    }

    protected ViewSetup setHeader(int layout) {
        ViewGroup viewGroup = vsRoot.viewGroup(R.id.ll_header);
        viewGroup.removeAllViews();
        viewGroup.setVisibility(View.VISIBLE);

        ViewSetup vsHeader = new ViewSetup(getActivity(), layout);
        viewGroup.addView(vsHeader.view);
        return vsHeader;
    }

    protected ViewSetup setFooter(int layout) {
        ViewGroup viewGroup = vsRoot.viewGroup(R.id.ll_footer);
        viewGroup.removeAllViews();
        viewGroup.setVisibility(View.VISIBLE);

        ViewSetup vsFooter = new ViewSetup(getActivity(), layout);
        viewGroup.addView(vsFooter.view);
        return vsFooter;
    }

    @SuppressWarnings("unchecked")
    protected <M extends VModule> void setListItems(MyArray<M> items) {
        adapter.setItems((MyArray<VModule>) items);
    }

    protected void showForm(F items) {
        adapter.showForm(items);
    }

    @Override
    public void onDrawerOpened() {
        adapter.notifyChanged();
    }


    public void setModulePredicate(MyPredicate<VModule> modulePredicate) {
        adapter.setModulePredicate(modulePredicate);
    }

    public void setFilterListener(Filter.FilterListener filterListener) {
        adapter.setFilterListener(filterListener);
    }
}
