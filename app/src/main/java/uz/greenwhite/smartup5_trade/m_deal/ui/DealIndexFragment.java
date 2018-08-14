package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.error.UserError;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.location.LocationHelper;
import uz.greenwhite.lib.location.LocationResult;
import uz.greenwhite.lib.mold.DummyMoldTuningFragment;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.BottomSheet;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealApi;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.ui.agree.AgreeFragment;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.total.VDealTotalModule;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;
import uz.greenwhite.smartup5_trade.m_take_location.TakeLocationUtil;

public class DealIndexFragment extends DealModuleFragment<VDealForm> {

    public static void open(ArgDeal arg) {
        Mold.openContent(DealIndexFragment.class, Mold.parcelableArgument(arg, ArgDeal.UZUM_ADAPTER));
    }

    public static Intent newInstance(Activity activity, ArgDeal arg) {
        return Mold.newContent(activity, DealIndexFragment.class,
                Mold.parcelableArgument(arg, ArgDeal.UZUM_ADAPTER));
    }

    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private LocationHelper locationHelper;
    private Location location;
    private TradeRoleKeys tradeRoleKeys;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.deal_title);
        final ArgDeal argDeal = getArgDeal();
        tradeRoleKeys = argDeal.getScope().ref.getRoleKeys();
        final Setter<DealData> data = new Setter<>();
        data.value = Mold.getData(getActivity());
        if (data.value != null) {
            data.value.vDeal.checkModuleForms();
            setListItems(data.value.vDeal.modules.getItems());
            makeHeader();
            makeFooter();
        }

        if (data.value == null) {
            ScopeUtil.executeWithDialog(getActivity(), jobMate, argDeal, new OnScopeReadyCallback<DealData>() {
                @Override
                public DealData onScopeReady(Scope scope) {
                    if (data.value == null) {
                        DealHolder dealHolder = argDeal.getDealHolder(scope);
                        if (dealHolder == null) {
                            throw new UserError(DS.getString(R.string.deal_is_removed));
                        }
                        DealData d = new DealData(scope, dealHolder);
                        d.vDeal.readyToChange();
                        d.vDeal.start();
                        d.formCode = d.vDeal.getFirstModuleFormCode();
                        data.value = d;

                        if (DealIndexFragment.this.location != null && TextUtils.isEmpty(d.vDeal.header.locLatLng)) {
                            Location location = DealIndexFragment.this.location;
                            d.vDeal.header.locLatLng = "" + location.getLatitude() + "," + location.getLongitude();
                        }
                    }
                    return data.value;
                }

                @Override
                public void onDone(DealData dealData) {
                    Mold.setData(getActivity(), dealData);
                    data.value.vDeal.checkModuleForms();
                    setListItems(dealData.vDeal.modules.getItems());
                    makeHeader();
                    makeFooter();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startLocationListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        DealData data = Mold.getData(getActivity());
        if (data != null) data.vDeal.start();
    }

    private void makeHeader() {
        final DealData data = Mold.getData(getActivity());
        if (data == null) {
            return;
        }

        vsRoot.viewGroup(R.id.ll_header_content).removeAllViews();

        if (data.vDeal.dealRef.allViolations.nonEmpty()) {
            vsRoot.id(R.id.cv_header).setVisibility(View.VISIBLE);
            ViewSetup vsHeader = new ViewSetup(getActivity(), R.layout.deal_header);
            vsHeader.textView(R.id.tv_title).setText(DS.getString(R.string.deal_you_have_more_violation, String.valueOf(data.vDeal.dealRef.allViolations.size())));
            vsHeader.textView(R.id.tv_detail).setText(data.vDeal.dealRef.allViolations.map(new MyMapper<Violation, String>() {
                @Override
                public String apply(Violation violation) {
                    return violation.name;
                }
            }).mkString(", "));

            vsHeader.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Mold.addContent(getActivity(), ViolationFragment.newInstance(getArgDeal()));
                }
            });

            vsRoot.viewGroup(R.id.ll_header_content).addView(vsHeader.view);
        }
    }

    private void makeFooter() {
        final DealData data = Mold.getData(getActivity());
        if (data == null) {
            return;
        }
        ViewSetup vsFooter = setFooter(R.layout.z_deal_module_footer);

        Button cSave = vsFooter.id(R.id.save);
        Button cMakeEditable = vsFooter.id(R.id.make_editable);
        Button cComplete = vsFooter.id(R.id.complete);
        Button cCompleteAs = vsFooter.id(R.id.complete_as);
        Button cCompleteDraft = vsFooter.id(R.id.btn_complete_draft);

        cSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDealTry(false, Deal.STATE_NEW, data.vDeal.dealRef.dealHolder.deal.dealState);
            }
        });
        cMakeEditable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable();
            }
        });
        cComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AdminApi.alwaysShowDealTotal(getArgDeal().accountId)) {
                    //First show TotalFragment and then send orders (if enabled in settings!)
                    VDealTotalModule module = (VDealTotalModule) data.vDeal.modules.getItems().findFirst(new MyPredicate<VDealModule>() {
                        @Override
                        public boolean apply(VDealModule vDealModule) {
                            return VisitModule.M_TOTAL == vDealModule.getModuleId();
                        }
                    });

                    if (data.vDeal.modules.modified()) {
                        if (module != null) {
                            if ((!module.form.overloads.isEmpty() && module.form.overloads.get(0).hasValue())
                                    || (!module.form.orders.isEmpty() && module.form.orders.get(0).hasValue())) {
                                onListItemClick(module.form);
                            } else {
                                UI.dialog()
                                        .title(R.string.warning)
                                        .message(R.string.deal_prepare_visit)
                                        .negative(R.string.no, new Command() {
                                            @Override
                                            public void apply() {
                                                getActivity().finish();
                                            }
                                        })
                                        .positive(R.string.yes, new Command() {
                                            @Override
                                            public void apply() {
                                                saveDealReady(Deal.STATE_NEW, data.vDeal.dealRef.dealHolder.deal.dealState);
                                            }
                                        }).show(getActivity());
                            }
                        } else {
                            getActivity().finish();
                        }
                    } else {
                        getActivity().finish();
                    }
                } else {
                    saveDealReady(Deal.STATE_NEW, data.vDeal.dealRef.dealHolder.deal.dealState);
                }
            }
        });

        cCompleteAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheet.Builder dialog = UI.bottomSheet().title(R.string.select);

                if (data.vDeal.dealRef.setting.deal.allowDealDraft) {
                    dialog.option(R.string.deal_state_draft, new Command() {
                        @Override
                        public void apply() {
                            saveDealReady(Deal.STATE_DRAFT, Deal.DEAL_STATE_DRAFT);
                        }
                    });
                }

                dialog.option(R.string.deal_state_new, new Command() {
                    @Override
                    public void apply() {
                        saveDealReady(Deal.STATE_NEW, Deal.DEAL_STATE_NEW);
                    }
                }).option(R.string.deal_state_shipped, new Command() {
                    @Override
                    public void apply() {
                        saveDealReady(Deal.STATE_NEW, Deal.DEAL_STATE_SHIPPED);
                    }
                });

                if (data.vDeal.dealRef.setting.deal.allowDealArchive) {
                    dialog.option(R.string.deal_state_archived, new Command() {
                        @Override
                        public void apply() {
                            saveDealReady(Deal.STATE_NEW, Deal.DEAL_STATE_ARCHIVED);
                        }
                    });
                }

                dialog.show(getActivity());
            }
        });
        cCompleteDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDealReady(Deal.STATE_DRAFT, Deal.DEAL_STATE_NONE);
            }
        });

        ArgDeal arg = getArgDeal();

        switch (data.vDeal.dealRef.dealHolder.entryState.state) {
            case EntryState.NOT_SAVED:
            case EntryState.SAVED:
                if (data.vDeal.dealRef.isVanseller &&
                        (Deal.DEAL_ORDER.equals(arg.type) || Deal.DEAL_EXTRAORDINARY.equals(arg.type) ||
                                Deal.DEAL_DOCTOR.equals(arg.type) || Deal.DEAL_PHARMCY.equals(arg.type))) {

                    cSave.setVisibility(View.VISIBLE);
                    cCompleteAs.setVisibility(View.VISIBLE);

                } else {
                    cSave.setVisibility(View.VISIBLE);
                    cComplete.setVisibility(View.VISIBLE);

                    if (data.vDeal.dealRef.setting.deal.allowDealDraft &&
                            Utils.isRole(arg.getScope(), tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser) &&
                            (Deal.DEAL_ORDER.equals(arg.type) || Deal.DEAL_EXTRAORDINARY.equals(arg.type) ||
                                    Deal.DEAL_DOCTOR.equals(arg.type) || Deal.DEAL_PHARMCY.equals(arg.type))) {

                        VDealOrderModule orderModule = (VDealOrderModule) data.vDeal.modules.getItems()
                                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);

                        if (orderModule != null && orderModule.getModuleForms().nonEmpty()) {
                            cCompleteDraft.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case EntryState.READY:
                cMakeEditable.setVisibility(View.VISIBLE);
                break;
        }

        if (!TextUtils.isEmpty(data.vDeal.dealRef.dealHolder.deal.finalDealId)) {
            cComplete.setVisibility(View.VISIBLE);
            cSave.setVisibility(View.GONE);
            cCompleteDraft.setVisibility(View.GONE);
            cMakeEditable.setVisibility(View.GONE);
            cCompleteAs.setVisibility(View.GONE);

        }
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new DummyMoldTuningFragment();
    }

    @Override
    public void onListItemClick(VForm vForm) {
        VDealForm form = (VDealForm) vForm;
        DealData data = Mold.getData(getActivity());
        if (data.vDeal.isValidForm(form)) {
            MoldContentFragment content = make(form);
            if (content != null) {
                Mold.setSubtitle(getActivity(), form.getSubtitle());
                Mold.addContent(getActivity(), content, form.getTitle());

                data.formCode = form.code;
            }
        } else {
            UI.alertError(getActivity(), DS.getString(R.string.deal_mandatory_complete));
        }
    }

    @Override
    public void onAboveContentPopped(Object result) {
        super.onAboveContentPopped(result);
        DealData data = Mold.getData(getActivity());
        int state = data.vDeal.dealRef.dealHolder.entryState.state;

        if (state != EntryState.READY && (boolean) result) {
            saveDealReady(Deal.STATE_NEW, Deal.DEAL_STATE_NEW);
        }
    }

    public MoldContentFragment make(VDealForm form) {
        String formCode = form.code;
        switch (form.getFormId()) {

            case VisitModule.M_ERROR:
                return DealUtil.newInstance(DealErrorFragment.class, formCode);

            case VisitModule.M_ORDER:
                return DealUtil.newInstance(getArgDeal(), OrderFragment.class, formCode);

            case VisitModule.M_PAYMENT:
                return DealUtil.newInstance(PaymentFragment.class, formCode);

            case VisitModule.M_PHOTO:
                return DealUtil.newInstance(getArgDeal(), PhotoFragment.class, formCode);

            case VisitModule.M_INFO:
                return DealUtil.newInstance(getArgDeal(), DealOutletInfoFragment.class, formCode);

            case VisitModule.M_STOCK:
                return DealUtil.newInstance(StockFragment.class, formCode);

            case VisitModule.M_RETURN:
                return DealUtil.newInstance(ReturnFragment.class, formCode);

            case VisitModule.M_RETURN_PAYMENT:
                return DealUtil.newInstance(ReturnPaymentFragment.class, formCode);

            case VisitModule.M_ATTACH:
                return DealUtil.newInstance(AttachFragment.class, formCode);

            case VisitModule.M_GIFT:
                return DealUtil.newInstance(getArgDeal(), GiftFragment.class, formCode);

            case VisitModule.M_ACTION:
                return DealUtil.newInstance(ActionFragment.class, formCode);

            case VisitModule.M_MEMO:
                return DealUtil.newInstance(MemoFragment.class, formCode);

            case VisitModule.M_NOTE:
                return DealUtil.newInstance(NoteFragment.class, formCode);

            case VisitModule.M_QUIZ:
                return DealUtil.newInstance(QuizFragment.class, formCode);

            case VisitModule.M_SERVICE:
                return DealUtil.newInstance(ServiceFragment.class, formCode);

            case VisitModule.M_COMMENT:
                return DealUtil.newInstance(CommentFragment.class, formCode);

            case VisitModule.M_RETAIL_AUDIT:
                return DealUtil.newInstance(getArgDeal(), RetailAuditFragment.class, formCode);

            case VisitModule.M_AGREE:
                return DealUtil.newInstance(getArgDeal(), AgreeFragment.class, formCode);

            case VisitModule.M_TOTAL:
                return DealUtil.newInstance(TotalFragment.class, formCode);

            case VisitModule.M_OVERLOAD:
                return DealUtil.newInstance(getArgDeal(), OverloadFragment.class, formCode);

            default:
                throw AppError.Unsupported();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.jobMate.stopListening();
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationListener() {
        final FragmentActivity activity = getActivity();
        DealData data = Mold.getData(activity);
        if (data != null && !TextUtils.isEmpty(data.vDeal.header.locLatLng)) {
            stopLocationListener(activity);
            return;
        }
        ArgDeal arg = getArgDeal();
        if (TextUtils.isEmpty(arg.dealId) && Deal.DEAL_ORDER.equals(arg.type)
                && Deal.DEAL_DOCTOR.equals(arg.type) && Deal.DEAL_PHARMCY.equals(arg.type) &&
                (TextUtils.isEmpty(arg.location) || Integer.parseInt(arg.accuracy) > 100) && locationHelper == null) {
            locationHelper = LocationHelper.getOneLocation(getActivity(), new LocationResult() {

                ArrayList<Location> locations = new ArrayList<>();

                @Override
                public void onLocationChanged(Location findLocation) {
                    try {
                        boolean accuracyIncorrect = true;
                        if (findLocation != null && activity != null) {

                            locations.add(findLocation);
                            findLocation = TakeLocationUtil.getCorrectLocation(locations);
                            accuracyIncorrect = findLocation.getAccuracy() > 100;
                            if (accuracyIncorrect) {
                                if (locationHelper != null) {
                                    locationHelper.startListener();
                                }
                            }

                            DealData data = Mold.getData(activity);
                            if (data != null) {
                                data.vDeal.header.locLatLng = "" + findLocation.getLatitude() + "," + findLocation.getLongitude();
                            } else {
                                DealIndexFragment.this.location = findLocation;
                            }

                        }
                        if (!accuracyIncorrect) {
                            stopLocationListener(activity);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) e.printStackTrace();
                    }
                }
            }, (5 * 60) * 1000); //5 minute
        }
        if (locationHelper != null && activity != null && SysUtil.checkSelfPermissionGranted(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationHelper.startListener();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void stopLocationListener(FragmentActivity activity) {
        try {
            if (locationHelper != null && activity != null &&
                    SysUtil.checkSelfPermissionGranted(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationHelper.stopListener();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        DealData data = Mold.getData(getActivity());
        if (data != null && data.vDeal.modified() && data.hasEdit()) {
            MoldContentFragment content = Mold.getContentFragment(getActivity());
            if (content instanceof PhotoInfoFragment) {
                return false;
            }
            UI.dialog()
                    .title(R.string.warning)
                    .message(R.string.exit_dont_save)
                    .negative(R.string.no, Util.NOOP)
                    .positive(R.string.yes, new Command() {
                        @Override
                        public void apply() {
                            getActivity().finish();
                        }
                    }).show(getActivity());
            return true;
        }
        return false;
    }


    //--------------------------------------------------------------------------------------------------

    private void makeEditable() {
        ArgDeal arg = getArgDeal();
        DealData data = Mold.getData(getActivity());
        Deal deal = data.vDeal.dealRef.dealHolder.deal;
        DealApi.dealMakeEdit(arg.getScope(), deal);
        DealIndexFragment.open(arg);
        getActivity().finish();
    }

    private void saveDealReady(final String dealNew, final String dealState) {
        try {
            DealData data = Mold.getData(getActivity());
            data.vDeal.prepareToReady();
            if (Deal.STATE_NEW.equals(dealNew)) {
                ErrorResult error = data.vDeal.getError();
                if (error.isError()) {
                    throw new UserError(error.getErrorMessage());
                }
            }
            UI.confirm(getActivity(), getString(R.string.save), getString(R.string.deal_prepare_visit), new Command() {
                @Override
                public void apply() {
                    saveDealTry(true, dealNew, dealState);
                }
            });
        } catch (Exception ex) {
            ErrorUtil.saveThrowable(ex);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
        }
    }

    private void saveDealTry(boolean ready, String dealNew, String dealState) {
        try {
            saveDeal(ready, dealNew, dealState);
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorUtil.saveThrowable(ex);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
        }
    }

    private void saveDeal(boolean ready, String dealNew, String dealState) {
        DealData data = Mold.getData(getActivity());
        VDeal vDeal = data.vDeal;
        Deal deal = vDeal.convertToDeal(dealNew, dealState);
        ArgDeal arg = getArgDeal();
        Scope scope = arg.getScope();

        if (ready && !DealUtil.checkDealEndDate(scope, deal)) {
            return;
        }

        if (!TextUtils.isEmpty(deal.finalDealId) && JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {
            jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener(true));
            UI.alertError(getActivity(), DS.getString(R.string.deal_save_online_deal_edit_error_1));
            return;
        }

        DealApi.saveDeal(scope, deal, ready);
        vDeal.makePhotoSaveAndRemove(scope, ready);

        if (!TextUtils.isEmpty(deal.finalDealId)) {
            jobMate.stopListening();
            jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener(false));

            if (!JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {
                jobMate.execute(new TapeSyncJob(AdminApi.getAccount(arg.accountId)));
            }

        } else {
            Outlet outlet = arg.getOutlet();
            if (ready && (outlet.isDoctor() || outlet.isPharm())) {
                Intent intent = new Intent();
                intent.putExtra(OutletIndexFragment.DEAL_REQUEST_ROOM, arg.roomId);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }

            getActivity().finish();
        }
    }

    private class SyncListener implements LongJobListener<ProgressValue> {

        private final boolean defaultListener;
        private final ProgressDialog pd;

        SyncListener(boolean defaultListener) {
            this.defaultListener = defaultListener;

            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
        }

        @Override
        public void onStart() {
            pd.setMessage(DS.getString(R.string.outlet_run_full_sync));
            pd.show();
        }

        @Override
        public void onStop(Throwable error) {
            pd.dismiss();
            if (error != null) {
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(error).message);
            } else if (!defaultListener) {
                getActivity().finish();
            }
        }

        @Override
        public void onProgress(ProgressValue progress) {
        }
    }

}
