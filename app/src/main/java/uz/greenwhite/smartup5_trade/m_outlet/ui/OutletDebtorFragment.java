package uz.greenwhite.smartup5_trade.m_outlet.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.math.BigDecimal;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.fab.FloatingActionButton;
import uz.greenwhite.lib.widget.fab.FloatingActionsMenu;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.DebtorApi;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.ui.DebtorFragment;
import uz.greenwhite.smartup5_trade.m_debtor.ui.PrepaymentFragment;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.DebtorFilter;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.DebtorFilterBuilder;
import uz.greenwhite.smartup5_trade.m_outlet.ui.filter.DebtorFilterValue;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDeal;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDebtor;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.PrepaymentPaymentTypes;

public class OutletDebtorFragment extends MoldContentRecyclerFragment<OutletDebtor> {

    public static OutletDebtorFragment newInstance(ArgOutlet arg) {
        return Mold.parcelableArgumentNewInstance(OutletDebtorFragment.class, arg, ArgOutlet.UZUM_ADAPTER);
    }

    public static void open(ArgOutlet arg) {
        Mold.openContent(OutletDebtorFragment.class, Mold.parcelableArgument(arg, ArgOutlet.UZUM_ADAPTER));
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    public DebtorFilter filter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArgOutlet arg = getArgOutlet();
        Outlet outlet = arg.getOutlet();
        Mold.setTitle(getActivity(), outlet.name);
        Mold.setSubtitle(getActivity(), outlet.getAddress());

        CharSequence title = UI.html().v(getString(R.string.outlet_name)).br().i().v(outlet.name).i().html();
        CharSequence detail = UI.html().v(getString(R.string.outlet_address)).br().i().v(outlet.getAddress()).i().html();
        ViewSetup vsHeader = setHeader(R.layout.outlet_debtor_header);
        vsHeader.textView(R.id.tv_title).setText(title);
        vsHeader.textView(R.id.tv_detail).setText(detail);

        setHasLongClick(true);

        setEmptyIcon(R.drawable.ic_monetization_on_black_48dp);
        setEmptyText(R.string.list_is_empty);

        initFabMenu();

        ViewGroup vgHeader = vsHeader.viewGroup(R.id.ll_cashing_header_info);
        vgHeader.setVisibility(View.VISIBLE);

        vgHeader.removeAllViews();

        MyArray<MyArray<Pair<Currency, BigDecimal>>> waitingPostedAbort = OutletApi
                .makeCashingRequest(arg.getScope(), outlet.id);


        MyArray<Pair<Currency, BigDecimal>> waitting = waitingPostedAbort.get(0);
        MyArray<Pair<Currency, BigDecimal>> posted = waitingPostedAbort.get(1);
        MyArray<Pair<Currency, BigDecimal>> abort = waitingPostedAbort.get(2);

        ViewSetup vsCashing = new ViewSetup(getActivity(), R.layout.outlet_cashing_request);
        vgHeader.addView(vsCashing.view);

        if (waitting.nonEmpty()) {
            vsCashing.id(R.id.ll_waiting).setVisibility(View.VISIBLE);
            vsCashing.id(R.id.tv_waiting).setVisibility(View.VISIBLE);
            String strWaiting = waitting.map(new MyMapper<Pair<Currency, BigDecimal>, String>() {
                @Override
                public String apply(Pair<Currency, BigDecimal> item) {
                    return item.first.getNameBaseEmpty() + " " + NumberUtil.formatMoney(item.second);
                }
            }).mkString(", ");
            vsCashing.textView(R.id.tv_waiting).setText(strWaiting.trim());
        }

        if (posted.nonEmpty()) {
            vsCashing.id(R.id.ll_posted).setVisibility(View.VISIBLE);
            vsCashing.id(R.id.tv_posted).setVisibility(View.VISIBLE);
            String strPosted = posted.map(new MyMapper<Pair<Currency, BigDecimal>, String>() {
                @Override
                public String apply(Pair<Currency, BigDecimal> item) {
                    return item.first.getNameBaseEmpty() + " " + NumberUtil.formatMoney(item.second);
                }
            }).mkString(", ");
            vsCashing.textView(R.id.tv_posted).setText(strPosted.trim());
        }

        if (abort.nonEmpty()) {
            vsCashing.id(R.id.ll_abort).setVisibility(View.VISIBLE);
            vsCashing.id(R.id.tv_abort).setVisibility(View.VISIBLE);
            String strAbort = abort.map(new MyMapper<Pair<Currency, BigDecimal>, String>() {
                @Override
                public String apply(Pair<Currency, BigDecimal> item) {
                    return item.first.getNameBaseEmpty() + " " + NumberUtil.formatMoney(item.second);
                }
            }).mkString(", ");
            vsCashing.textView(R.id.tv_abort).setText(strAbort.trim());
        }
    }

