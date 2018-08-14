package uz.greenwhite.smartup5_trade.m_outlet.ui;// 08.09.2016

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.OShippedFilter;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.OShippedFilterBuilder;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.OShippedFilterValue;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletSDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedApi;
import uz.greenwhite.smartup5_trade.m_shipped.arg.ArgSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SDealIndexFragment;

public class ShippedFragment extends MoldContentRecyclerFragment<OutletSDeal> {

    public static ShippedFragment newInstance(ArgOutlet arg) {
        return Mold.parcelableArgumentNewInstance(ShippedFragment.class, arg, ArgOutlet.UZUM_ADAPTER);
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    public OShippedFilter filter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addSubMenu(getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });

        ArgOutlet arg = getArgOutlet();
        Outlet outlet = arg.getOutlet();

        CharSequence title = UI.html().v(getString(R.string.outlet_name)).br().i().v(outlet.name).i().html();
        CharSequence detail = UI.html().v(getString(R.string.outlet_address)).br().i().v(outlet.getAddress()).i().html();

        ViewSetup vsHeader = setHeader(R.layout.outlet_shipped_header);
        vsHeader.textView(R.id.tv_title).setText(title);
        vsHeader.textView(R.id.tv_detail).setText(detail);

        setHasLongClick(true);
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, OutletSDeal item) {
        ArgSDeal argSDeal = new ArgSDeal(getArgOutlet(), item.holder.entryId, item.holder.deal.dealId);
        SDealIndexFragment.open(argSDeal);
    }

    @Override
    protected void onItemLongClick(final RecyclerAdapter.ViewHolder holder, final OutletSDeal item) {
        if (TextUtils.isEmpty(item.holder.entryId)) {
            onItemClick(holder, item);
            return;
        }

        UI.dialog().title(R.string.select).option(R.string.open, new Command() {
            @Override
            public void apply() {
                onItemClick(holder, item);
            }
        }).option(R.string.edit, new Command() {
            @Override
            public void apply() {
                ArgOutlet arg = getArgOutlet();
                ShippedApi.dealMakeEdit(arg.getScope(), item.holder);
                onItemClick(holder, item);
            }
        }).option(R.string.remove, new Command() {
            @Override
            public void apply() {
                ArgOutlet arg = getArgOutlet();
                ShippedApi.dealDelete(arg.getScope(), item.holder);
                reloadContent();
            }
        }).show(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        OShippedFilterValue filterValue = filter != null ? filter.toValue() : OShippedFilterValue.makeDefault();
        filter = OShippedFilterBuilder.build(filterValue);

        final ArgOutlet arg = getArgOutlet();
        ScopeUtil.execute(jobMate, arg, new OnScopeReadyCallback<MyArray<OutletSDeal>>() {
            @Override
            public MyArray<OutletSDeal> onScopeReady(Scope scope) {
                return OutletApi.getOutletSDeals(scope, arg.outletId);
            }

            @Override
            public void onDone(MyArray<OutletSDeal> outletSDeals) {
                setListItems(outletSDeals);
                setFilterValues();
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(throwable).message).show();
                setListItems(MyArray.<OutletSDeal>emptyArray());
            }
        });
    }

    public void setFilterValues() {
        if (filter == null || adapter == null) {
            return;
        }
        adapter.predicateOthers = filter.getPredicate();
        adapter.filter();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    protected void onListItemChanged() {
        CharSequence paymentDetail = OutletUtil.prepareSDealPaymentDetail(getListFilteredItems());
        ViewSetup vs = new ViewSetup(getHeader());
        vs.textView(R.id.tv_payment).setText(paymentDetail);
        vs.textView(R.id.tv_payment).setVisibility(TextUtils.isEmpty(paymentDetail) ? View.GONE : View.VISIBLE);
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new ShippedTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.outlet_sdeal;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, OutletSDeal item) {
        Tuple2 icon = item.getStateIcon();

        if (icon != null) {
            vsItem.viewGroup(R.id.lf_state).setBackground((Drawable) icon.second);
            vsItem.imageView(R.id.state).setImageDrawable((Drawable) icon.first);
            vsItem.id(R.id.lf_state).setVisibility(View.VISIBLE);
        } else {
            vsItem.id(R.id.lf_state).setVisibility(View.GONE);
        }
        vsItem.textView(R.id.title).setText(item.getTitle());
        vsItem.textView(R.id.detail).setText(item.getDetail());
    }
}
