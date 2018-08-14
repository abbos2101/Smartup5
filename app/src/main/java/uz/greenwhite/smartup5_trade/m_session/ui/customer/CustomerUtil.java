package uz.greenwhite.smartup5_trade.m_session.ui.customer;

import java.util.ArrayList;
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
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.Dashboard;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.CustomerRow;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.PersonCustomerRow;

public class CustomerUtil {

    private static final Object TAG_VOI = new Object();

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

    public static MyArray<String> getVisitedOutletIds(Scope scope) {
        MyArray<String> result = (MyArray<String>) scope.cache.get(Scope.C_CUSTOMER_PERSON_VISIT_IDS);
        if (result == null) {

            synchronized (TAG_VOI) {
                result = (MyArray<String>) scope.cache.get(Scope.C_CUSTOMER_PERSON_VISIT_IDS);

                if (result == null) {
                    String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
                    Dashboard dashboard = scope.ref.getDashboard();
                    Set<String> items = new HashSet<>();
                    for (DOutlet val : dashboard.outlets) {
                        if (today.equals(val.visitDate) && !items.contains(val.outletId)) {
                            items.add(val.outletId);
                        }
                    }
                    result = MyArray.from(items);
                    scope.cache.put(Scope.C_CUSTOMER_PERSON_VISIT_IDS, result);
                }
            }
        }
        return result;

    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<PersonCustomerRow> getPersonCustomerRows(Scope scope, boolean allOutlets) {
        assert scope.ref != null;

        TradeRoleKeys tradeRoleKeys = scope.ref.getRoleKeys();
        MyArray<CustomerRow> result = (MyArray<CustomerRow>) scope.cache.get(Scope.C_CUSTOMER_PERSON_ROW);
        if (result == null) {
            MyArray<PersonBalanceReceivable> balanceReceivables = MyArray.emptyArray();
            Setting setting = scope.ref.getSettingWithDefault();
            if (setting.person.showPersonDebtExists && Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser)) {
                balanceReceivables = scope.ref.getPersonBalanceReceivables();
            }

            MyArray<Outlet> outlets = DSUtil.getFilialOutlets(scope);
            MyArray<PersonLastInfo> lastInfos = scope.ref.getPersonLastInfo();
            MyArray<OutletType> outletTypes = scope.ref.getOutletTypes();

            List<PersonCustomerRow> r = new ArrayList<>();
            for (Outlet outlet : outlets) {
                OutletType outletType = null;
                if (outlet.isDoctor()) {
                    outletType = outletTypes.find(((OutletDoctor) outlet).specialityId, OutletType.KEY_ADAPTER);
                }

                r.add(new PersonCustomerRow(
                        outlet,
                        balanceReceivables.find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER),
                        setting.person.showPersonDebtAmount,

                        lastInfos.find(outlet.id, PersonLastInfo.KEY_ADAPTER),
                        outletType
                ));
            }

            result = MyArray.from(r).toSuper();
            scope.cache.put(Scope.C_CUSTOMER_PERSON_ROW, result);
        }

        Map<String, ArrayList<DealHolder>> outletDeals = getOutletDeals(scope);
        MyArray<String> visitedOutletIds = getVisitedOutletIds(scope);

        MyArray<String> outletIds = visitedOutletIds.union(MyArray.from(outletDeals.keySet()));

        for (String outletId : outletIds) {
            CustomerRow customer = result.find(outletId, CustomerRow.KEY_ADAPTER);
            if (customer == null) continue;

            ArrayList<DealHolder> holders = outletDeals.get(outletId);
            Boolean visited = Util.nvl(visitedOutletIds.contains(outletId, MyMapper.<String>identity()), false);
            if (holders == null) holders = new ArrayList<>();

            customer.populateState(visited, MyArray.from(holders).map(DealHolder.TO_ENTRY_STATE));
        }

        result = result.sort(new Comparator<CustomerRow>() {
            @Override
            public int compare(CustomerRow l, CustomerRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.outlet.name, r.outlet.name);
            }
        });

        if (!allOutlets) {
            result = result.filter(new MyPredicate<CustomerRow>() {
                @Override
                public boolean apply(CustomerRow outlet) {
                    return outlet.outlet.isOutlet();
                }
            });
        }
        return result.toSuper();
    }

}
