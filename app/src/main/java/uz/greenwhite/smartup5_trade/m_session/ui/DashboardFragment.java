package uz.greenwhite.smartup5_trade.m_session.ui;// 03.11.2016

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.util.Date;
import java.util.List;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.user.User;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.common.widget.HorizontalChart;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal_history.arg.ArgHistory;
import uz.greenwhite.smartup5_trade.m_deal_history.ui.DealHistoryFragment;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.DashboardUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgProductKpi;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.Dashboard;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.ProductPriceRow;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.ProductRow;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.row.DashboardProductKpiRow;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;

public class DashboardFragment extends MoldContentFragment {

    public static DashboardFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(DashboardFragment.class, arg, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;
    private TradeRoleKeys tradeRoleKeys;
    private int pushedButtonId = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.session_dashboard);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tradeRoleKeys = getArgSession().getScope().ref.getRoleKeys();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadContent();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    public void reloadContent() {
        ErrorUtil.tryCatch(new OnTryCatchCallback() {
            @Override
            public void onTry() throws Exception {
                ArgSession arg = getArgSession();
                final Activity activity = getActivity();
                final User user = arg.getUser();
                vsRoot.id(R.id.ll_dashboard_content).setVisibility(View.GONE);
                vsRoot.id(R.id.ll_progressContainer).setVisibility(View.VISIBLE);

                final String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);

                ScopeUtil.execute(jobMate, arg, new OnScopeReadyCallback<MyArray<ViewSetup>>() {

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public MyArray<ViewSetup> onScopeReady(Scope scope) {
                        Dashboard dashboard = scope.ref.getDashboard();

                        return MyArray.from(
                                makeInfo(scope, today),
                                makeKpiView(scope),
                                makeOutletPlan(scope, dashboard, today),
                                makeOutletDeal(scope, dashboard, today),
                                makeOutletReturn(scope, dashboard, today),

                                makeProductInfo(activity, scope, dashboard, true),
                                makeProductInfo(activity, scope, dashboard, false)
                        ).filterNotNull();
                    }

                    @Override
                    public void onDone(MyArray<ViewSetup> viewSetups) {
                        vsRoot.id(R.id.ll_dashboard_content).setVisibility(View.VISIBLE);
                        vsRoot.id(R.id.ll_progressContainer).setVisibility(View.GONE);

                        vsRoot.textView(R.id.tv_user_name).setText(getString(R.string.session_welcome, user.name));
                        vsRoot.textView(R.id.tv_header_date).setText(DashboardUtil.getToday(today));

                        LinearLayout ll = vsRoot.id(R.id.ll_dashboard);
                        ll.removeAllViews();
                        for (ViewSetup vs : viewSetups) {
                            ll.addView(vs.view);
                        }
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        super.onFail(throwable);
                        UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                    }
                });
            }

            @Override
            public void onCatch(Exception ex) throws Exception {
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private ViewSetup makeInfo(final Scope scope, final String today) {
        if (!Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.expeditor, tradeRoleKeys.agentMerchandiser) &&
                !Utils.isRole(scope, Role.DOCTOR, Role.PHARMACY, Role.PHARMCY_PLUS_DOCTOR,
                        tradeRoleKeys.merchandiser, tradeRoleKeys.vanseller)) {
            return null;
        }
        ViewSetup vs = new ViewSetup(getActivity(), R.layout.dashboard_info2);
        final FitChart chart = vs.id(R.id.fc_chart);

        if (Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser, tradeRoleKeys.vanseller)) {
            final FitChart chart2 = vs.id(R.id.fc_chart2);
            vs.id(R.id.fl_deal_history).setVisibility(View.VISIBLE);
            MyArray<DealHistory> dealHistories = scope.ref.getDealHistories().filter(new MyPredicate<DealHistory>() {
                @Override
                public boolean apply(DealHistory dealHistory) {
                    return dealHistory.dealDate.equals(today);
                }
            });
            int allDeals = 0, dealDraft = 0, dealNew = 0;
            for (DealHistory deal : dealHistories) {
                allDeals++;
                if (DealHistory.DEAL_STATE_DRAFT.equals(deal.dealState)) {
                    dealDraft++;
                } else if (DealHistory.DEAL_STATE_NEW.equals(deal.dealState)) {
                    dealNew++;
                }
            }

            final boolean hasDeals = allDeals > 0;
            chart2.setMaxValue(allDeals);
            final List<FitChartValue> fitChartValues = MyArray.from(
                    new FitChartValue(dealDraft, DS.getColor(R.color.deal_draft)),
                    new FitChartValue(dealNew, DS.getColor(R.color.deal_new)),
                    new FitChartValue(allDeals - (dealDraft + dealNew), DS.getColor(R.color.deal_others))
            ).asList();

            CharSequence historyInfo = UI.html().c("#343434").v(String.valueOf(dealNew)).c().v("/")
                    .c("#9EA2A3").v(String.valueOf(dealDraft)).br().v(DS.getString(R.string.session_dashboard_deal_history_info)).c().html();

            vs.textView(R.id.tv_history_info).setText(historyInfo);

            Manager.handler.post(new Runnable() {
                @Override
                public void run() {
                    chart2.setValues(fitChartValues);

                }
            });

            chart2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hasDeals) {
                        DealHistoryFragment.Companion.open(getActivity(), new ArgHistory(getArgSession()));
                    }
                }
            });
        }

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArgSession arg = getArgSession();
                Setting setting = arg.getSetting();
                if (!setting.deal.visitAllow) {
                    return;
                }
                SessionIndexFragment index = Mold.getIndexFragment(getActivity());
                if (Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser,
                        tradeRoleKeys.merchandiser, Role.DOCTOR, Role.PHARMACY, tradeRoleKeys.vanseller)) {
                    index.showForm(SessionIndexFragment.TODAY_VISIT);
                } else if (Utils.isRole(scope, tradeRoleKeys.expeditor)) {
                    index.showForm(SessionIndexFragment.TODAY_SHIPPED);
                }
            }
        });

        int totalVisit = 0;
        int planTotal = 0;
        if (Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser) ||
                Utils.isRole(scope, Role.DOCTOR, Role.PHARMACY, Role.PHARMCY_PLUS_DOCTOR,
                        tradeRoleKeys.merchandiser, tradeRoleKeys.vanseller)) {
            totalVisit = DashboardUtil.visitTotal(scope);
            planTotal = DashboardUtil.planTotal(scope).size();

        } else if (Utils.isRole(scope, tradeRoleKeys.expeditor)) {
            Tuple2 shippedInfo = DashboardUtil.shippedTotalInfo(scope);
            totalVisit = (int) shippedInfo.first;
            planTotal = (int) shippedInfo.second;
        }

        chart.setMaxValue(planTotal);

        CharSequence visitInfo = UI.html().c("#343434").v(String.valueOf(planTotal)).c().v("/")
                .c("#9EA2A3").v(String.valueOf(totalVisit)).br().v(DS.getString(R.string.session_dashboard_visits_info)).c().html();

        vs.textView(R.id.tv_visit_deal_info).setText(visitInfo);

        final int finalTotalVisit = totalVisit;
        Manager.handler.post(new Runnable() {
            @Override
            public void run() {
                chart.setValue(finalTotalVisit);
            }
        });
        return vs;
    }

    private MyCommand<OutletRow> makeItemClick(final Scope scope) {
        final FragmentActivity activity = getActivity();
        return new MyCommand<OutletRow>() {
            @Override
            public void apply(OutletRow val) {
                if (DSUtil.getFilialOutlets(scope).contains(val.outlet.id, Outlet.KEY_ADAPTER)) {
                    OutletIndexFragment.open(new ArgOutlet(getArgSession(), val.outlet.id));
                } else {
                    String message = getString(R.string.you_can_note_work_with_outlet, val.outlet.name);
                    Mold.makeSnackBar(activity, message).show();
                }
            }
        };
    }

    private ViewSetup makeOutletPlan(final Scope scope, Dashboard dashboard, String today) {
        MyCommand<OutletRow> itemClick = makeItemClick(scope);
        MyArray<OutletRow> customers = DashboardUtil.getCustomers(scope, dashboard, today);
        return makeOutlet(R.string.visit_outlets, customers, itemClick);
    }

    private ViewSetup makeOutletDeal(Scope scope, Dashboard dashboard, String today) {
        MyCommand<OutletRow> itemClick = makeItemClick(scope);
        MyArray<OutletRow> customers = DashboardUtil.getDealCustomers(scope, dashboard, today);
        return makeOutlet(R.string.deal_outlets, customers, itemClick);
    }

    private ViewSetup makeOutletReturn(Scope scope, Dashboard dashboard, String today) {
        MyCommand<OutletRow> itemClick = makeItemClick(scope);
        MyArray<OutletRow> customer = DashboardUtil.getReturnCustomers(scope, dashboard, today);
        return makeOutlet(R.string.return_outlets, customer, itemClick);
    }

    private ViewSetup makeOutlet(int title,
                                 MyArray<OutletRow> items,
                                 final MyCommand<OutletRow> itemClick) {
        if (items.isEmpty()) return null;

        int listSize = items.size();
        int maxSize = listSize > 3 ? 3 : listSize;

        ViewSetup vs = new ViewSetup(getActivity(), R.layout.dashboard_outlet_visit);
        vs.textView(R.id.tv_title).setText(title);

        ViewGroup vg = vs.viewGroup(R.id.ll_outlet_row);
        vg.removeAllViews();
        for (int i = 0; i < maxSize; i++) {
            final OutletRow item = items.get(i);

            final ViewSetup row = new ViewSetup(getActivity(), R.layout.z_outlet_row);
            row.id(R.id.ll_outlet_row_bottom_line).setVisibility(maxSize - 1 == i ? View.GONE : View.VISIBLE);
            Tuple2 icon = item.getIcon();
            if (icon != null) {
                row.viewGroup(R.id.lf_state).setBackground((Drawable) icon.second);
                row.imageView(R.id.state).setImageDrawable((Drawable) icon.first);
                row.id(R.id.lf_state).setVisibility(View.VISIBLE);
            } else {
                row.id(R.id.lf_state).setVisibility(View.GONE);
            }
            row.textView(R.id.title).setText(item.title);
            row.textView(R.id.tv_info).setText(item.getLstVisitDate());
            row.textView(R.id.detail).setText(item.detail);
            row.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClick != null) itemClick.apply(item);
                }
            });
            vg.addView(row.view);

            row.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
            row.imageView(R.id.iv_avatar).setBackgroundResource(item.image);

            Manager.handler.post(new Runnable() {
                @Override
                public void run() {
                    jobMate.execute(new FetchImageJob(getArgSession().accountId, item.outlet.photoSha))
                            .always(new Promise.OnAlways<Bitmap>() {
                                @Override
                                public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                                    if (resolved) {
                                        if (result != null) {
                                            row.imageView(R.id.iv_avatar).setImageBitmap(result);
                                            row.imageView(R.id.iv_icon).setVisibility(View.GONE);
                                        } else {
                                            row.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                            row.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                                        }
                                    } else {
                                        row.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                        row.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                                        if (error != null) error.printStackTrace();
                                    }
                                }
                            });
                }
            });

        }
        if (listSize > 3) {
            vs.id(R.id.ll_show_all).setVisibility(View.VISIBLE);
            vs.id(R.id.tv_show_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Mold.showContent(getActivity(), SessionIndexFragment.OUTLET_SHOP);
                }
            });
        }
        return vs;
    }

    private ViewSetup makeKpiView(final Scope scope) {
        final Setter<MyArray<DashboardProductKpiRow>> result = new Setter<>();

        TradeRoleKeys tradeRoleKeys = scope.ref.getRoleKeys();
        if (!Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser, tradeRoleKeys.supervisor, tradeRoleKeys.vanseller) &&
                !Utils.isRole(scope, Role.DOCTOR, Role.PHARMACY, Role.PHARMCY_PLUS_DOCTOR, tradeRoleKeys.merchandiser)) {
            return null;
        }

        final MyArray<DashboardProductKpiRow> kpiPt = scope.cache.containsKey(Scope.C_KPI_1) ?
                (MyArray<DashboardProductKpiRow>) scope.cache.get(Scope.C_KPI_1) :
                DashboardUtil.makeDashboardProductTypeKpi(scope);

        final MyArray<DashboardProductKpiRow> kpiP = scope.cache.containsKey(Scope.C_KPI_2) ?
                (MyArray<DashboardProductKpiRow>) scope.cache.get(Scope.C_KPI_2) :
                DashboardUtil.makeDashboardProductPlan(scope);

        final MyArray<DashboardProductKpiRow> kpiR = scope.cache.containsKey(Scope.C_KPI_3) ?
                (MyArray<DashboardProductKpiRow>) scope.cache.get(Scope.C_KPI_3) :
                DashboardUtil.makeDashboardRoomPlan(scope);

        MyArray<MyArray<DashboardProductKpiRow>> kpiArray = MyArray.from(kpiPt, kpiP, kpiR).filter(new MyPredicate<MyArray<DashboardProductKpiRow>>() {
            @Override
            public boolean apply(MyArray<DashboardProductKpiRow> val) {
                return val != null && val.nonEmpty();
            }
        });

        if (kpiArray.nonEmpty()) {
            result.value = kpiArray.get(0);


            Setting setting = scope.ref.getSettingWithDefault();

            if (!setting.common.showKPIPlanInDashboard) {
                return null;
            }


            ViewSetup vs = new ViewSetup(getActivity(), R.layout.dashboard_kpi_plan);
            ViewGroup vg = vs.viewGroup(R.id.ll_content);
            vg.removeAllViews();

            final ViewSetup vsPlan = new ViewSetup(getActivity(), R.layout.dashboard_kpi_plan_row);
            final ViewGroup vgPlan = vsPlan.viewGroup(R.id.ll_kpi_plan_row);

            final Button btnKPIpt = vsPlan.button(R.id.btn_kpi_pt);                                     // pt = product type
            Button btnKPIp = vsPlan.button(R.id.btn_kpi_p);                                             // p = product
            Button btnKPIr = vsPlan.button(R.id.btn_kpi_r);                                             // r = room

            btnKPIpt.setText(DS.getString(R.string.kpi_product_type));
            btnKPIp.setText(DS.getString(R.string.kpi_product));
            btnKPIr.setText(DS.getString(R.string.kpi_room));

            btnKPIpt.setVisibility(kpiPt != null && kpiPt.nonEmpty() ? View.VISIBLE : View.GONE);
            btnKPIp.setVisibility(kpiP != null && kpiP.nonEmpty() ? View.VISIBLE : View.GONE);
            btnKPIr.setVisibility(kpiR != null && kpiR.nonEmpty() ? View.VISIBLE : View.GONE);

            final RadioGroup rg = vsPlan.id(R.id.rg_kpi);
            final RadioButton firstChild = (RadioButton) rg.getChildAt(0);


            btnKPIpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pushedButtonId != 0 && pushedButtonId != v.getId()) {
                        Button anotherButton = vsPlan.button(pushedButtonId);
                        anotherButton.setBackground(DS.getDrawable(R.drawable.gwslib_button_background));
                    }

                    if (pushedButtonId != v.getId()) {
                        pushedButtonId = v.getId();
                        v.setBackgroundColor(DS.getColor(R.color.colorPrimary));

                        result.value = kpiPt;
                        String key = (String) vsPlan.id(rg.getCheckedRadioButtonId()).getTag();
                        if (key != null) {
                            updateKpiInfo(vgPlan, result.value, key);
                        } else {
                            firstChild.setChecked(true);
                        }
                    }
                }
            });

            btnKPIpt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Mold.makeSnackBar(getActivity(), DS.getString(R.string.kpi_product_type)).show();
                    return true;
                }
            });

            btnKPIp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pushedButtonId != 0 && pushedButtonId != v.getId()) {
                        Button anotherButton = vsPlan.button(pushedButtonId);
                        anotherButton.setBackground(DS.getDrawable(R.drawable.gwslib_button_background));
                    }

                    if (pushedButtonId != v.getId()) {
                        pushedButtonId = v.getId();
                        v.setBackgroundColor(DS.getColor(R.color.colorPrimary));

                        result.value = kpiP;
                        String key = (String) vsPlan.id(rg.getCheckedRadioButtonId()).getTag();
                        if (key != null) {
                            updateKpiInfo(vgPlan, result.value, key);
                        } else {
                            firstChild.setChecked(true);
                        }
                    }
                }
            });


            btnKPIp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Mold.makeSnackBar(getActivity(), DS.getString(R.string.kpi_product)).show();
                    return true;
                }
            });

            btnKPIr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pushedButtonId != 0 && pushedButtonId != v.getId()) {
                        Button anotherButton = vsPlan.button(pushedButtonId);
                        anotherButton.setBackground(DS.getDrawable(R.drawable.gwslib_button_background));
                    }

                    if (pushedButtonId != v.getId()) {
                        pushedButtonId = v.getId();
                        v.setBackgroundColor(DS.getColor(R.color.colorPrimary));

                        result.value = kpiR;
                        String key = (String) vsPlan.id(rg.getCheckedRadioButtonId()).getTag();
                        if (key != null) {
                            updateKpiInfo(vgPlan, result.value, key);
                        } else {
                            firstChild.setChecked(true);
                        }
                    }
                }
            });


            btnKPIr.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Mold.makeSnackBar(getActivity(), DS.getString(R.string.kpi_room)).show();
                    return true;
                }
            });

            Manager.handler.post(new Runnable() {
                @Override
                public void run() {
//                check first object by default (on start)
                    btnKPIpt.setBackgroundColor(DS.getColor(R.color.colorPrimary));
                    pushedButtonId = btnKPIpt.getId();
                    firstChild.setChecked(true);
                }
            });


            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = vsPlan.id(checkedId);
                    final String key = (String) rb.getTag();
                    updateKpiInfo(vgPlan, result.value, key);

                }
            });


            vg.addView(vsPlan.view);
            return vs;
        } else {
            return null;
        }
    }


    //----------------------------------------------------------------------------------------------

    private ViewSetup makeProductInfo(Activity activity, Scope scope, Dashboard dashboard, boolean order) {
        ProductRow row = DashboardUtil.getVisitProduct(scope, dashboard, order);
        if (row.priceRows.isEmpty()) {
            return null;
        }

        ViewSetup vs = new ViewSetup(activity, R.layout.dashboard_product);
        vs.textView(R.id.tv_product_info_title).setText(order ?
                R.string.dashboard_order_product : R.string.dashboard_extraordinary_product);

        ViewGroup vg = vs.viewGroup(R.id.ll_product_row);
        vg.removeAllViews();
        for (ProductPriceRow r : row.priceRows) {
            String productCount = NumberUtil.formatMoney(r.getCount());
            String totalSum = NumberUtil.formatMoney(r.getTotalSum());

            ViewSetup vsRow = new ViewSetup(activity, R.layout.dashboard_product_row);
            vsRow.textView(R.id.tv_price_type).setText(getString(R.string.dashboard_price_type, r.priceName));
            vsRow.textView(R.id.tv_count).setText(getString(R.string.dashboard_count, productCount));
            vsRow.textView(R.id.tv_total_sum).setText(getString(R.string.dashboard_total_sum, totalSum));
            vg.addView(vsRow.view);
        }
        return vs;
    }

    private void updateKpiInfo(ViewGroup vgPlan, final MyArray<DashboardProductKpiRow> value, final String checkedRbTag) {
        MyArray<DashboardProductKpiRow> temp = value.filter(new MyPredicate<DashboardProductKpiRow>() {
            @Override
            public boolean apply(DashboardProductKpiRow val) {
                return val.planType.equalsIgnoreCase(checkedRbTag);
            }
        });

        vgPlan.removeAllViews();
        for (final DashboardProductKpiRow plan : temp) {
            ViewSetup vsItem = new ViewSetup(getActivity(), R.layout.dashboard_kpi_product_plan);
            vsItem.textView(R.id.tv_room_name).setText(plan.getTitle());

            ViewGroup vgItem = vsItem.viewGroup(R.id.ll_product_plan);
            vgItem.removeAllViews();

            for (final DashboardProductKpiRow.Detail detail : plan.details) {
                ViewSetup vsDetail = new ViewSetup(getActivity(), R.layout.dashboard_kpi_product_type_row);
                vsDetail.textView(R.id.tv_product_group_type).setText(detail.name);
                HorizontalChart chart = vsDetail.id(R.id.hc_plan_chart);
                chart.setLines(detail.generateChartLine(plan.planType));

                vgItem.addView(vsDetail.view);

                vsDetail.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductKpiDialog.open(getActivity(), new ArgProductKpi(getArgSession(),
                                plan.room != null ? plan.room.id : "",
                                plan.planType, detail.id, value));
                    }
                });
            }
            vgPlan.addView(vsItem.view);
        }
    }

}
