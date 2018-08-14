package uz.greenwhite.smartup5_trade.m_outlet;// 29.06.2016

import android.text.TextUtils;
import android.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.datasource.EntryValue;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.bean.file.PersonFile;
import uz.greenwhite.smartup5_trade.m_outlet.bean.file.PersonFileDetail;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDeal;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDebtor;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletSDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.CashingRequest;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.person.PersonLastDebt;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class OutletApi {

    private static MyArray<Pair<Currency, BigDecimal>> makeCashRequestCurrencyAmount(
            Scope scope,
            MyArray<CashingRequest> cashingRequests) {
        final MyArray<Currency> currencys = scope.ref.getCurrencys();
        Map<String, BigDecimal> result = new HashMap<>();
        for (CashingRequest item : cashingRequests) {
            BigDecimal amount = Util.nvl(result.get(item.currencyId), BigDecimal.ZERO);
            result.put(item.currencyId, amount.add(item.amount));
        }
        return MyArray.from(result.entrySet()).map(new MyMapper<Map.Entry<String, BigDecimal>, Pair<Currency, BigDecimal>>() {
            @Override
            public Pair<Currency, BigDecimal> apply(Map.Entry<String, BigDecimal> item) {
                Currency currency = currencys.find(item.getKey(), Currency.KEY_ADAPTER);
                return currency == null ? null : new Pair<>(currency, item.getValue());
            }
        }).filterNotNull();
    }

    public static MyArray<MyArray<Pair<Currency, BigDecimal>>> makeCashingRequest(Scope scope, final String outletId) {
        MyArray<CashingRequest> cashingRequests = scope.ref.getCashingRequests()
                .filter(new MyPredicate<CashingRequest>() {
                    @Override
                    public boolean apply(CashingRequest cashingRequest) {
                        return outletId.equals(cashingRequest.outletId);
                    }
                });

        MyArray<Pair<Currency, BigDecimal>> waiting = makeCashRequestCurrencyAmount(scope,
                cashingRequests.filter(new MyPredicate<CashingRequest>() {
                    @Override
                    public boolean apply(CashingRequest cashingRequest) {
                        return CashingRequest.K_WAITING.equals(cashingRequest.state);
                    }
                }));

        MyArray<Pair<Currency, BigDecimal>> posted = makeCashRequestCurrencyAmount(scope,
                cashingRequests.filter(new MyPredicate<CashingRequest>() {
                    @Override
                    public boolean apply(CashingRequest cashingRequest) {
                        return CashingRequest.K_POSTED.equals(cashingRequest.state);
                    }
                }));

        MyArray<Pair<Currency, BigDecimal>> abort = makeCashRequestCurrencyAmount(scope,
                cashingRequests.filter(new MyPredicate<CashingRequest>() {
                    @Override
                    public boolean apply(CashingRequest cashingRequest) {
                        return CashingRequest.K_ABORT.equals(cashingRequest.state);
                    }
                }));

        return MyArray.from(waiting, posted, abort);
    }

    //----------------------------------------------------------------------------------------------

    public static PersonMemo getOutletMemos(Scope scope, String outletId) {
        PersonMemo memo = scope.ref.getOutletMemo(outletId);
        if (memo == null) {
            return PersonMemo.makeDefault(outletId);
        }
        return memo;
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<OutletDebtor> getOutletDebtor(Scope scope, String outletId) {
        MyArray<DebtorDeal> items = DSUtil.getOutletDebtor(scope, outletId);
        MyArray<DebtorHolder> entries = scope.entry.getDebtorHolder();
        return OutletUtil.prepareOutletDebtor(scope, outletId, items, entries);
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletSDeal> getOutletSDeals(Scope scope, String outletId) {
        MyArray<SDeal> sDeals = DSUtil.getOutletSDeals(scope, outletId);
        MyArray<SDealHolder> entrySDeal = scope.entry.getSDealHolders();
        MyArray<SDealHolder> holders = OutletUtil.prepareSDealHolders(sDeals, entrySDeal);
        return OutletUtil.prepareOutletSDeal(scope, holders, outletId);
    }

    public static MyArray<OutletDeal> getOutletDeals(Scope scope, String outletId) {
        return OutletUtil.prepareOutletDeal(scope, DSUtil.getAllDeals(scope), outletId);
    }

    public static MyArray<OutletDeal> getOutletReturn(Scope scope, String outletId) {
        return OutletUtil.prepareOutletDeal(scope, DSUtil.getReturnDeals(scope), outletId);
    }

    public static MyArray<Room> getOutletRooms(Scope scope, String outletId) {
        return DSUtil.getOutletRooms(scope, outletId);
    }

    @SuppressWarnings("ConstantConditions")
    public static OutletLocation getOutletLocation(Scope scope, String outletId) {
        return DSUtil.getOutletLocation(scope, outletId);
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<PersonFileDetail> getPersonFileDetails(ArgOutlet arg) {
        Scope scope = arg.getScope();
        PersonFile personFiles = scope.ref.getPersonFiles(arg.outletId);
        if (personFiles == null) {
            return MyArray.emptyArray();
        }
        return personFiles.fileDetails;
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<Violation> getPersonViolations(Scope scope, final Outlet outlet, final String roomId) {
        if (outlet.isDoctor() || outlet.isPharm()) return MyArray.emptyArray();

        final Set<String> personGroupIds = outlet.groupValues.map(new MyMapper<OutletGroupValue, String>() {
            @Override
            public String apply(OutletGroupValue outletGroupValue) {
                return outletGroupValue.groupId;
            }
        }).asSet();

        final Set<String> personTypeIds = outlet.groupValues.map(new MyMapper<OutletGroupValue, String>() {
            @Override
            public String apply(OutletGroupValue outletGroupValue) {
                return outletGroupValue.typeId;
            }
        }).asSet();

        final MyArray<PersonLastDebt> personLastDebts = scope.ref.getPersonLastDebts()
                .filter(new MyPredicate<PersonLastDebt>() {
                    @Override
                    public boolean apply(PersonLastDebt val) {
                        return val.personId.equals(outlet.id);
                    }
                });

        return scope.ref.getViolations().filter(new MyPredicate<Violation>() {
            @Override
            public boolean apply(final Violation violation) {
                boolean contains = violation.roomIds.isEmpty() || violation.roomIds.contains(roomId, MyMapper.<String>identity());
                if (contains && !TextUtils.isEmpty(violation.groupId) && personGroupIds.contains(violation.groupId)) {
                    contains = violation.groupTypeIds.isEmpty() || personTypeIds.containsAll(violation.groupTypeIds.asSet());
                }
                if (!contains)return false;

                if (Violation.K_NONE.equals(violation.kind)) {
                    return true;
                } else if (personLastDebts.isEmpty()) {
                    return false;
                }

                if (personLastDebts.nonEmpty()) {
                    if (Violation.K_PAYMENT_TYPE.equals(violation.kind)) {
                        contains = violation.sourceIds.contains(new MyPredicate<String>() {
                            @Override
                            public boolean apply(final String paymentTypeId) {
                                PersonLastDebt find = personLastDebts.findFirst(new MyPredicate<PersonLastDebt>() {
                                    @Override
                                    public boolean apply(PersonLastDebt val) {
                                        return val.paymentTypeId.equals(paymentTypeId);
                                    }
                                });

                                return find != null && Util.nvl(find.amount, BigDecimal.ZERO).intValue() >= violation.value.intValue();
                            }
                        });
                    } else if (Violation.K_CONSIGNMENT_DATE.equals(violation.kind)) {
                        PersonLastDebt personLastDebt = personLastDebts.get(0);

                        Integer expireDate = Integer.parseInt(DateUtil.convert(personLastDebt.expireDate, DateUtil.FORMAT_AS_NUMBER));
                        Integer today = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
                        return today - expireDate >= violation.value.intValue();
                    }
                }
                return contains;
            }
        });

    }

    public static MyArray<Ban> prepareViolationBans(MyArray<Violation> violations) {
        Ban minPrepaymentType = null;
        Map<String, Ban> minPriceType = new HashMap<>();

        Map<String, ArrayList<Ban>> banWithSource = new HashMap<>();
        ArrayList<Ban> result = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        for (Violation v : violations) {
            for (Ban b : v.bans) {
                if (Ban.K_PREPAYMENT.equals(b.kind)) {
                    minPrepaymentType = minPrepaymentType == null ? b : b.kindValue.intValue() < minPrepaymentType.kindValue.intValue() ? b : minPrepaymentType;
                } else if (Ban.K_PRICE_TYPE.equals(b.kind)) {
//                    Ban ban = minPriceType.get(b.kindValue.toPlainString());
//                    ban = ban == null ? b : b.kindValue.intValue() < ban.kindValue.intValue() ? b : ban;
//                    minPriceType.put(b.kindValue.toPlainString())
                    result.add(b);
                } else if (b.kindSourceIds.nonEmpty()) {
                    ArrayList<Ban> bans = banWithSource.get(b.kind);
                    if (bans == null) {
                        bans = new ArrayList<>();
                        banWithSource.put(b.kind, bans);
                    }
                    bans.add(b);
                } else if (!keys.contains(b.kind)) {
                    result.add(b);
                    keys.add(b.kind);
                }
            }
        }

        for (ArrayList<Ban> bans : banWithSource.values()) {
            Set<String> source = new HashSet<>();
            for (Ban b : bans) {
                source.addAll(b.kindSourceIds.asSet());
            }
            Ban ban = bans.get(0);
            result.add(new Ban(ban.banId, ban.kind, BigDecimal.ZERO, MyArray.from(source)));
        }

        return MyArray.from(result);
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    public static void saveOutletLocation(Scope scope, final OutletLocation val) {
        EntryValue<OutletLocation> find = scope.entry.getOutletLocations()
                .findFirst(new MyPredicate<EntryValue<OutletLocation>>() {
                    @Override
                    public boolean apply(EntryValue<OutletLocation> location) {
                        return location.value.outletId.equals(val.outletId);
                    }
                });
        String entryId;

        if (find != null) {
            entryId = find.entryId;
            scope.db.tryMakeStateSaved(entryId);
        } else {
            entryId = String.valueOf(AdminApi.nextSequence());
        }
        scope.entry.saveOutletLocation(entryId, val);
    }
}
