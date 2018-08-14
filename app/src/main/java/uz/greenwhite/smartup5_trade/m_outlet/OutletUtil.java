package uz.greenwhite.smartup5_trade.m_outlet;// 01.07.2016

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPaymentModule;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorPayment;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SPayment;
import uz.greenwhite.smartup5_trade.m_outlet.bean.file.PersonFileDetail;
import uz.greenwhite.smartup5_trade.m_outlet.job.DownloadPersonFileJob;
import uz.greenwhite.smartup5_trade.m_outlet.row.OutletInfoRow;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDeal;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDealInfo;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDebtor;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletSDeal;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.CashingRequest;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class OutletUtil {

    private static final String K_DEBTOR_OTHERS = ":others";
    private static final String K_DEBTOR_CONSIGN = ":consign";

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletInfoRow> makeOutletInfo(Scope scope, Outlet outlet) {
        MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
        final MyArray<OutletType> types = scope.ref.getOutletTypes();
        OutletLocation location = DSUtil.getOutletLocation(scope, outlet.id);

        SparseArray<MyArray<String>> outletGroup = new SparseArray<>();
        for (OutletGroupValue g : outlet.groupValues) {
            int key = Integer.parseInt(g.groupId);
            MyArray<String> res = outletGroup.get(key, MyArray.<String>emptyArray());
            if (!res.contains(g.typeId, MyMapper.<String>identity())) {
                outletGroup.put(key, res.append(g.typeId));
            }
        }

        String outletLatLng = location != null ? location.location : outlet.latLng;

        List<OutletInfoRow> r = new ArrayList<>();
        r.add(new OutletInfoRow(DS.getString(R.string.outlet_name), outlet.name, OutletInfoRow.STRING));

        r.add(new OutletInfoRow(DS.getString(R.string.outlet_address), outlet.address, outletLatLng,
                TextUtils.isEmpty(outletLatLng) ? OutletInfoRow.STRING : OutletInfoRow.LOCATION));

        r.add(new OutletInfoRow(DS.getString(R.string.outlet_address_guide), outlet.addressGuide, outletLatLng,
                TextUtils.isEmpty(outlet.address) && !TextUtils.isEmpty(outletLatLng) ? OutletInfoRow.LOCATION : OutletInfoRow.STRING));

        r.add(new OutletInfoRow(DS.getString(R.string.outlet_phone), outlet.phone, OutletInfoRow.PHONE));
        r.add(new OutletInfoRow(DS.getString(R.string.barcode), outlet.barcode, OutletInfoRow.STRING));

        for (int i = 0; i < outletGroup.size(); i++) {
            int key = outletGroup.keyAt(i);
            MyArray<String> result = outletGroup.get(key)
                    .map(new MyMapper<String, String>() {
                        @Override
                        public String apply(String typeId) {
                            OutletType type = types.find(typeId, OutletType.KEY_ADAPTER);
                            if (type == null) return null;
                            return type.name;
                        }
                    }).filterNotNull();
            OutletGroup group = groups.find(String.valueOf(key), OutletGroup.KEY_ADAPTER);
            r.add(new OutletInfoRow(group.name, result.mkString(","), OutletInfoRow.STRING));
        }

        return MyArray.from(r).filter(new MyPredicate<OutletInfoRow>() {
            @Override
            public boolean apply(OutletInfoRow val) {
                return !TextUtils.isEmpty(val.detail);
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<OutletDeal> prepareOutletDeal(final Scope scope, MyArray<DealHolder> deals, final String outletId) {
        return deals.map(new MyMapper<DealHolder, OutletDeal>() {
            @Override
            public OutletDeal apply(DealHolder deal) {
                if (outletId.equals(deal.deal.outletId)) {
                    HashMap<Currency, BigDecimal> result = new HashMap<>();
                    DealModule payment = deal.deal.modules.find(VisitModule.M_PAYMENT, DealModule.KEY_ADAPTER);
                    if (payment != null) {
                        DealPaymentModule module = (DealPaymentModule) payment;
                        for (DealPayment val : module.payments) {
                            Currency currency = scope.ref.getCurrency(val.currencyId);
                            if (currency == null) continue;
                            BigDecimal amount = Util.nvl(result.get(currency), BigDecimal.ZERO);
                            result.put(currency, amount.add(val.value));
                        }
                    }
                    return new OutletDeal(deal, result);
                } else {
                    return null;
                }
            }
        }).filterNotNull();
    }

    public static MyArray<SDealHolder> prepareSDealHolders(MyArray<SDeal> refSDeal, final MyArray<SDealHolder> entrySDeal) {
        return refSDeal.map(new MyMapper<SDeal, SDealHolder>() {
            @Override
            public SDealHolder apply(SDeal sDeal) {
                SDealHolder holder = entrySDeal.find(sDeal.dealId, SDealHolder.KEY_ADAPTER);
                if (holder == null) {
                    return new SDealHolder("", sDeal, EntryState.NOT_SAVED_ENTRY);
                }
                return holder;
            }
        });
    }

    public static MyArray<OutletSDeal> prepareOutletSDeal(final Scope scope, MyArray<SDealHolder> deals, final String outletId) {
        return deals.map(new MyMapper<SDealHolder, OutletSDeal>() {
            @Override
            public OutletSDeal apply(SDealHolder deal) {
                if (outletId.equals(deal.deal.outletId)) {
                    HashMap<Currency, BigDecimal> result = new HashMap<>();
                    for (SPayment payment : deal.deal.payments) {
                        Currency currency = scope.ref.getCurrency(payment.currencyId);
                        if (currency == null) continue;
                        BigDecimal val = Util.nvl(result.get(currency), BigDecimal.ZERO);
                        result.put(currency, val.add(payment.amount));
                    }
                    return new OutletSDeal(deal, result);
                } else {
                    return null;
                }
            }
        }).filterNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletDebtor> prepareOutletDebtor(Scope scope,
                                                            final String outletId,
                                                            MyArray<DebtorDeal> debtors,
                                                            MyArray<DebtorHolder> entries) {
        debtors = debtors.sort(new Comparator<DebtorDeal>() {
            @Override
            public int compare(DebtorDeal l, DebtorDeal r) {
                return CharSequenceUtil.compareToIgnoreCase(r.expiryDate, l.expiryDate);
            }
        });

        MyArray<DebtorHolder> prepayment = entries.filter(new MyPredicate<DebtorHolder>() {
            @Override
            public boolean apply(DebtorHolder debtorHolder) {
                return debtorHolder.debtor.isPrepayment() &&
                        outletId.equals(debtorHolder.debtor.outletId);
            }
        });

        HashMap<String, HashMap<Currency, BigDecimal>> debtorPayments = new HashMap<>();
        for (final DebtorDeal v : debtors) {
            int consignDate = Integer.parseInt(DateUtil.convert(v.expiryDate, DateUtil.FORMAT_AS_NUMBER));
            int today = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
            if (consignDate > today) {
                String key = v.dealId + K_DEBTOR_CONSIGN;
                HashMap<Currency, BigDecimal> result = debtorPayments.get(key);
                if (result == null) {
                    result = new HashMap<>();
                    debtorPayments.put(key, result);
                }

                Currency currency = scope.ref.getCurrency(v.currencyId);
                if (currency == null) continue;
                BigDecimal amount = Util.nvl(result.get(currency), BigDecimal.ZERO);
                result.put(currency, amount.add(v.amount));
            } else {
                String key = v.dealId + K_DEBTOR_OTHERS;
                HashMap<Currency, BigDecimal> result = debtorPayments.get(key);
                if (result == null) {
                    result = new HashMap<>();
                    debtorPayments.put(key, result);
                }

                Currency currency = scope.ref.getCurrency(v.currencyId);
                if (currency == null) continue;
                BigDecimal amount = Util.nvl(result.get(currency), BigDecimal.ZERO);
                result.put(currency, amount.add(v.amount));
            }
        }

        for (final DebtorHolder holder : entries) {
            if (holder.debtor.isPrepayment()) {
                continue;
            }
            String key = holder.debtor.dealId + K_DEBTOR_OTHERS;
            HashMap<Currency, BigDecimal> result = debtorPayments.get(key);
            if (result == null) {
                result = new HashMap<>();
                debtorPayments.put(key, result);
            }

            for (final DebtorPayment p : holder.debtor.payments) {
                Currency currency = scope.ref.getCurrency(p.currencyId);
                if (currency == null) continue;
                BigDecimal amount = Util.nvl(result.get(currency), BigDecimal.ZERO);
                result.put(currency, amount.subtract(p.amount));

                if (p.consignAmount.compareTo(BigDecimal.ZERO) != 0) {
                    result = debtorPayments.get(holder.debtor.dealId + K_DEBTOR_CONSIGN);
                    if (result != null && result.containsKey(currency)) {
                        result.put(currency, result.get(currency).subtract(p.consignAmount));
                    }
                }
            }
        }

        MyArray<CashingRequest> cashingRequests = scope.ref.getCashingRequests().filter(new MyPredicate<CashingRequest>() {
            @Override
            public boolean apply(CashingRequest cashingRequest) {
                return outletId.equals(cashingRequest.outletId);
            }
        });


        ArrayList<OutletDebtor> deal = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        for (final DebtorDeal d : debtors) {
            int consignDate = Integer.parseInt(DateUtil.convert(d.expiryDate, DateUtil.FORMAT_AS_NUMBER));
            int today = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
            if (consignDate > today) {
                String key = d.dealId + K_DEBTOR_CONSIGN;
                if (!keys.contains(key)) {
                    keys.add(key);
                    DebtorHolder found = entries.findFirst(new MyPredicate<DebtorHolder>() {
                        @Override
                        public boolean apply(DebtorHolder holder) {
                            return holder.debtor.dealId.equals(d.dealId) &&
                                    holder.debtor.debtorDate.equals(d.expiryDate);
                        }
                    });
                    EntryState entryState = EntryState.NOT_SAVED_ENTRY;
                    Debtor debtor = null;
                    if (found != null && found.debtor.payments.contains(new MyPredicate<DebtorPayment>() {
                        @Override
                        public boolean apply(DebtorPayment debtorPayment) {
                            return debtorPayment.consignAmount.compareTo(BigDecimal.ZERO) != 0;
                        }
                    })) {
                        entryState = found.entryState;
                        debtor = found.debtor;
                    }
                    Room room = scope.ref.getRoom(d.roomId);
                    MyArray<CashingRequest> filterCashingRequest = cashingRequests.filter(new MyPredicate<CashingRequest>() {
                        @Override
                        public boolean apply(CashingRequest val) {
                            return val.dealId.equals(d.dealId);
                        }
                    });
                    OutletDebtor outletDebtor = new OutletDebtor(d.dealId, d.expiryDate, d.expiryDate, room != null ? room.name : "",
                            debtor, entryState, debtorPayments.get(key), true);

                    outletDebtor.setCashingRequests(filterCashingRequest);

                    deal.add(outletDebtor);
                }
            } else {
                String key = d.dealId + K_DEBTOR_OTHERS;
                if (!keys.contains(key)) {
                    keys.add(key);
                    DebtorHolder found = entries.findFirst(new MyPredicate<DebtorHolder>() {
                        @Override
                        public boolean apply(DebtorHolder holder) {
                            return holder.debtor.dealId.equals(d.dealId);
                        }
                    });
                    EntryState entryState = EntryState.NOT_SAVED_ENTRY;
                    Debtor debtor = null;
                    if (found != null && found.debtor.payments.contains(new MyPredicate<DebtorPayment>() {
                        @Override
                        public boolean apply(DebtorPayment debtorPayment) {
                            return debtorPayment.amount.compareTo(BigDecimal.ZERO) != 0;
                        }
                    })) {
                        entryState = found.entryState;
                        debtor = found.debtor;
                    }
                    Room room = scope.ref.getRoom(d.roomId);

                    MyArray<CashingRequest> filterCashingRequest = cashingRequests.filter(new MyPredicate<CashingRequest>() {
                        @Override
                        public boolean apply(CashingRequest val) {
                            return val.dealId.equals(d.dealId);
                        }
                    });

                    OutletDebtor outletDebtor = new OutletDebtor(d.dealId, d.expiryDate, d.expiryDate, room != null ? room.name : "",
                            debtor, entryState, debtorPayments.get(key), false);
                    outletDebtor.setCashingRequests(filterCashingRequest);
                    deal.add(outletDebtor);
                }
            }
        }

        for (final DebtorHolder val : prepayment) {
            HashMap<Currency, BigDecimal> mHash = new HashMap<>();
            for (DebtorPayment p : val.debtor.payments) {
                Currency currency = scope.ref.getCurrency(p.currencyId);
                if (currency == null) continue;
                BigDecimal amount = Util.nvl(mHash.get(currency), BigDecimal.ZERO);
                mHash.put(currency, amount.add(p.amount));
            }
            deal.add(new OutletDebtor(val.debtor, val.entryState, mHash));
        }

        Collections.sort(deal, new Comparator<OutletDebtor>() {
            @Override
            public int compare(OutletDebtor l, OutletDebtor r) {
                int compare = CharSequenceUtil.compareToIgnoreCase(
                        DateUtil.convert(l.date, DateUtil.FORMAT_AS_NUMBER),
                        DateUtil.convert(r.date, DateUtil.FORMAT_AS_NUMBER));
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.dealId, r.dealId);
                }
                return compare;
            }
        });

        return MyArray.from(deal);
    }

    //----------------------------------------------------------------------------------------------

    public static CharSequence prepareDealPaymentDetail(MyArray<OutletDeal> items) {
        MyArray<HashMap<Currency, BigDecimal>> result = items.map(new MyMapper<OutletDeal, HashMap<Currency, BigDecimal>>() {
            @Override
            public HashMap<Currency, BigDecimal> apply(OutletDeal outletSDeal) {
                return outletSDeal.payments;
            }
        });
        return makeHeaderPaymentDetail(result);
    }

    public static CharSequence prepareSDealPaymentDetail(MyArray<OutletSDeal> items) {
        MyArray<HashMap<Currency, BigDecimal>> result = items.map(new MyMapper<OutletSDeal, HashMap<Currency, BigDecimal>>() {
            @Override
            public HashMap<Currency, BigDecimal> apply(OutletSDeal outletSDeal) {
                return outletSDeal.payments;
            }
        });
        return makeHeaderPaymentDetail(result);
    }

    public static CharSequence prepareDebtorPaymentDetail(MyArray<OutletDebtor> items) {
        MyArray<HashMap<Currency, BigDecimal>> result = items.filter(new MyPredicate<OutletDebtor>() {
            @Override
            public boolean apply(OutletDebtor outletDebtor) {
                return !outletDebtor.isPrepayment();
            }
        }).map(new MyMapper<OutletDebtor, HashMap<Currency, BigDecimal>>() {
            @Override
            public HashMap<Currency, BigDecimal> apply(OutletDebtor outletSDeal) {
                return outletSDeal.payments;
            }
        });
        return makeHeaderPaymentDetail(result);
    }

    private static CharSequence makeHeaderPaymentDetail(MyArray<HashMap<Currency, BigDecimal>> items) {
        Map<Currency, BigDecimal> m = new HashMap<>();
        for (HashMap<Currency, BigDecimal> val : items) {
            for (Map.Entry<Currency, BigDecimal> v : val.entrySet()) {
                BigDecimal amount = Util.nvl(m.get(v.getKey()), BigDecimal.ZERO);
                m.put(v.getKey(), amount.add(v.getValue()));
            }
        }

        ArrayList<String> r = new ArrayList<>();
        for (Map.Entry<Currency, BigDecimal> val : m.entrySet()) {
            r.add(val.getKey().getName() + ": " + NumberUtil.formatMoney(val.getValue()));
        }
        return UI.html().v(MyArray.from(r).mkString("<br/>")).html();
    }

    //----------------------------------------------------------------------------------------------

    public static boolean hasShowPlan(Scope scope) {
        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        for (Role r : roles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleVisibles.contains(
                        RoleSetting.KE_OUTLET_PLAN, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasEditPlan(Scope scope) {
        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        for (Role r : roles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleEditings.contains(
                        RoleSetting.KE_OUTLET_PLAN, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasEditLocation(Scope scope) {
        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        for (Role r : roles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleEditings.contains(
                        RoleSetting.KE_OUTLET_LOCATION, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------

    public static String makePaymentDetail(HashMap<Currency, BigDecimal> payments) {
        ArrayList<String> r = new ArrayList<>();
        for (Map.Entry<Currency, BigDecimal> val : payments.entrySet()) {
            String name = val.getKey().getName();
            String amount = NumberUtil.formatMoney(val.getValue());
            r.add(String.format("<i>%s</i>: %s", name, amount));
        }
        return MyArray.from(r).mkString("<br/>");
    }

    //----------------------------------------------------------------------------------------------

    public static boolean hasPrepayment(ArgSession arg, String paymentKind) {
        Scope scope = arg.getScope();
        return scope.ref.filterByPaymentKind(paymentKind).nonEmpty();
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<OutletDealInfo> getOutletDealInfos(final OutletIndexFragment fragment) {
        final ArgOutlet arg = fragment.getArgOutlet();
        Scope scope = arg.getScope();
        TradeRoleKeys tradeRoleKeys = scope.ref.getRoleKeys();
        final Setter<MyArray<OutletDealInfo>> dealInfoItems = new Setter<>();
        dealInfoItems.value = MyArray.emptyArray();
        if (Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser, tradeRoleKeys.vanseller) ||
                Utils.isRole(scope, tradeRoleKeys.supervisor, Role.DOCTOR,
                        Role.PHARMACY, Role.PHARMCY_PLUS_DOCTOR, tradeRoleKeys.merchandiser)) {
            ScopeUtil.execute(arg, new OnScopeReadyCallback<MyArray<OutletDeal>>() {
                @Override
                public MyArray<OutletDeal> onScopeReady(Scope scope) {
                    return OutletApi.getOutletDeals(scope, arg.outletId);
                }

                @Override
                public void onDone(MyArray<OutletDeal> outletDeals) {
                    dealInfoItems.value = outletDeals.toSuper();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    UI.alertError(fragment.getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                }
            });

        } else if (Utils.isRole(scope, tradeRoleKeys.expeditor)) {
            ScopeUtil.execute(arg, new OnScopeReadyCallback<MyArray<OutletDealInfo>>() {
                @Override
                public MyArray<OutletDealInfo> onScopeReady(Scope scope) {
                    MyArray<OutletSDeal> outletSDeals = OutletApi.getOutletSDeals(scope, arg.outletId);
                    MyArray<OutletDeal> outletReturns = OutletApi.getOutletReturn(scope, arg.outletId);
                    return outletSDeals.<OutletDealInfo>toSuper().append(outletReturns.<OutletDealInfo>toSuper());
                }

                @Override
                public void onDone(MyArray<OutletDealInfo> outletSDeals) {
                    dealInfoItems.value = outletSDeals.toSuper();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    Mold.makeSnackBar(fragment.getActivity(), ErrorUtil.getErrorMessage(throwable).message).show();
                }
            });

        } else if (Utils.isRole(scope, tradeRoleKeys.billCollector)) {
            ScopeUtil.execute(arg, new OnScopeReadyCallback<MyArray<OutletDebtor>>() {
                @Override
                public MyArray<OutletDebtor> onScopeReady(Scope scope) {
                    return OutletApi.getOutletDebtor(scope, arg.outletId);
                }

                @Override
                public void onDone(MyArray<OutletDebtor> items) {
                    dealInfoItems.value = items.toSuper();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    UI.alertError(fragment.getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                }
            });

        }
        return MyArray.nvl(dealInfoItems.value);
    }

    public static boolean fileExistsInDownloadFolder(String accountId, String filename) {
        File parent = new File(DS.getServerPath(accountId) + "/" + DownloadPersonFileJob.PERSON_FILE);
        if (!parent.exists()) {
            parent.mkdirs();
            return false;
        }
        return new File(parent, filename).exists();
    }


    public static String downloadFileFromServer(String accountId, String filename, byte[] bytes) throws Exception {
        File file = new File(DS.getServerPath(accountId) + "/" + DownloadPersonFileJob.PERSON_FILE, filename);
        if (!fileExistsInDownloadFolder(accountId, filename)) {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes, 0, bytes.length);
            out.close();
        }
        return file.getPath();
    }

    public static void openFile(Activity activity, ArgSession arg, PersonFileDetail item) {
        try {
            File file = new File(DS.getServerPath(arg.accountId) + "/" +
                    DownloadPersonFileJob.PERSON_FILE + "/" + item.fileName);
            Uri uriForFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uriForFile = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                uriForFile = Uri.fromFile(file);
            }

            Intent openFile = new Intent(Intent.ACTION_VIEW);
            openFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openFile.setDataAndType(uriForFile, item.contentType);
            activity.startActivity(openFile);
        } catch (ActivityNotFoundException ex) {
            UI.alert(activity, DS.getString(R.string.warning), uz.greenwhite.smartup5_trade.ErrorUtil.getErrorMessage(ex).message.toString()
                    + "\n" + DS.getString(R.string.f_m_file_downloaded_to_error, item.fileName));
        }
    }

    //----------------------------------------------------------------------------------------------

    public static boolean hasOutletSDeal(Scope scope, final String outletId) {
        return scope.ref.getSDeals().contains(new MyPredicate<SDeal>() {
            @Override
            public boolean apply(SDeal sDeal) {
                return sDeal.outletId.equals(outletId);
            }
        });
    }

}
