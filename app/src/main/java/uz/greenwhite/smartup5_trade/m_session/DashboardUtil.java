package uz.greenwhite.smartup5_trade.m_session;// 07.11.2016

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrderModule;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DAccount;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.Dashboard;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductTypePlan;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.ProductPriceRow;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.ProductRow;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.row.DashboardProductKpiRow;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class DashboardUtil {

    public static String getToday(String date) {
        Date d = DateUtil.parse(date);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            CharSequence week = DateFormat.format("EEEE", d);
            CharSequence month = DateFormat.format("MMMM", d);
            String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            return String.format("%s, %s-%s", week, day, month);
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            e.printStackTrace();
        }
        return DateUtil.format(d, DateUtil.FORMAT_AS_DATE);
    }

    //----------------------------------------------------------------------------------------------
    private static int makeVisitCount(Scope scope, final String visitType, MyArray<DealHolder> holders) {
        final MyArray<OutletPlan> outletVisits = DSUtil.getOutletVisits(scope);
        Dashboard dashboard = scope.ref.getDashboard();

        final String todayNumber = DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER);
        final String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);

        MyArray<DOutlet> filter = dashboard.outlets.filter(new MyPredicate<DOutlet>() {
            @Override
            public boolean apply(final DOutlet val) {
                return visitType.equals(val.type) &&
                        today.equals(val.visitDate) &&
                        (!visitType.equals(Deal.DEAL_ORDER) ||
                                outletVisits.contains(new MyPredicate<OutletPlan>() {
                                    @Override
                                    public boolean apply(OutletPlan outletPlan) {
                                        return outletPlan.outletId.equals(val.outletId) &&
                                                outletPlan.date.equals(todayNumber);
                                    }
                                }));
            }
        });
        final Set<String> outlets = filter.map(new MyMapper<DOutlet, String>() {
            @Override
            public String apply(DOutlet dOutlet) {
                return dOutlet.outletId;
            }
        }).asSet();

        int dealCount = holders.filter(new MyPredicate<DealHolder>() {
            @Override
            public boolean apply(final DealHolder holder) {
                if (outlets.contains(holder.deal.outletId)) return false;
                if (Deal.DEAL_ORDER.equals(visitType) && Deal.DEAL_ORDER.equals(holder.deal.dealType)) {
                    return outletVisits.contains(new MyPredicate<OutletPlan>() {
                        @Override
                        public boolean apply(OutletPlan outletPlan) {
                            return outletPlan.outletId.equals(holder.deal.outletId) &&
                                    outletPlan.date.equals(todayNumber);
                        }
                    });
                }
                return today.equals(DateUtil.convert(holder.deal.header.begunOn, DateUtil.FORMAT_AS_DATE));
            }
        }).map(new MyMapper<DealHolder, String>() {
            @Override
            public String apply(DealHolder dealHolder) {
                return dealHolder.deal.outletId;
            }
        }).asSet().size();

        return outlets.size() + dealCount;
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public static Tuple2 shippedTotalInfo(@NonNull Scope scope) {
        final String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);

        final int outletSize = scope.ref.getSDeals().filter(new MyPredicate<SDeal>() {
            @Override
            public boolean apply(SDeal sDeal) {
                return today.equals(sDeal.deliveryDate);
            }
        }).size();

        int saveSDeal = scope.entry.getSDealHolders().filter(new MyPredicate<SDealHolder>() {
            @Override
            public boolean apply(SDealHolder sDealHolder) {
                return sDealHolder.entryState.isReady() &&
                        sDealHolder.deal.deliveryDate.equals(today);
            }
        }).size();

        return new Tuple2(saveSDeal, outletSize);
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<Outlet> planTotal(Scope scope) {
        MyArray<Outlet> outlets = DSUtil.getFilialOutlets(scope);
        final MyArray<OutletPlan> visits = DSUtil.getOutletVisits(scope);
        if (visits.isEmpty()) {
            return MyArray.emptyArray();
        }
        final String s = DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER);
        return outlets.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(final Outlet outlet) {
                return visits.contains(new MyPredicate<OutletPlan>() {
                    @Override
                    public boolean apply(OutletPlan outletPlan) {
                        return outletPlan.outletId.equals(outlet.id) &&
                                outletPlan.date.equals(s);
                    }
                });
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static int visitTotal(Scope scope) {
        final MyArray<DealHolder> holders = scope.entry.getOrderDeals();
        return makeVisitCount(scope, Deal.DEAL_ORDER, holders);
    }

    @SuppressWarnings("ConstantConditions")
    public static int returnTotal(Scope scope) {
        final MyArray<DealHolder> holders = scope.entry.getReturnDeals();
        return makeVisitCount(scope, Deal.DEAL_RETURN, holders);
    }

    @SuppressWarnings("ConstantConditions")
    public static int extraordinaryTotal(Scope scope) {
        final MyArray<DealHolder> holders = scope.entry.getExtraordinaryDeals();
        return makeVisitCount(scope, Deal.DEAL_EXTRAORDINARY, holders);
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<OutletRow> getCustomers(Scope scope, Dashboard dashboard, final String today) {
        return makeCustomers(scope, dashboard, SessionApi.getOrderDeals(scope).filter(new MyPredicate<DealHolder>() {
            @Override
            public boolean apply(DealHolder holder) {
                return DateUtil.convert(holder.deal.header.begunOn, DateUtil.FORMAT_AS_DATE).equals(today);
            }
        }).map(new MyMapper<DealHolder, String>() {
            @Override
            public String apply(DealHolder d) {
                return d.deal.outletId;
            }
        }), Deal.DEAL_ORDER, today);
    }

    public static MyArray<OutletRow> getDealCustomers(Scope scope, Dashboard dashboard, final String today) {
        return makeCustomers(scope, dashboard, SessionApi.getExtraordinaryDeals(scope)
                .filter(new MyPredicate<DealHolder>() {
                    @Override
                    public boolean apply(DealHolder holder) {
                        return DateUtil.convert(holder.deal.header.begunOn, DateUtil.FORMAT_AS_DATE).equals(today);
                    }
                }).map(new MyMapper<DealHolder, String>() {
                    @Override
                    public String apply(DealHolder dealHolder) {
                        return dealHolder.deal.outletId;
                    }
                }), Deal.DEAL_EXTRAORDINARY, today);
    }

    public static MyArray<OutletRow> getReturnCustomers(Scope scope, Dashboard dashboard, final String today) {
        return makeCustomers(scope, dashboard, SessionApi.getReturnDeals(scope)
                .filter(new MyPredicate<DealHolder>() {
                    @Override
                    public boolean apply(DealHolder holder) {
                        return DateUtil.convert(holder.deal.header.begunOn, DateUtil.FORMAT_AS_DATE).equals(today);
                    }
                }).map(new MyMapper<DealHolder, String>() {
                    @Override
                    public String apply(DealHolder dealHolder) {
                        return dealHolder.deal.outletId;
                    }
                }), Deal.DEAL_RETURN, today);
    }

    @SuppressWarnings("ConstantConditions")
    private static MyArray<OutletRow> makeCustomers(final Scope scope,
                                                    Dashboard dashboard,
                                                    MyArray<String> outletIds,
                                                    final String visitType,
                                                    final String today) {
        Setting setting = scope.ref.getSettingWithDefault();
        final MyArray<OutletType> outletTypes = scope.ref.getOutletTypes();

        final Set<String> oIds = outletIds.asSet();
        MyArray<OutletRow> customers = SessionUtil.getOutletRow(true, scope)
                .filter(new MyPredicate<OutletRow>() {
                    @Override
                    public boolean apply(final OutletRow val) {
                        return oIds.contains(val.outlet.id) && val.lastInfo != null;
                    }
                })
                .sort(new Comparator<OutletRow>() {
                    @Override
                    public int compare(OutletRow l, OutletRow r) {
                        return CharSequenceUtil.compareToIgnoreCase(r.lastInfo.lastVisit, l.lastInfo.lastVisit);
                    }
                });
        MyArray<OutletRow> filter = !setting.common.visitHistoryAllow ? MyArray.<OutletRow>emptyArray() :
                dashboard.outlets.filter(new MyPredicate<DOutlet>() {
                    @Override
                    public boolean apply(DOutlet val) {
                        return val.is(visitType) && today.equals(val.visitDate);
                    }
                }).map(new MyMapper<DOutlet, OutletRow>() {
                    @Override
                    public OutletRow apply(DOutlet val) {
                        Outlet outlet = DSUtil.getOutlet(scope, val.outletId);
                        if (outlet == null) {
                            outlet = new Outlet(val.outletId, val.name, val.address);
                        }
                        OutletType outletType = null;
                        if (outlet.isDoctor()) {
                            outletType = outletTypes.find(((OutletDoctor) outlet).specialityId, OutletType.KEY_ADAPTER);
                        }
                        return new OutletRow(outlet, new PersonLastInfo(val.outletId, "", "", val.visitDate), outletType);
                    }
                });
        return customers.union(filter);
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    public static ProductRow getVisitProduct(Scope scope, Dashboard dashboard, final boolean order) {
        MyArray<DealHolder> holders;

        if (order) {
            holders = scope.entry.getOrderDeals();
        } else {
            holders = scope.entry.getExtraordinaryDeals();
        }

        ArrayList<DealOrder> result = new ArrayList<>();
        for (DealHolder h : holders) {
            DealModule module = h.deal.modules.find(VisitModule.M_ORDER, DealModule.KEY_ADAPTER);
            if (module != null) {
                DealOrderModule orderModule = (DealOrderModule) module;
                MyArray<DealOrder> o = orderModule.orders;
                for (DealOrder r : o) {
                    result.add(r);
                }
            }
        }

        Set<String> sku = new HashSet<>();
        int position = 0;

        Map<String, ProductPriceRow> map = new HashMap<>();
        if (!result.isEmpty()) {

            MyArray<PriceType> priceTypes = scope.ref.getPriceTypes();
            for (DealOrder o : result) {
                position++;
                sku.add(o.productId);

                ProductPriceRow find = map.get(o.priceTypeId);
                if (find == null) {
                    PriceType priceType = priceTypes.find(o.priceTypeId, PriceType.KEY_ADAPTER);
                    find = new ProductPriceRow(priceType != null ? priceType.name : DS.getString(R.string.unknown));
                    map.put(o.priceTypeId, find);
                }
                find.setCount(o.quantity);
                find.setTotalSum(o.quantity.multiply(o.pricePerQuant));
            }
        }

        boolean hasToday = dashboard.outlets.nonEmpty() &&
                dashboard.outlets.get(0).visitDate.equals(DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE));

        MyArray<DAccount> dashboardAccounts = hasToday ? dashboard.accounts.filter(new MyPredicate<DAccount>() {
            @Override
            public boolean apply(DAccount dAccount) {
                return dAccount.visitType.equals(order ? DAccount.TYPE_ORDER : DAccount.TYPE_EXTRAORDINARY);
            }
        }) : MyArray.<DAccount>emptyArray();

        for (DAccount r : dashboardAccounts) {
            ProductPriceRow find = map.get(r.priceTypeId);
            if (find == null) {
                find = new ProductPriceRow(r.priceTypeName);
                map.put(r.priceTypeId, find);
            }
            find.setCount(new BigDecimal(r.productCount));
            find.setTotalSum(new BigDecimal(r.totalPrice));
        }

        ArrayList<ProductPriceRow> priceRows = new ArrayList<>();
        for (String key : map.keySet()) {
            priceRows.add(map.get(key));
        }

        if (priceRows.isEmpty()) {
            return ProductRow.DEFAULT;
        }

        return new ProductRow(String.valueOf(position), String.valueOf(sku.size()), MyArray.from(priceRows));
    }

    public static MyArray<DashboardProductKpiRow> makeDashboardProductTypeKpi(final Scope scope) {
        Map<String, DashboardProductKpiRow> result = new HashMap<>();

        MyArray<DashboardProductTypePlan> dashboardProductTypePlans = scope.ref.getDashboardProductTypePlans();
        MyArray<Room> rooms = scope.ref.getRooms();
        MyArray<ProductType> productTypes = scope.ref.getProductTypes();

        for (DashboardProductTypePlan plan : dashboardProductTypePlans) {
            if (!plan.hasPlan()) continue;
            Room room = rooms.find(plan.roomId, Room.KEY_ADAPTER);
            if (room == null) continue;
            ProductType productType = productTypes.find(plan.productTypeId, ProductType.KEY_ADAPTER);
            if (productType == null) continue;

            String key = DashboardProductKpiRow.getKey(plan.roomId, plan.planType);
            DashboardProductKpiRow find = result.get(key);

            if (find == null) {
                find = new DashboardProductKpiRow(room, plan.planType);
                result.put(key, find);
            }

            find.details.add(new DashboardProductKpiRow.Detail(
                    productType.typeId, productType.name, plan.fact, plan.plan, plan.prediction));
        }

        ArrayList<DashboardProductKpiRow> r = new ArrayList<>();
        r.addAll(result.values());
        return MyArray.from(r);
    }

    public static MyArray<DashboardProductKpiRow> makeDashboardRoomPlan(final Scope scope) {
        Map<String, DashboardProductKpiRow> result = new HashMap<>();

        MyArray<DashboardPlan> roomPlans = scope.ref.getDashboardRoomPlans();
        MyArray<Room> rooms = scope.ref.getRooms();

        for (final DashboardPlan plan : roomPlans) {
            if (!plan.hasPlan()) continue;
            Room room = rooms.find(plan.roomId, Room.KEY_ADAPTER);
            if (room == null) continue;

            DashboardProductKpiRow find = result.get(plan.planType);

            if (find == null) {
                find = new DashboardProductKpiRow(null, plan.planType);
                result.put(plan.planType, find);
            }

            find.details.add(new DashboardProductKpiRow.Detail(
                    room.id, room.name, plan.fact, plan.plan, plan.prediction));
        }

        ArrayList<DashboardProductKpiRow> r = new ArrayList<>();
        r.addAll(result.values());
        return MyArray.from(r);
    }


    public static MyArray<DashboardProductKpiRow> makeDashboardProductPlan(final Scope scope) {
        Map<String, DashboardProductKpiRow> result = new HashMap<>();

        MyArray<DashboardProductPlan> productPlans = scope.ref.getDashboardProductPlans();
        MyArray<Room> rooms = scope.ref.getRooms();
        MyArray<Product> products = scope.ref.getProducts();
        products.checkUniqueness(Product.KEY_ADAPTER);

        for (DashboardProductPlan plan : productPlans) {
            if (!plan.hasPlan()) continue;
            Room room = rooms.find(plan.roomId, Room.KEY_ADAPTER);
            if (room == null) continue;
            Product product = products.find(plan.productId, Product.KEY_ADAPTER);
            if (product == null) continue;

            String key = DashboardProductKpiRow.getKey(plan.roomId, plan.planType);
            DashboardProductKpiRow find = result.get(key);

            if (find == null) {
                find = new DashboardProductKpiRow(room, plan.planType);
                result.put(key, find);
            }
            find.details.add(new DashboardProductKpiRow.Detail(
                    product.id, product.name, plan.fact, plan.plan, plan.prediction));
        }

        ArrayList<DashboardProductKpiRow> r = new ArrayList<>();
        r.addAll(result.values());
        return MyArray.from(r);
    }
}
