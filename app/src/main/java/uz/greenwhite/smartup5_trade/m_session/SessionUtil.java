package uz.greenwhite.smartup5_trade.m_session;// 27.06.2016

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.row.MovementRow;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.DoctorHospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.Dashboard;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.CashingRequest;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.row.Customer;
import uz.greenwhite.smartup5_trade.m_session.row.DebtorRow;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;
import uz.greenwhite.smartup5_trade.m_session.row.WarehouseRow;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class SessionUtil {

    private static Map<String, ArrayList<DealHolder>> getOutletDeals(Scope scope) {
        MyArray<DealHolder> deals = DSUtil.getAllDeals(scope);
        Map<String, ArrayList<DealHolder>> d = new HashMap<>();
        for (DealHolder holder : deals) {
            ArrayList<DealHolder> result = d.get(holder.deal.outletId);
            if (result == null) {
                result = new ArrayList<>();
                d.put(holder.deal.outletId, result);
            }
            result.add(holder);
        }
        return d;
    }

    private static Map<String, Boolean> getOutletVisit(Date date, Scope scope) {
        String today = DateUtil.format(date, DateUtil.FORMAT_AS_DATE);
        Dashboard dashboard = scope.ref.getDashboard();

        Map<String, Boolean> r = new HashMap<>();
        for (DOutlet val : dashboard.outlets) {
            if (today.equals(val.visitDate) && r.get(val.outletId) == null) {
                r.put(val.outletId, true);
            }
        }
        return r;
    }

    public static MyArray<WarehouseRow> getWarehouseRows(final Scope scope) {
        Filial filial = scope.ref.getFilial(scope.filialId);
        final MyArray<IncomingHolder> allIncoming = scope.entry.getAllIncoming();

        return filial.warehouseIds.map(new MyMapper<String, WarehouseRow>() {
            @Override
            public WarehouseRow apply(final String warehouseId) {
                Warehouse warehouse = scope.ref.getWarehouse(warehouseId);
                if (warehouse == null) {
                    return null;
                }

                MyArray<EntryState> entryStates = allIncoming.filter(new MyPredicate<IncomingHolder>() {
                    @Override
                    public boolean apply(IncomingHolder holder) {
                        return warehouseId.equals(holder.incoming.warehouseId);
                    }
                }).map(new MyMapper<IncomingHolder, EntryState>() {
                    @Override
                    public EntryState apply(IncomingHolder holder) {
                        return holder.state;
                    }
                });

                return new WarehouseRow(warehouse, entryStates);
            }
        }).filterNotNull();
    }

    public static MyArray<MovementRow> getMovementRows(final Scope scope) {
        final MyArray<MovementIncomingHolder> allMovementIncoming = scope.entry.getAllMovementIncoming();
        allMovementIncoming.checkUniqueness(MovementIncomingHolder.KEY_ADAPTER);
        return scope.ref.getMovementIncomings().map(new MyMapper<MovementIncoming, MovementRow>() {
            @Override
            public MovementRow apply(MovementIncoming val) {
                MovementIncomingHolder holder = allMovementIncoming.find(val.movementId, MovementIncomingHolder.KEY_ADAPTER);
                return new MovementRow(val, holder == null ? EntryState.NOT_SAVED_ENTRY : holder.state);
            }
        });
    }


    public static MyArray<OutletRow> getOutletRow(boolean allOutlets, Scope scope) {
        assert scope.ref != null;
        TradeRoleKeys tradeRoleKeys = scope.ref.getRoleKeys();
        MyArray<OutletRow> result = (MyArray<OutletRow>) scope.cache.get(Scope.C_OUTLET_ROW);
        if (result == null) {
            MyArray<PersonBalanceReceivable> balanceReceivables = MyArray.emptyArray();
            Setting setting = scope.ref.getSettingWithDefault();
            if (setting.person.showPersonDebtExists && Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser, tradeRoleKeys.vanseller)) {
                balanceReceivables = scope.ref.getPersonBalanceReceivables();
            }


            MyArray<Outlet> outlets = DSUtil.getFilialOutlets(scope);
            MyArray<PersonLastInfo> lastInfos = scope.ref.getPersonLastInfo();
            MyArray<OutletType> outletTypes = scope.ref.getOutletTypes();
            List<OutletRow> r = new ArrayList<>();
            for (Outlet outlet : outlets) {
                OutletType outletType = null;
                if (outlet.isDoctor()) {
                    outletType = outletTypes.find(((OutletDoctor) outlet).specialityId, OutletType.KEY_ADAPTER);
                }
                OutletRow outletRow = new OutletRow(outlet, lastInfos.find(outlet.id, PersonLastInfo.KEY_ADAPTER), outletType);
                outletRow.setBalanceReceivable(balanceReceivables.find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER));
                outletRow.setPersonDebtAmount(setting.person.showPersonDebtAmount);
                r.add(outletRow);
            }

            result = MyArray.from(r);
            scope.cache.put(Scope.C_OUTLET_ROW, result);
        }

        Map<String, ArrayList<DealHolder>> outletDeals = getOutletDeals(scope);
        Map<String, Boolean> outletVisit = getOutletVisit(new Date(), scope);

        MyArray<String> outletIds = MyArray.from(outletVisit.keySet())
                .union(MyArray.from(outletDeals.keySet()));

        for (String outletId : outletIds) {
            OutletRow outletRow = result.find(outletId, OutletRow.KEY_ADAPTER);
            if (outletRow == null) continue;

            ArrayList<DealHolder> holders = outletDeals.get(outletId);
            Boolean visited = Util.nvl(outletVisit.get(outletId), false);
            if (holders == null) holders = new ArrayList<>();

            outletRow.populateState(visited, MyArray.from(holders));
        }

        result = result.sort(new Comparator<OutletRow>() {
            @Override
            public int compare(OutletRow l, OutletRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.outlet.name, r.outlet.name);
            }
        });

        if (!allOutlets) {
            result = result.filter(new MyPredicate<OutletRow>() {
                @Override
                public boolean apply(OutletRow outlet) {
                    return outlet.outlet.isOutlet();
                }
            });
        }
        return result;
    }

    public static MyArray<OutletRow> getOutletDoctorRow(Scope scope) {
        assert scope.ref != null;
        MyArray<OutletRow> result = (MyArray<OutletRow>) scope.cache.get(Scope.C_OUTLET_DOCTOR_ROW);
        if (result == null) {
            MyArray<Outlet> outlets = DSUtil.getFilialOutlets(scope).filter(new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor();
                }
            });
            MyArray<PersonLastInfo> lastInfos = scope.ref.getPersonLastInfo();
            MyArray<OutletType> outletTypes = scope.ref.getOutletTypes();
            List<OutletRow> r = new ArrayList<>();
            for (Outlet outlet : outlets) {
                OutletType outletType = null;
                if (outlet.isDoctor()) {
                    outletType = outletTypes.find(((OutletDoctor) outlet).specialityId, OutletType.KEY_ADAPTER);
                }
                r.add(new OutletRow(outlet, lastInfos.find(outlet.id, PersonLastInfo.KEY_ADAPTER), outletType));
            }

            result = MyArray.from(r);
            scope.cache.put(Scope.C_OUTLET_DOCTOR_ROW, result);
        }

        Map<String, ArrayList<DealHolder>> outletDeals = getOutletDeals(scope);
        Map<String, Boolean> outletVisit = getOutletVisit(new Date(), scope);

        MyArray<String> outletIds = MyArray.from(outletVisit.keySet())
                .union(MyArray.from(outletDeals.keySet()));

        for (String outletId : outletIds) {
            OutletRow outletRow = result.find(outletId, OutletRow.KEY_ADAPTER);
            if (outletRow == null) continue;

            ArrayList<DealHolder> holders = outletDeals.get(outletId);
            Boolean visited = Util.nvl(outletVisit.get(outletId), false);
            if (holders == null) holders = new ArrayList<>();

            outletRow.populateState(visited, MyArray.from(holders));
        }

        return result.sort(new Comparator<OutletRow>() {
            @Override
            public int compare(OutletRow l, OutletRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.outlet.name, r.outlet.name);
            }
        });
    }

    public static DoctorHospital getHospital(Scope scope, String hospitalId) {
        assert scope.ref != null;
        MyArray<DoctorHospital> doctorHospitals = scope.ref.getDoctorHospitals();
        return doctorHospitals.find(hospitalId, DoctorHospital.KEY_ADAPTER);
    }

    public static MyArray<OutletRow> getOutletPharmRow(Scope scope) {
        assert scope.ref != null;
        MyArray<OutletRow> result = (MyArray<OutletRow>) scope.cache.get(Scope.C_OUTLET_PHARM_ROW);
        if (result == null) {
            MyArray<Outlet> outlets = DSUtil.getFilialOutlets(scope).filter(new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isPharm();
                }
            });
            MyArray<PersonLastInfo> lastInfos = scope.ref.getPersonLastInfo();
            List<OutletRow> r = new ArrayList<>();
            for (Outlet outlet : outlets) {
                r.add(new OutletRow(outlet, lastInfos.find(outlet.id, PersonLastInfo.KEY_ADAPTER), null));
            }

            result = MyArray.from(r);
            scope.cache.put(Scope.C_OUTLET_PHARM_ROW, result);
        }

        Map<String, ArrayList<DealHolder>> outletDeals = getOutletDeals(scope);
        Map<String, Boolean> outletVisit = getOutletVisit(new Date(), scope);

        MyArray<String> outletIds = MyArray.from(outletVisit.keySet())
                .union(MyArray.from(outletDeals.keySet()));

        for (String outletId : outletIds) {
            OutletRow outletRow = result.find(outletId, OutletRow.KEY_ADAPTER);
            if (outletRow == null) continue;

            ArrayList<DealHolder> holders = outletDeals.get(outletId);
            Boolean visited = Util.nvl(outletVisit.get(outletId), false);
            if (holders == null) holders = new ArrayList<>();

            outletRow.populateState(visited, MyArray.from(holders));
        }

        return result.sort(new Comparator<OutletRow>() {
            @Override
            public int compare(OutletRow l, OutletRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.outlet.name, r.outlet.name);
            }
        });
    }

    public static MyArray<Customer> getCustomersShipped(Scope scope) {
        Map<String, ArrayList<SDealHolder>> deals = new HashMap<>();
        for (SDealHolder r : scope.entry.getSDealHolders()) {
            ArrayList<SDealHolder> holders = deals.get(r.deal.outletId);
            if (holders == null) {
                holders = new ArrayList<>();
                deals.put(r.deal.outletId, holders);
            }
            holders.add(r);
        }

        Set<String> outletIds = scope.ref.getSDeals().map(new MyMapper<SDeal, String>() {
            @Override
            public String apply(SDeal sDeal) {
                return sDeal.outletId;
            }
        }).asSet();

        ArrayList<Customer> r = new ArrayList<>();
        for (Outlet outlet : DSUtil.getFilialOutlets(scope)) {
            if (!outletIds.contains(outlet.id)) continue;

            ArrayList<SDealHolder> states = deals.get(outlet.id);
            if (states == null) {
                states = new ArrayList<>();
            }
            r.add(Customer.newCustomerShipped(outlet, false, MyArray.from(states)));
        }
        Collections.sort(r, new Comparator<Customer>() {
            @Override
            public int compare(Customer l, Customer r) {
                return CharSequenceUtil.compareToIgnoreCase(l.param.outletName, r.param.outletName);
            }
        });

        return MyArray.from(r);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public static MyArray<DebtorRow> getDebtorRows(final Scope scope) {
        final String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);

        MyArray<PersonBalanceReceivable> balanceReceivables = MyArray.emptyArray();
        final Setting setting = scope.ref.getSettingWithDefault();
        if (setting.person.showPersonDebtExists) {
            balanceReceivables = scope.ref.getPersonBalanceReceivables();
        }

        final MyArray<DebtorHolder> holders = scope.entry.getDebtorHolder();
        final MyArray<String> completeVisitPersonIds = scope.ref.getCashingRequests().map(new MyMapper<CashingRequest, String>() {
            @Override
            public String apply(CashingRequest cashingRequest) {
                if (!TextUtils.isEmpty(cashingRequest.requestDate) && today.equals(cashingRequest.requestDate)) {
                    return cashingRequest.outletId;
                }
                return null;
            }
        }).filterNotNull();

        final MyArray<PersonBalanceReceivable> finalBalanceReceivables = balanceReceivables;
        final MyArray<DebtorRow> debtors = scope.ref.getDebtorOutlets().map(new MyMapper<DebtorOutlet, DebtorRow>() {
            @Override
            public DebtorRow apply(final DebtorOutlet val) {
                Outlet outlet = DSUtil.getOutlet(scope, val.outletId);
                if (outlet == null) return null;
                MyArray<DebtorHolder> debtors = holders.filter(new MyPredicate<DebtorHolder>() {
                    @Override
                    public boolean apply(DebtorHolder debtorHolder) {
                        return val.outletId.equals(debtorHolder.debtor.outletId);
                    }
                });
                MyArray<String> expireDate = val.deals.map(new MyMapper<DebtorDeal, String>() {
                    @Override
                    public String apply(DebtorDeal debtorDeal) {
                        return debtorDeal.expiryDate;
                    }
                });

                boolean completeVisit = completeVisitPersonIds.contains(val.outletId, MyMapper.<String>identity());
                DebtorRow debtorRow = new DebtorRow(outlet, debtors, expireDate, completeVisit);
                debtorRow.setBalanceReceivable(finalBalanceReceivables.find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER));
                debtorRow.setPersonDebtAmount(setting.person.showPersonDebtAmount);
                return debtorRow;
            }
        }).filterNotNull();

        final Set<String> prepaymentOutletIds = new HashSet<>();
        MyArray<DebtorHolder> prepaymentHolder = holders.filter(new MyPredicate<DebtorHolder>() {
            @Override
            public boolean apply(DebtorHolder debtorHolder) {
                Debtor val = debtorHolder.debtor;
                boolean isPrepayment = val.isPrepayment() &&
                        !debtors.contains(val.outletId, DebtorRow.KEY_ADAPTER);
                if (isPrepayment) {
                    prepaymentOutletIds.add(val.outletId);
                }
                return isPrepayment;
            }
        });

        ArrayList<DebtorRow> prepayments = new ArrayList<>();
        for (final String outletId : prepaymentOutletIds) {
            Outlet outlet = DSUtil.getOutlet(scope, outletId);
            if (outlet == null) continue;
            MyArray<DebtorHolder> outletPrepayment = prepaymentHolder.filter(new MyPredicate<DebtorHolder>() {
                @Override
                public boolean apply(DebtorHolder holder) {
                    return outletId.equals(holder.debtor.outletId);
                }
            });
            MyArray<String> debtorDates = outletPrepayment.map(new MyMapper<DebtorHolder, String>() {
                @Override
                public String apply(DebtorHolder debtorHolder) {
                    return debtorHolder.debtor.debtorDate;
                }
            });
            DebtorRow debtorRow = new DebtorRow(outlet, outletPrepayment, debtorDates, false);
            debtorRow.setBalanceReceivable(finalBalanceReceivables.find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER));
            debtorRow.setPersonDebtAmount(setting.person.showPersonDebtAmount);
            prepayments.add(debtorRow);
        }

        return debtors.append(MyArray.from(prepayments)).sort(new Comparator<DebtorRow>() {
            @Override
            public int compare(DebtorRow l, DebtorRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.outlet.name, r.outlet.name);
            }
        });
    }

}
