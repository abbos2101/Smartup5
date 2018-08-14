package uz.greenwhite.smartup5_trade.m_outlet.ui;// 29.06.2016

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.mold.LocationUtil;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumParcellable;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.arg.ArgFilial;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup.anor.m_task.arg.ArgTask;
import uz.greenwhite.smartup.anor.m_task.arg.ArgTaskInfo;
import uz.greenwhite.smartup.anor.m_task.ui.TaskInfoFragment;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.RootUtil;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.common.dialog.MyDatePickerDialog;
import uz.greenwhite.smartup5_trade.common.dialog.SyncDialog;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealApi;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealIndexFragment;
import uz.greenwhite.smartup5_trade.m_deal_history.arg.ArgHistory;
import uz.greenwhite.smartup5_trade.m_deal_history.ui.DealHistoryFragment;
import uz.greenwhite.smartup5_trade.m_debtor.DebtorApi;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.ui.DebtorFragment;
import uz.greenwhite.smartup5_trade.m_debtor.ui.PrepaymentFragment;
import uz.greenwhite.smartup5_trade.m_display.ui.DisplayFragment;
import uz.greenwhite.smartup5_trade.m_module_edit.ModuleApi;
import uz.greenwhite.smartup5_trade.m_module_edit.arg.ArgModule;
import uz.greenwhite.smartup5_trade.m_module_edit.ui.ModuleSettingFragment;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.Memo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonTask;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDeal;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDealInfo;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDebtor;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletSDeal;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportOutletFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSync;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialSetting;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleMenu;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.ui.SyncFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedApi;
import uz.greenwhite.smartup5_trade.m_shipped.arg.ArgSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SDealIndexFragment;
import uz.greenwhite.smartup5_trade.m_take_location.ui.MyTakeLocationDialog;
import uz.greenwhite.smartup5_trade.m_take_location.ui.TakeLocationListener;
import uz.greenwhite.smartup5_trade.m_vp.ArgVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp.VPApi;
import uz.greenwhite.smartup5_trade.m_vp.builder.BuilderVisit;
import uz.greenwhite.smartup5_trade.m_vp.variable.VOutlet;
import uz.greenwhite.smartup5_trade.m_vp.variable.VVisit;

public class OutletIndexFragment extends MoldContentFragment implements TakeLocationListener {