    private void initFabMenu() {
        ArgOutlet arg = getArgOutlet();

        boolean hasCash = OutletUtil.hasPrepayment(arg, PrepaymentPaymentTypes.K_CASH);
        boolean hasCard = OutletUtil.hasPrepayment(arg, PrepaymentPaymentTypes.K_CARD);

        if (hasCard || hasCash) {
            Drawable cash = UI.changeDrawableColor(getActivity(), R.drawable.ic_local_atm_black_24dp, R.color.white);
            Drawable card = UI.changeDrawableColor(getActivity(), R.drawable.ic_credit_card_black_24dp, R.color.white);
            FloatingActionsMenu fabMenu = Mold.makeFloatActionMenu(getActivity());

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
                PrepaymentFragment.open(new ArgDebtor(getArgOutlet(), paymentKind));
            }
        });
        menu.addButton(fab);
        return fab;
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        final ArgOutlet arg = getArgOutlet();
        ScopeUtil.execute(arg, new OnScopeReadyCallback<Tuple2>() {
            @Override
            public Tuple2 onScopeReady(Scope scope) {
                MyArray<OutletDebtor> outletDebtor = OutletApi.getOutletDebtor(scope, arg.outletId);

                DebtorFilter filter = DebtorFilterBuilder.build(DebtorFilterValue.makeDefault());
                return new Tuple2(outletDebtor, filter);
            }

            @Override
            public void onDone(Tuple2 items) {
                setListItems((MyArray<OutletDebtor>) items.first);
                filter = (DebtorFilter) items.second;
                setFilterValues();
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
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
    public MoldTuningFragment getTuningFragment() {
        return new DebtorTuningFragment();
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, OutletDebtor item) {
        if (item.isPrepayment()) {
            ArgDebtor arg = new ArgDebtor(getArgOutlet(), item.debtor.localId, item.debtor.paymentKind);
            PrepaymentFragment.open(arg);
        } else {
            ArgDebtor arg = new ArgDebtor(getArgOutlet(), "", item.dealId, item.debtorDate, item.consign);
            DebtorFragment.open(arg);
        }
    }

    @Override
    protected void onItemLongClick(final RecyclerAdapter.ViewHolder holder, final OutletDebtor item) {
        if (item.debtor == null) {
            onItemClick(holder, item);
            return;
        }

        UI.dialog().title(R.string.select)
                .option(R.string.edit, new Command() {
                    @Override
                    public void apply() {
                        ArgOutlet arg = getArgOutlet();
                        DebtorApi.debtorMakeEdit(arg.getScope(), item.debtor.localId);
                        onItemClick(holder, item);
                    }
                })
                .option(R.string.remove, new Command() {
                    @Override
                    public void apply() {
                        ArgOutlet arg = getArgOutlet();
                        DebtorApi.debtorDelete(arg.getScope(), item.debtor.localId);
                        reloadContent();
                    }
                }).show(getActivity());
    }

    @Override
    protected void onListItemChanged() {
        CharSequence paymentDetail = OutletUtil.prepareDebtorPaymentDetail(getListFilteredItems());
        ViewSetup vs = new ViewSetup(getHeader());
        vs.textView(R.id.tv_payment).setText(paymentDetail);
        vs.textView(R.id.tv_payment).setVisibility(TextUtils.isEmpty(paymentDetail) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.outlet_deal_info_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vs, final OutletDebtor item) {
        vs.textView(R.id.tv_title).setText(item.getTitle());
        vs.textView(R.id.tv_detail).setText(item.getDetail());

        CharSequence error = item.getError();
        if (TextUtils.isEmpty(error)) {
            vs.id(R.id.tv_error).setVisibility(View.GONE);
        } else {
            vs.id(R.id.tv_error).setVisibility(View.VISIBLE);
            vs.textView(R.id.tv_error).setText(error);
        }

        Tuple2 icon = item.getStateIcon();

        if (icon != null) {
            vs.viewGroup(R.id.lf_state).setBackground((Drawable) icon.second);
            vs.imageView(R.id.state).setImageDrawable((Drawable) icon.first);
            vs.id(R.id.lf_state).setVisibility(View.VISIBLE);
        } else {
            vs.id(R.id.lf_state).setVisibility(View.GONE);
        }

        ImageView ivEdit = vs.imageView(R.id.iv_edit);
        if (item.hasEdit()) {
            ivEdit.setVisibility(View.VISIBLE);
            ivEdit.setImageDrawable(OutletDeal.EDIT_DRAWABLE);

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArgOutlet arg = getArgOutlet();
                    DebtorApi.debtorMakeEdit(arg.getScope(), item.debtor.localId);
                    onItemClick(null, item);
                }
            });
        } else {
            ivEdit.setVisibility(View.GONE);
        }

    }
}
