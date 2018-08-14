package uz.greenwhite.smartup5_trade.m_session.ui.person;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.fab.FloatingActionButton;
import uz.greenwhite.lib.widget.fab.FloatingActionsMenu;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgPrepaymentOutlet;
import uz.greenwhite.smartup5_trade.m_debtor.ui.PrepaymentOutletFragment;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletDebtorFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.SessionUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.PrepaymentPaymentTypes;
import uz.greenwhite.smartup5_trade.m_session.filter.DebtorFilter;
import uz.greenwhite.smartup5_trade.m_session.filter.DebtorFilterBuilder;
import uz.greenwhite.smartup5_trade.m_session.filter.DebtorFilterValue;
import uz.greenwhite.smartup5_trade.m_session.row.DebtorRow;
import uz.greenwhite.smartup5_trade.m_take_location.ui.MyTakeLocationDialog;
import uz.greenwhite.smartup5_trade.m_take_location.ui.TakeLocationListener;

public class DebtorFragment extends PersonFragment<DebtorRow> implements TakeLocationListener {

    public static DebtorFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(DebtorFragment.class,
                arg, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    public DebtorFilter filter;
    private FloatingActionsMenu fabMenu;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(DebtorRow item, String text) {
                return CharSequenceUtil.containsIgnoreCase(item.title, text);
            }
        });
        addSubMenu(getString(R.string.map), new Command() {
            @Override
            public void apply() {
                if (adapter != null && !UIUtils.showAlertIsEmpty(getActivity(), adapter.getFilteredItems())) {
                    MyTakeLocationDialog.show(getActivity(), getArgSession());
                }
            }
        });

        addSubMenu(DS.getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });
        setEmptyIcon(R.drawable.empty);
        setEmptyText(R.string.session_today_empty);
    }

    @Override
    public void onStart() {
        super.onStart();

        initFabMenu();
    }

    private void makeFooter() {
        final ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.person_count_foter);
        vsFooter.textView(R.id.tv_persons).setText(DS.getString(R.string.debtor));
        vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void populate() {
                vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                this.populate();
            }

            public void onChanged() {
                super.onChanged();
                this.populate();
            }
        });
        BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void initFabMenu() {
        ArgSession arg = getArgSession();

        boolean hasCash = OutletUtil.hasPrepayment(arg, PrepaymentPaymentTypes.K_CASH);
        boolean hasCard = OutletUtil.hasPrepayment(arg, PrepaymentPaymentTypes.K_CARD);

        if (hasCard || hasCash) {
            Drawable cash = UI.changeDrawableColor(getActivity(), R.drawable.ic_local_atm_black_24dp, R.color.white);
            Drawable card = UI.changeDrawableColor(getActivity(), R.drawable.ic_credit_card_black_24dp, R.color.white);
            FloatingActionsMenu fabMenu = Mold.makeFloatActionMenu(getActivity());

            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(fabMenu.getLayoutParams());
            Resources resources = DS.getResources();
            int padding16 = (int) resources.getDimension(R.dimen.padding_16dp);
            int padding80 = (int) resources.getDimension(R.dimen.padding_80dp);

            lp.setMargins(padding16, padding16, padding16, padding80);
            lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            fabMenu.setLayoutParams(lp);


            if (hasCash) {
                makeButton(fabMenu, cash, R.string.outlet_debdot_cash, PrepaymentPaymentTypes.K_CASH);
            }
            if (hasCard) {
                makeButton(fabMenu, card, R.string.outlet_debdot_card, PrepaymentPaymentTypes.K_CARD);
            }
        }
    }

    private FloatingActionButton makeButton(final FloatingActionsMenu menu,
                                            @NonNull Drawable icon,
                                            int titleResId,
                                            final String paymentKind) {
        FloatingActionButton fab = new FloatingActionButton(getActivity());
        fab.setSize(FloatingActionButton.SIZE_MINI);
        fab.setIconDrawable(icon);
        fab.setColorNormalResId(R.color.app_color_accent);
        fab.setColorPressedResId(R.color.app_color_accent);
        fab.setStrokeVisible(true);
        fab.setTitle(getString(titleResId));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                PrepaymentOutletFragment.open(new ArgPrepaymentOutlet(getArgSession(), paymentKind));
            }
        });
        menu.addButton(fab);
        return fab;
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void reloadContent() {
        final DebtorFilterValue filterValue = filter != null ?
                filter.toValue() : DebtorFilterValue.makeDefault();

        ScopeUtil.execute(jobMate, getArgSession(), new OnScopeReadyCallback<Tuple2>() {
            @Override
            public Tuple2 onScopeReady(Scope scope) {
                MyArray<DebtorRow> debtorRows = SessionUtil.getDebtorRows(scope);
                MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
                MyArray<OutletType> types = scope.ref.getOutletTypes();
                DebtorFilter filter = DebtorFilterBuilder.build(filterValue, debtorRows, groups, types);
                return new Tuple2(debtorRows, filter);
            }

            @Override
            public void onDone(Tuple2 debtorRows) {
                setListItems((MyArray<DebtorRow>) debtorRows.first);
                filter = (DebtorFilter) debtorRows.second;
                setFilterValues();
                makeFooter();
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
        if (fabMenu == null) {
            fabMenu = Mold.makeFloatActionMenu(getActivity());
        }
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(fabMenu.getLayoutParams());
        Resources resources = DS.getResources();
        int padding16 = (int) resources.getDimension(R.dimen.padding_16dp);

        lp.setMargins(padding16, padding16, padding16, padding16);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        fabMenu.setLayoutParams(lp);
    }

    public void setFilterValues() {
        if (filter == null) {
            return;
        }
        final MyPredicate<Outlet> predicate = filter.getPredicate();
        if (predicate != null) {
            adapter.predicateOthers = new MyPredicate<DebtorRow>() {
                @Override
                public boolean apply(final DebtorRow outletRow) {
                    return outletRow.outlet != null && predicate.apply(outletRow.outlet);
                }
            };
        } else {
            adapter.predicateOthers = null;
        }
        adapter.filter();
    }


    @Override
    public void onLocationToken(Location location) {
        MyArray<String> outletIds = adapter.getFilteredItems().map(new MyMapper<DebtorRow, String>() {
            @Override
            public String apply(DebtorRow outletRow) {
                return outletRow.outlet.id;
            }
        });
        UIUtils.openNearOutletIndexFragment(getActivity(), getArgSession(), location, outletIds);
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, DebtorRow item) {
        ArgSession arg = getArgSession();
        if (!item.hasDebt && item.hasPrepayment) {
            OutletDebtorFragment.open(new ArgOutlet(arg, item.outlet.id));
        } else {
            OutletIndexFragment.open(new ArgOutlet(arg, item.outlet.id));
        }
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new DebtorTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_outlet_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final DebtorRow item) {
        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<DebtorRow> filteredItems = adapter.getFilteredItems();
            DebtorRow lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        if (item.icon != null) {
            vsItem.viewGroup(R.id.lf_state).setBackground((Drawable) item.icon.second);
            vsItem.imageView(R.id.state).setImageDrawable((Drawable) item.icon.first);
            vsItem.id(R.id.lf_state).setVisibility(View.VISIBLE);
        } else {
            vsItem.id(R.id.lf_state).setVisibility(View.GONE);
        }
        vsItem.textView(R.id.title).setText(item.title);
        vsItem.textView(R.id.detail).setText(item.detail);

        CharSequence balanceReceivable = item.getBalanceReceivable();
        if (!TextUtils.isEmpty(balanceReceivable)) {
            vsItem.textView(R.id.tv_balance_receivable).setText(balanceReceivable);
            vsItem.id(R.id.tv_balance_receivable).setVisibility(View.VISIBLE);
        } else vsItem.id(R.id.tv_balance_receivable).setVisibility(View.GONE);


        jobMate.execute(new FetchImageJob(getArgSession().accountId, item.outlet.photoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsItem.imageView(R.id.iv_avatar).setImageBitmap(result);
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.GONE);
                            } else {
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            }
                        } else {
                            vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                            vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });

        Location mLocation = SmartupApp.getLocation();
        if (mLocation != null) {
            CharSequence outletDistance = item.getOutletDistance(mLocation);
            vsItem.id(R.id.tv_outlet_distance).setVisibility(TextUtils.isEmpty(outletDistance) ? View.GONE : View.VISIBLE);
            vsItem.textView(R.id.tv_outlet_distance).setText(outletDistance);
        } else {
            vsItem.id(R.id.tv_outlet_distance).setVisibility(View.GONE);
        }
    }
}