    public static void open(ArgOutlet arg) {
        Mold.openContent(OutletIndexFragment.class, Mold.parcelableArgument(arg, ArgOutlet.UZUM_ADAPTER));
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    public static final int EXIT = 0;
    //    public static final int DEAL = 1;
    public static final int INFO = 2;
    public static final int REPORT = 3;
    public static final int SHIPPED = 4;
    public static final int DEBTOR = 5;
    public static final int DISPLAY_BARCODE = 6;
    public static final int DEAL_FULL_VISIT = 7;
    public static final int DEAL_FAST_VISIT = 8;
    public static final int DEAL_RETURN = 9;
    public static final int CATEGORIZATION = 10;
    public static final int SV_VISIT = 11;
    public static final int PERSON_FILES = 12;
    public static final int DEAL_HISTORY = 13;
    public static final int SYNC = 14;

    public static final int DEAL_REQUEST = 1000;

    public static final String DEAL_REQUEST_ROOM = "deal_request_room";

    public static final MyArray<NavigationItem> FORMS = MyArray.from(
            new NavigationItem(DEAL_HISTORY, DS.getString(R.string.outlet_deal_history), R.drawable.book),
            new NavigationItem(INFO, DS.getString(R.string.info), R.drawable.store_1),
//            new NavigationItem(DEAL, DS.getString(R.string.visit), R.drawable.store_2),
            new NavigationItem(SV_VISIT, DS.getString(R.string.sv_visit), R.drawable.store_2),
            new NavigationItem(SHIPPED, DS.getString(R.string.shipped), R.drawable.ic_local_shipping_black_24dp),
            new NavigationItem(REPORT, DS.getString(R.string.reporting), R.drawable.store_5),
            new NavigationItem(DEBTOR, DS.getString(R.string.debtor), R.drawable.book),
            new NavigationItem(DISPLAY_BARCODE, DS.getString(R.string.outlet_display_barcode), R.drawable.ic_filter_center_focus_black_24dp)
    );

    public static MyArray<NavigationItem> getFormItems(ArgOutlet argOutlet, boolean withPersonId) {
        Scope scope = argOutlet.getScope();
        assert scope.ref != null;
        FilialSetting filialSetting = scope.ref.getFilialSetting();
        TradeRoleKeys roleKeys = scope.ref.getRoleKeys();
        Setting setting = argOutlet.getSetting();

        MyArray<NavigationItem> result = FORMS;

        if (filialSetting.outletIds != null && !filialSetting.outletIds.contains(argOutlet.outletId, MyMapper.<String>identity())) {
            result = result.append(new NavigationItem(CATEGORIZATION, DS.getString(R.string.categorization), R.drawable.ic_assignment_black_24dp));
        }

        result = RoleMenu.sortForms(argOutlet, RoleMenu.PERSON, result, NavigationItem.KEY_ADAPTER);

        result = result.append(new NavigationItem(SYNC, DS.getString(R.string.admin_sync), R.mipmap.menu_2));
        result = result.append(new NavigationItem(PERSON_FILES, DS.getString(R.string.outlet_person_file), R.drawable.ic_folder_open_black_48dp));
        result = result.append(new NavigationItem(EXIT, DS.getString(R.string.exit_go_out), R.drawable.ic_exit_to_app_black_24dp));

        if (Utils.isRole(scope, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.merchandiser, roleKeys.vanseller)) {

            if (setting.deal.returnAllow && Utils.isRole(scope, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.expeditor)) {
                result = result.prepend(new NavigationItem(DEAL_RETURN, DS.getString(R.string.outlet_return), R.drawable.visit_3));
            }

            if (Utils.isRole(scope, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.vanseller)
                    && setting.deal.visitAllow && setting.deal.allowDealFast) {
                result = result.prepend(new NavigationItem(DEAL_FAST_VISIT, DS.getString(R.string.outlet_extraordinary), R.drawable.store_3));
            }

            result = result.prepend(new NavigationItem(DEAL_FULL_VISIT, DS.getString(R.string.outlet_visit), R.drawable.store_2));
        }

        if (setting.deal.returnAllow &&
                Utils.isRole(scope, roleKeys.expeditor) &&
                !result.contains(DEAL_RETURN, NavigationItem.KEY_ADAPTER)) {
            result = result.prepend(new NavigationItem(DEAL_RETURN, DS.getString(R.string.outlet_return), R.drawable.visit_3));
        }

        if (!result.contains(DEAL_FULL_VISIT, NavigationItem.KEY_ADAPTER) &&
                Utils.isRole(scope, Role.DOCTOR, Role.PHARMACY, Role.PHARMCY_PLUS_DOCTOR)) {
            result = result.prepend(new NavigationItem(DEAL_FULL_VISIT, DS.getString(R.string.outlet_visit), R.drawable.store_2));
        }

        if (withPersonId && !OutletUtil.hasOutletSDeal(scope, argOutlet.outletId)) {
            result = result.filter(new MyPredicate<NavigationItem>() {
                @Override
                public boolean apply(NavigationItem navigationItem) {
                    return SHIPPED != navigationItem.id;
                }
            });
        }
        return ModuleApi.INSTANCE.makeFormOrderNo(argOutlet, ArgModule.PERSON, result);
    }

    private ViewSetup vsRoot;
    private long lastBackClickTime = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.outlet_index);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArgOutlet arg = getArgOutlet();
        final Outlet outlet = arg.getOutlet();
        Mold.setTitle(getActivity(), outlet.name);

