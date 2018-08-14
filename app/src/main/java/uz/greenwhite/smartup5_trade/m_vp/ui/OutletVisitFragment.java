package uz.greenwhite.smartup5_trade.m_vp.ui;// 23.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Button;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.MyContentRecyclerFragment;
import uz.greenwhite.smartup5_trade.common.predicate.OutletFilter;
import uz.greenwhite.smartup5_trade.common.predicate.OutletFilterBuilder;
import uz.greenwhite.smartup5_trade.common.predicate.OutletFilterValue;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.DoctorHospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_vp.ArgVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp.VPApi;
import uz.greenwhite.smartup5_trade.m_vp.variable.VOutlet;

public class OutletVisitFragment extends MyContentRecyclerFragment<VOutlet> {

    public static void open(ArgVisitPlan arg) {
        Mold.openContent(OutletVisitFragment.class, Mold.parcelableArgument(arg, ArgVisitPlan.UZUM_ADAPTER));
    }

    public ArgVisitPlan getArgVisitPlan() {
        return Mold.parcelableArgument(this, ArgVisitPlan.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    public OutletFilter filter;
    private boolean isCheckedAll = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VOutlet val, String str) {
                return CharSequenceUtil.containsIgnoreCase(val.title, str);
            }
        });

        try {
            VisitData data = Mold.getData(getActivity());
            if (data == null) {
                data = new VisitData(getArgVisitPlan());
                Mold.setData(getActivity(), data);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
        }

        setFooterHeight(150);

        VisitData data = Mold.getData(getActivity());
        if (data != null)
            setListItems(data.vVisit.outlets.getItems());


        ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.visit_footer);
        BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        vsFooter.id(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllOrReset((Button) v);
            }
        });
        vsFooter.id(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVisitPlan();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadContent();
    }


    private void checkAllOrReset(Button btn) {
        VisitData data = Mold.getData(getActivity());
        if (data == null) return;
        MyArray<VOutlet> items = data.vVisit.outlets.getItems();
        for (VOutlet val : items) {
            if (isCheckedAll) {
                val.check.setValue(false);
            } else {
                val.check.setValue(true);
            }
        }
        if (!isCheckedAll) {
            btn.setText(DS.getString(R.string.visit_reset));
            isCheckedAll = true;
        } else {
            btn.setText(DS.getString(R.string.visit_checkAll));
            isCheckedAll = false;
        }
        setListItems(items);
    }

    public void saveVisitPlan() {
        try {
            ArgVisitPlan arg = getArgVisitPlan();
            VisitData data = Mold.getData(getActivity());
            MyArray<OutletPlan> result = data.vVisit.convert(arg.getScope());
            VPApi.save(arg.getScope(), result);

            getActivity().finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorUtil.saveThrowable(ex);
            UI.alertError(getActivity(), ErrorUtil.getErrorMessage(ex).message.toString());
        }
    }

    @Override
    public void reloadContent() {
        final VisitData data = Mold.getData(getActivity());

        final OutletFilterValue filterValue = filter != null ?
                filter.toValue() : OutletFilterValue.DEFAULT;

        ScopeUtil.execute(jobMate, data.arg, new OnScopeReadyCallback<OutletFilter>() {
            @Override
            public OutletFilter onScopeReady(final Scope scope) {
                MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
                MyArray<OutletType> types = scope.ref.getOutletTypes();
                MyArray<Region> regions = scope.ref.getRegions();
                MyArray<PersonLastInfo> outletDatas = scope.ref.getPersonLastInfo();
                MyArray<DoctorHospital> hospitals = scope.ref.getDoctorHospitals();

                MyArray<Outlet> outlets = data.vVisit.outlets.getItems().map(new MyMapper<VOutlet, Outlet>() {
                    @Override
                    public Outlet apply(VOutlet val) {
                        return val.outlet;
                    }
                });

                return OutletFilterBuilder.build(filterValue, outlets, groups, types, null, regions, outletDatas, hospitals);
            }

            @Override
            public void onDone(OutletFilter result) {
                OutletVisitFragment.this.filter = result;
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new OutletVisitTuningFragment();
    }

    public void setFilterValues() {
        if (filter == null) return;
        final MyPredicate<Outlet> predicate = filter.getPredicate();
        if (predicate != null) {
            setFilterPredicate(new MyPredicate<VOutlet>() {
                @Override
                public boolean apply(VOutlet val) {
                    return predicate.apply(val.outlet);
                }
            });
        } else setFilterPredicate(null);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.visit_simple_select_item;
    }

    @Override
    protected void adapterPopulateItem(ViewSetup vsItem, VOutlet item) {
        UI.bind(vsItem.compoundButton(R.id.cb_check), item.check);
        vsItem.textView(R.id.tv_title).setText(item.title);
        vsItem.textView(R.id.tv_detail).setText(item.detail);
    }
}
