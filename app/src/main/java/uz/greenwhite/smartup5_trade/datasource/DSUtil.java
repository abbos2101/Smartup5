package uz.greenwhite.smartup5_trade.datasource;// 28.10.2016

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.smartup.anor.datasource.EntryValue;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPharm;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonMargin;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;

public class DSUtil {

    public static Outlet getOutlet(Scope scope, String outletId) {
        Outlet outlet = scope.ref.getOutletDoctors().find(outletId, OutletDoctor.KEY_ADAPTER);

        if (outlet == null) {
            outlet = scope.ref.getOutletPharms().find(outletId, OutletPharm.KEY_ADAPTER);
        }

        if (outlet == null) {
            outlet = scope.ref.getOutlets().find(outletId, Outlet.KEY_ADAPTER);
        }
        return outlet;
    }

    public static MyArray<OutletPlan> removeOutletPlanDuplicate(MyArray<OutletPlan> plans) {
        final Set<String> keys = new HashSet<>();
        ArrayList<OutletPlan> result = new ArrayList<>();
        for (OutletPlan val : plans) {
            String key = val.getKey();
            if (!keys.contains(key)) {
                result.add(val);
                keys.add(key);
            }
        }
        return MyArray.from(result);
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletPlan> getOutletVisits(Scope scope) {
        AppError.checkNull(scope.ref, scope.entry);
        MyArray<OutletPlan> result = removeOutletPlanDuplicate(scope.entry.getOutletVisits());
        MyArray<OutletPlan> refResult = removeOutletPlanDuplicate(scope.ref.getOutletVisits());

        final Set<String> keys = result.map(OutletPlan.KEY_ADAPTER).asSet();

        MyArray<OutletPlan> refs = refResult.filter(new MyPredicate<OutletPlan>() {
            @Override
            public boolean apply(OutletPlan plan) {
                return !keys.contains(plan.getKey()) && !keys.contains(plan.getDeletedKey());
            }
        });

        return refs.union(result, OutletPlan.KEY_ADAPTER);
    }

    @SuppressWarnings("ConstantConditions")
    public static OutletVisitPlan getOutletVisitPlan(Scope scope, String filialId, String
            roomId, String outletId) {
        AppError.checkNull(scope.ref, scope.entry);
        OutletVisitPlan entryResult = scope.entry.getOutletVisitPlan(filialId, roomId, outletId);
        if (entryResult == null) {
            return scope.ref.getOutletVisitPlan(roomId, outletId);
        }
        return entryResult;
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<DealHolder> getAllDeals(Scope scope) {
        AppError.checkNull(scope.entry);
        MyArray<DealHolder> deals = scope.entry.getExtraordinaryDeals();
        deals = deals.append(scope.entry.getOrderDeals());
        deals = deals.append(scope.entry.getReturnDeals());
        return deals;
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<DealHolder> getReturnDeals(Scope scope) {
        AppError.checkNull(scope.entry);
        return scope.entry.getReturnDeals();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Role> getFilialRoles(final Scope scope) {
        AppError.checkNull(scope.ref);
        Filial filial = scope.ref.getFilial(scope.filialId);
        if (filial == null) return MyArray.emptyArray();
        return filial.roleIds.map(new MyMapper<String, Role>() {
            @Override
            public Role apply(String roleId) {
                return scope.ref.getRole(roleId);
            }
        }).filterNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Room> getOutletRooms(Scope scope, final String outletId) {
        AppError.checkNull(scope.ref);
        Filial filial = scope.ref.getFilial(scope.filialId);
        MyArray<RoomOutletIds> result = scope.ref.getRoomOutletIds();
        ArrayList<Room> r = new ArrayList<>();
        for (RoomOutletIds val : result) {
            if (!filial.roomIds.contains(val.roomId, MyMapper.<String>identity())) continue;
            if (val.outletIds.contains(outletId, MyMapper.<String>identity())) {
                Room room = scope.ref.getRoom(val.roomId);
                if (room != null) r.add(room);
            }
        }
        Collections.sort(r, new Comparator<Room>() {
            @Override
            public int compare(Room l, Room r) {
                return CharSequenceUtil.compareToIgnoreCase(l.name, r.name);
            }
        });
        return MyArray.from(r);
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Outlet> getRoomOutlets(final Scope scope, String roomId) {
        AppError.checkNull(scope.ref);
        RoomOutletIds roIds = scope.ref.getRoomOutletIds(roomId);
        if (roIds == null || roIds.outletIds.isEmpty()) {
            return MyArray.emptyArray();
        }
        MyArray<Outlet> outlets = scope.ref.getOutlets();
        MyArray<OutletDoctor> outletDoctors = scope.ref.getOutletDoctors();
        MyArray<OutletPharm> outletPharms = scope.ref.getOutletPharms();
        Set<String> ids = new HashSet<>();
        ArrayList<Outlet> result = new ArrayList<>();
        for (String outletId : roIds.outletIds) {
            if (ids.contains(outletId)) continue;
            ids.add(outletId);
            Outlet find = outletPharms.find(outletId, OutletPharm.KEY_ADAPTER);
            if (find != null) {
                result.add(find);
            } else {
                find = outletDoctors.find(outletId, OutletDoctor.KEY_ADAPTER);
                if (find != null) {
                    result.add(find);
                } else {
                    find = outlets.find(outletId, Outlet.KEY_ADAPTER);
                    if (find != null) result.add(find);
                }
            }
        }
        return MyArray.from(result);
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Margin> getOutletMargin(final Scope scope, String roomId, String outletId) {
        AppError.checkNull(scope.ref);
        MyMapper<String, Margin> MAPPER = new MyMapper<String, Margin>() {
            @Override
            public Margin apply(String marginId) {
                return scope.ref.getMargin(marginId);
            }
        };
        PersonMargin personMargin = scope.ref.getPersonMargins()
                .find(outletId, PersonMargin.KEY_ADAPTER);

        Room room = scope.ref.getRoom(roomId);
        if (personMargin == null || personMargin.marginIds.isEmpty()) {
            return room.marginIds.map(MAPPER).filterNotNull();
        }

        if (room.marginIds.isEmpty()) {
            return personMargin.marginIds.map(MAPPER).filterNotNull();
        }

        return Utils.intersect(room.marginIds, personMargin.marginIds).map(MAPPER).filterNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<SDeal> getOutletSDeals(final Scope scope, final String outletId) {
        AppError.checkNull(scope.ref);
        return scope.ref.getSDeals().filter(new MyPredicate<SDeal>() {
            @Override
            public boolean apply(SDeal sDeal) {
                return sDeal.outletId.equals(outletId);
            }
        });
    }

    public static MyArray<DebtorDeal> getOutletDebtor(final Scope scope, String outletId) {
        AppError.checkNull(scope.ref);
        DebtorOutlet debtor = scope.ref.getDebtorOutlet(outletId);
        return debtor != null ? debtor.deals : MyArray.<DebtorDeal>emptyArray();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletLocation> getOutletLocations(Scope scope) {
        return scope.entry.getOutletLocations().map(new MyMapper<EntryValue<OutletLocation>, OutletLocation>() {
            @Override
            public OutletLocation apply(EntryValue<OutletLocation> val) {
                return val.value;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static OutletLocation getOutletLocation(Scope scope, String outletId) {
        OutletLocation find = getOutletLocations(scope).find(outletId, OutletLocation.KEY_ADAPTER);
        if (find == null) {
            Outlet outlet = DSUtil.getOutlet(scope, outletId);
            return new OutletLocation(outlet.id, outlet.latLng);
        }
        return find;
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Outlet> getFilialOutlets(Scope scope) {
        Filial filial = scope.ref.getFilial(scope.filialId);
        if (filial == null) return MyArray.emptyArray();
        MyArray<Outlet> result = MyArray.emptyArray();
        for (String roomId : filial.roomIds) {
            MyArray<Outlet> r = getRoomOutlets(scope, roomId);
            result = result.union(r, Outlet.KEY_ADAPTER);

        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<Room> getFilialRooms(final Scope scope) {
        Filial filial = scope.ref.getFilial(scope.filialId);
        if (filial == null) {
            return MyArray.emptyArray();
        }
        return filial.roomIds.map(new MyMapper<String, Room>() {
            @Override
            public Room apply(String roomId) {
                return scope.ref.getRoom(roomId);
            }
        }).filterNotNull();
    }
}