        if (!TextUtils.isEmpty(outlet.phone)) {
            addMenu(R.drawable.ic_phone_black_36dp, R.string.outlet_phone, new Command() {
                @Override
                public void apply() {
                    UI.confirm(getActivity(), getString(R.string.outlet_call), outlet.phone, new Command() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void apply() {
                            String phone = outlet.phone;
                            if (phone.length() == 12) {
                                phone = "+" + phone;
                            }
                            Intent i = new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + phone));
                            if (SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.CALL_PHONE)) {
                                getActivity().startActivity(i);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 0);
                            }
                        }
                    });
                }
            });
        }

        addSubMenu(DS.getString(R.string.module_title), new Command() {
            @Override
            public void apply() {
                ModuleSettingFragment.Companion.open(getActivity(), new ArgModule(getArgOutlet(), ArgModule.PERSON));
            }
        });


        reloadForms();

        Scope scope = arg.getScope();
        PersonMemo outletMemos = OutletApi.getOutletMemos(scope, outlet.id);

        TradeRoleKeys roleKeys = scope.ref.getRoleKeys();
        if (outletMemos.memos.nonEmpty() &&
                Utils.isRole(scope, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.merchandiser, roleKeys.supervisor, roleKeys.vanseller)) {
            Memo lastMemo = outletMemos.memos.sort(new Comparator<Memo>() {
                @Override
                public int compare(Memo l, Memo r) {
                    return CharSequenceUtil.compareToIgnoreCase(l.date, r.date);
                }
            }).get(0);

            vsRoot.textView(R.id.tv_memo_title).setText(lastMemo.memo.trim());
            vsRoot.textView(R.id.tv_memo_date).setText(lastMemo.date);

            vsRoot.id(R.id.ll_deal_last_memo).setVisibility(View.VISIBLE);
        } else {
            vsRoot.id(R.id.ll_deal_last_memo).setVisibility(View.GONE);
        }

        if (Utils.isRole(scope, roleKeys.billCollector)) {
            ViewGroup vgHeader = vsRoot.viewGroup(R.id.ll_header_info);
            View line = vsRoot.id(R.id.v_header);

            vgHeader.removeAllViews();

            MyArray<MyArray<Pair<Currency, BigDecimal>>> waitingPostedAbort = OutletApi.makeCashingRequest(scope, outlet.id);

            MyArray<Pair<Currency, BigDecimal>> waitting = waitingPostedAbort.get(0);
            MyArray<Pair<Currency, BigDecimal>> posted = waitingPostedAbort.get(1);
            MyArray<Pair<Currency, BigDecimal>> abort = waitingPostedAbort.get(2);

            if (waitting.nonEmpty() || posted.nonEmpty() || abort.nonEmpty()) {
                vgHeader.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
            }
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
    }

    private void reloadForms() {
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_outlet_info_row);
        vg.removeAllViews();
        MyArray<NavigationItem> formItems = ModuleApi.INSTANCE.makeFormVisible(getArgOutlet(), ArgModule.PERSON, getFormItems(getArgOutlet(), true));
        for (final NavigationItem item : formItems) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.z_card_view_row);
            vs.imageView(R.id.iv_card_view_icon).setImageDrawable(
                    UI.changeDrawableColor(getActivity(), item.icon, R.color.colorAccent));
            vs.textView(R.id.tv_card_view_title).setText(item.title);
            vs.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showForm(item);
                }
            });
            vg.addView(vs.view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
        reloadForms();

        jobMate.listenKey(TapeSyncJob.key(getArgOutlet().accountId), new SyncListener());
    }

    @Override
    public void reloadContent() {

        MyArray<OutletDealInfo> infos = OutletUtil.getOutletDealInfos(this);

        ViewGroup vgInfo = vsRoot.viewGroup(R.id.ll_deal_info_row);
        View line = vsRoot.id(R.id.v_deal_info);
        vgInfo.removeAllViews();

        if (infos.nonEmpty()) {
            vgInfo.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

            for (final OutletDealInfo item : infos) {
                final ViewSetup vs = new ViewSetup(getActivity(), R.layout.outlet_deal_info_row);
                vgInfo.addView(vs.view);

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


                vs.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO v is Nullable

                        int infoType = item.getInfoType();
                        switch (infoType) {

                            case OutletDealInfo.DEAL:
                                OutletDeal deal = (OutletDeal) item;
                                Deal d = deal.holder.deal;
                                ArgOutlet arg = getArgOutlet();
                                Outlet outlet = arg.getOutlet();
                                ArgDeal argDeal = new ArgDeal(getArgOutlet(), d.roomId, d.dealLocalId, "", "", d.dealType);

                                if (outlet.isDoctor() || outlet.isPharm()) {
                                    Intent intent = DealIndexFragment.newInstance(getActivity(), argDeal);
                                    startActivityForResult(intent, DEAL_REQUEST);
                                } else {
                                    DealIndexFragment.open(argDeal);
                                }
                                break;

                            case OutletDealInfo.DEBTOR:
                                OutletDebtor debtor = (OutletDebtor) item;
                                if (debtor.isPrepayment()) {
                                    PrepaymentFragment.open(new ArgDebtor(getArgOutlet(), debtor.debtor.localId, debtor.debtor.paymentKind));
                                } else {
                                    DebtorFragment.open(new ArgDebtor(getArgOutlet(), "", debtor.dealId, debtor.debtorDate, debtor.consign));
                                }
                                break;

                            case OutletDealInfo.SDEAL:
                                OutletSDeal sdeal = (OutletSDeal) item;
                                SDealIndexFragment.open(new ArgSDeal(getArgOutlet(), sdeal.holder.entryId, sdeal.holder.deal.dealId));
                                break;
                        }
                    }
                });

                ImageView ivEdit = vs.imageView(R.id.iv_edit);
                if (item.getEntryState().isReady()) {
                    ivEdit.setVisibility(View.VISIBLE);
                    ivEdit.setImageDrawable(OutletDeal.EDIT_DRAWABLE);

                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int infoType = item.getInfoType();
                            switch (infoType) {

                                case OutletDealInfo.DEAL:
                                    OutletDeal deal = (OutletDeal) item;
                                    Deal d = deal.holder.deal;
                                    DealApi.dealMakeEdit(getArgOutlet().getScope(), deal.holder.deal);

                                    ArgOutlet arg = getArgOutlet();
                                    Outlet outlet = arg.getOutlet();

                                    ArgDeal argDeal = new ArgDeal(arg, d.roomId, d.dealLocalId, "", "", d.dealType);

                                    if (outlet.isDoctor() || outlet.isPharm()) {
                                        Intent intent = DealIndexFragment.newInstance(getActivity(), argDeal);
                                        startActivityForResult(intent, DEAL_REQUEST);
                                    } else {
                                        DealIndexFragment.open(argDeal);
                                    }
                                    break;

                                case OutletDealInfo.DEBTOR:
                                    OutletDebtor debtor = (OutletDebtor) item;
                                    DebtorApi.debtorMakeEdit(getArgOutlet().getScope(), debtor.debtor.localId);

                                    if (debtor.isPrepayment()) {
                                        PrepaymentFragment.open(new ArgDebtor(getArgOutlet(),
                                                debtor.debtor.localId, debtor.debtor.paymentKind));
                                    } else {
                                        DebtorFragment.open(new ArgDebtor(getArgOutlet(),
                                                "", debtor.dealId, debtor.debtorDate, debtor.consign));
                                    }
                                    break;

                                case OutletDealInfo.SDEAL:
                                    OutletSDeal sdeal = (OutletSDeal) item;
                                    ShippedApi.dealMakeEdit(getArgOutlet().getScope(), sdeal.holder);
                                    SDealIndexFragment.open(new ArgSDeal(getArgOutlet(),
                                            sdeal.holder.entryId, sdeal.holder.deal.dealId));
                                    break;
                            }
                        }
                    });
                } else {
                    ivEdit.setVisibility(View.GONE);
                }


                vs.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        EntryState entryState = item.getEntryState();
                        if (!(entryState.isSaved() || entryState.isReady())) {
                            return false;
                        }

                        switch (item.getInfoType()) {
                            case OutletDealInfo.DEAL:
                                UI.popup()
                                        .option(R.string.remove, new Command() {
                                            @Override
                                            public void apply() {
                                                final OutletDeal deal = (OutletDeal) item;
                                                ArgOutlet arg = getArgOutlet();
                                                DealApi.dealDelete(arg.getScope(), deal.holder.deal);
                                                reloadContent();
                                            }
                                        }).show(vs.id(R.id.lf_state));
                                break;
                            case OutletDealInfo.SDEAL:
                                UI.popup()
                                        .option(R.string.remove, new Command() {
                                            @Override
                                            public void apply() {
                                                OutletSDeal sdeal = (OutletSDeal) item;
                                                ArgOutlet arg = getArgOutlet();
                                                ShippedApi.dealDelete(arg.getScope(), sdeal.holder);
                                                reloadContent();
                                            }
                                        }).show(vs.id(R.id.lf_state));
                                break;
                            case OutletDealInfo.DEBTOR:
                                UI.popup()
                                        .option(R.string.remove, new Command() {
                                            @Override
                                            public void apply() {
                                                OutletDebtor debtor = (OutletDebtor) item;
                                                ArgOutlet arg = getArgOutlet();
                                                DebtorApi.debtorDelete(arg.getScope(), debtor.debtor.localId);
                                                reloadContent();
                                            }
                                        }).show(vs.id(R.id.lf_state));
                                break;
                        }
                        return true;
                    }
                });

            }
        }

        ViewGroup vgTask = vsRoot.viewGroup(R.id.ll_tasks);
        View lineTask = vsRoot.id(R.id.v_tasks);
        vgTask.removeAllViews();

        final ArgOutlet arg = getArgOutlet();
        if (arg.getScope().ref != null) {
            MyArray<PersonTask> personTasks = arg.getScope().ref.getPersonTasks();
            if (personTasks.nonEmpty()) {
                vgTask.setVisibility(View.VISIBLE);
                lineTask.setVisibility(View.VISIBLE);

                MyArray<PersonTask> personTasksFiltered = personTasks.filter(new MyPredicate<PersonTask>() {
                    @Override
                    public boolean apply(PersonTask personTask) {
                        return personTask.state.equalsIgnoreCase("N") &&
                                personTask.personId.equals(arg.outletId);
                    }
                });

                for (final PersonTask task : personTasksFiltered) {
                    ViewSetup vsTask = new ViewSetup(getActivity(), R.layout.outlet_task_row);
                    vsTask.textView(R.id.tv_title).setText(task.title);
                    TextView tvDate = vsTask.textView(R.id.tv_detail);
                    tvDate.setText(task.beginDate + " - " + task.endDate);
                    if (!TextUtils.isEmpty(task.endDate)) {
                        long endDate = DateUtil.parse(task.endDate).getTime();
                        long today = new Date().getTime();
                        if (endDate - today < 0) {
                            tvDate.setTextColor(DS.getColor(R.color.red));
                            tvDate.setText(DS.getString(R.string.out_of_date, tvDate.getText()));
                        }
                    }

                    vsTask.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // getting argument
                            ArgOutlet arg = getArgOutlet();
                            ArgFilial argFilial = new ArgFilial(arg.accountId, arg.filialId);
                            ArgTask argTask = new ArgTask(argFilial, "");
                            ArgTaskInfo argTaskInfo = new ArgTaskInfo(argTask, task.taskId);


                            // open task information fragment
                            TaskInfoFragment.open(argTaskInfo);
                        }
                    });
                    vgTask.addView(vsTask.view);
                }
            }

        }

    }

    public void showForm(NavigationItem form) {
        MoldContentFragment f = make(form.id);
        if (f != null) {
            Mold.addContent(getActivity(), f, form);
        }
    }

    public MoldContentFragment make(int id) {
        ArgOutlet argOutlet = getArgOutlet();
        switch (id) {
            case SV_VISIT:
                openNewDeal(Deal.DEAL_SUPERVISOR);
                return null;
            case DEAL_HISTORY:
                DealHistoryFragment.Companion.open(getActivity(), new ArgHistory(argOutlet, argOutlet.outletId));
                return null;
            case INFO:
                return OutletInfoFragment.newInstance(getArgOutlet());
            case REPORT:
                return ReportOutletFragment.newInstance(getArgOutlet());
            case SHIPPED:
                return ShippedFragment.newInstance(getArgOutlet());
            case DEBTOR:
                return OutletDebtorFragment.newInstance(getArgOutlet());
            case DISPLAY_BARCODE:
                DisplayFragment.open(getArgOutlet());
                return null;
            case DEAL_FULL_VISIT:
                openNewDeal(Deal.DEAL_ORDER);
                return null;
            case DEAL_FAST_VISIT:
                openNewDeal(Deal.DEAL_EXTRAORDINARY);
                return null;
            case DEAL_RETURN:
                ArgOutlet arg = getArgOutlet();
                Scope scope = arg.getScope();
                TradeRoleKeys roleKeys = scope.ref.getRoleKeys();

                if (Utils.isRole(scope, roleKeys.expeditor) &&
                        !Utils.isRole(scope, roleKeys.agent, roleKeys.agentMerchandiser) &&
                        scope.ref.getFilialAgents().isEmpty()) {
                    UI.alertError(getActivity(), DS.getString(R.string.deal_attach_agent_is_null_or_empty));
                    return null;
                }

                if (Utils.isRole(scope, roleKeys.agentMerchandiser, roleKeys.agent) &&
                        !Utils.isRole(scope, roleKeys.expeditor) &&
                        scope.ref.getFilialExpeditors().isEmpty()) {
                    UI.alertError(getActivity(), DS.getString(R.string.deal_attach_expeditor_is_null_or_empty));
                    return null;
                }

                openNewDeal(Deal.DEAL_RETURN);
                return null;
            case CATEGORIZATION:
                CategorizationFragment.open(getArgOutlet());
                return null;
            case PERSON_FILES:
                PersonFileFragment.open(getActivity(), getArgOutlet());
                return null;
            case SYNC:
                SyncFragment.open(getActivity(), getArgOutlet());
                return null;
            case EXIT:
                getActivity().finish();
                return null;
            default:
                return null;
        }
    }

    @Override
    public boolean onBackPressed() {
        if ((System.currentTimeMillis() - lastBackClickTime) <= 2000) { // 2 second
            getActivity().finish();
            return true;
        }

        lastBackClickTime = System.currentTimeMillis();
        Mold.makeSnackBar(getActivity(), DS.getString(R.string.double_exit)).show();
        return true;
    }

    //----------------------------------------------------------------------------------------------

    private void openNewDeal(final String dealType) {
        ScopeUtil.execute(getArgOutlet(), new OnScopeReadyCallback<Boolean>() {
            @Override
            public Boolean onScopeReady(Scope scope) {
                boolean isRooted = RootUtil.isDeviceRooted(getActivity(), scope);
                if (isRooted) return false;
                if (Deal.DEAL_ORDER.equals(dealType) || Deal.DEAL_EXTRAORDINARY.equals(dealType)) {
                    boolean isEqual = DealUtil.isEqualDealStartDate(scope);
                    if (!isEqual) {
                        UI.dialog()
                                .title(R.string.warning)
                                .message(DS.getString(R.string.error_server_and_device_time_not_equal))
                                .positive(R.string.admin_sync, new Command() {
                                    @Override
                                    public void apply() {
                                        SyncDialog.show(getActivity(), new ArgSync(getArgOutlet(), true));
                                    }
                                })
                                .negative(R.string.cancel, Util.NOOP)
                                .show(getActivity());

                        return false;
                    }
                    return true;
                }
                return true;
            }

            @Override
            public void onDone(Boolean notRoot) {
                if (notRoot) showRoomDialog(dealType);
            }
        });
    }

    public void showRoomDialog(final String type) {
        final ArgOutlet arg = getArgOutlet();
        Scope scope = arg.getScope();
        final String dateNumber = DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER);
        final MyArray<OutletPlan> outletVisits = DSUtil.getOutletVisits(scope);

        final MyArray<String> roomIds = outletVisits.filter(new MyPredicate<OutletPlan>() {
            @Override
            public boolean apply(OutletPlan outletPlan) {
                return outletPlan.outletId.equals(arg.outletId) && outletPlan.date.equals(dateNumber);
            }
        }).map(new MyMapper<OutletPlan, String>() {
            @Override
            public String apply(OutletPlan outletPlan) {
                return outletPlan.roomId;
            }
        });

        final boolean emptyRoomId = roomIds.contains(new MyPredicate<String>() {
            @Override
            public boolean apply(String roomId) {
                return TextUtils.isEmpty(roomId);
            }
        });

        final boolean dealOrder = Deal.DEAL_ORDER.equals(type);

        MyArray<Room> outletRooms = OutletApi.getOutletRooms(scope, arg.outletId);

        final MyArray<Room> rooms = outletRooms.filter(new MyPredicate<Room>() {
            @Override
            public boolean apply(Room room) {
                return !dealOrder || emptyRoomId || roomIds.isEmpty() || roomIds.contains(room.id, MyMapper.<String>identity());
            }
        });

        //TODO fix refactoring
        if (rooms.nonEmpty()) {
            outletRooms = rooms;
        }

        UIUtils.showRoomDialog(getActivity(), outletRooms, new MyCommand<Room>() {
            @Override
            public void apply(Room val) {
                showLocationDialog(val, type);
            }
        });
    }

    public void showLocationDialog(Room room, String type) {
        if (Deal.DEAL_ORDER.equals(type)) {
            ArgOutlet arg = getArgOutlet();
            Setting setting = arg.getSetting();
            if (setting.location.takeRequired) {
                setFragmentData(Uzum.toParcellable(room, Room.UZUM_ADAPTER));
                MyTakeLocationDialog.show(getActivity(), getArgOutlet(), true);
            } else {
                openDeal(room.id, "", "", "", Deal.DEAL_ORDER);
            }
        } else {
            openDeal(room.id, "", "", "", type);
        }
    }

    @Override
    public void onLocationToken(Location location) {
        UzumParcellable data = getFragmentData();
        if (data != null) {
            String latLng = "";
            if (location != null) {
                latLng = "" + location.getLatitude() + "," + location.getLongitude();
            }
            Room room = Uzum.toValue(data, Room.UZUM_ADAPTER);
            openDeal(room.id, "", latLng, location != null ? "" + ((int) location.getAccuracy()) : "", Deal.DEAL_ORDER);
        }
    }

    public void openDeal(final String roomId, final String dealLocalId,
                         final String location, final String accuracy, String type) {
        final Setter<String> finalType = new Setter<>();
        finalType.value = type;
        if (Deal.DEAL_ORDER.equals(type)) {
            Outlet outlet = getArgOutlet().getOutlet();
            if (outlet != null) {
                if (outlet.isPharm()) {
                    finalType.value = Deal.DEAL_PHARMCY;
                } else if (outlet.isDoctor()) {
                    finalType.value = Deal.DEAL_DOCTOR;
                }
            }
        }
        ScopeUtil.execute(getArgOutlet(), new OnScopeReadyCallback<Boolean>() {
            @Override
            public Boolean onScopeReady(Scope scope) {
                return !RootUtil.isDeviceRooted(getActivity(), scope);
            }

            @Override
            public void onDone(Boolean notRoot) {
                if (notRoot) {
                    ArgOutlet arg = getArgOutlet();
                    Outlet outlet = arg.getOutlet();
                    ArgDeal argDeal = new ArgDeal(arg, roomId, dealLocalId, location, accuracy, finalType.value);

                    if (outlet.isDoctor() || outlet.isPharm()) {
                        Intent intent = DealIndexFragment.newInstance(getActivity(), argDeal);
                        startActivityForResult(intent, DEAL_REQUEST);
                    } else {
                        DealIndexFragment.open(argDeal);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(), true);

        if (requestCode == LocationUtil.REQUEST_CHECK_SETTINGS) {
            Fragment fragment = getFragmentManager()
                    .findFragmentByTag(MyTakeLocationDialog.TAKE_LOCATION_DIALOG);
            if (fragment != null && fragment instanceof MyTakeLocationDialog) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == DEAL_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String roomId = data.getStringExtra(DEAL_REQUEST_ROOM);
                showDateVisitFollowing(roomId);
            }
        }
    }


    private void showDateVisitFollowing(final String roomId) {
        DatePickerDialog dialog = MyDatePickerDialog.show(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth, 0, 0);
                String selectedDate = DateUtil.FORMAT_AS_NUMBER.get().format(c.getTime());
                savePharmNextVisit(roomId, selectedDate);
            }
        });
        dialog.setTitle(getString(R.string.deal_next_visit));
        dialog.show();
    }

    private void savePharmNextVisit(String roomId, String selectedDate) {
        ArgOutlet arg = getArgOutlet();
        final Outlet outlet = arg.getOutlet();
        if (outlet == null) {
            UI.alertError(getActivity(), DS.getString(R.string.outlet_not_found));
            return;
        }
        try {
            ArgVisitPlan argVisitPlan = new ArgVisitPlan(arg, roomId, selectedDate);
            VVisit vVisit = BuilderVisit.make(argVisitPlan);
            VOutlet vOutlet = vVisit.outlets.getItems().findFirst(new MyPredicate<VOutlet>() {
                @Override
                public boolean apply(VOutlet vOutlet) {
                    return vOutlet.outlet.id.equals(outlet.id);
                }
            });

            vOutlet.check.setValue(true);
            MyArray<OutletPlan> outletPlans = vVisit.convert(arg.getScope());
            VPApi.save(arg.getScope(), outletPlans);

            Mold.makeSnackBar(getActivity(), DS.getString(R.string.deal_saved_next_visit_plan,
                    DateUtil.convert(selectedDate, DateUtil.FORMAT_AS_DATE))).show();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            UI.alertError(getActivity(), e);
        }
    }


    private class SyncListener implements LongJobListener<ProgressValue> {

        @Override
        public void onStart() {

        }

        @Override
        public void onStop(Throwable error) {
            reloadContent();
            reloadForms();
        }

        @Override
        public void onProgress(ProgressValue progress) {
        }
    }
}
