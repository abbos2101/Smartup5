package uz.greenwhite.smartup5_trade.m_shipped.builder;// 09.09.2016

import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.m_deal.common.WP;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SOrder;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDBalance;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrder;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderModule;

public class BuilderSOrder {

    public final SDealRef sDealRef;
    public final VisitModule module;
    public final MyArray<SOrder> initialOrders;

    public BuilderSOrder(SDealRef sDealRef) {
        this.sDealRef = sDealRef;
        this.module = new VisitModule(VisitModule.M_ORDER, false);
        this.initialOrders = getInitialOrders();
    }

    private MyArray<SOrder> getInitialOrders() {
        MyArray<SOrder> orders = sDealRef.holder.deal.orders;
        return orders != null ? orders : MyArray.<SOrder>emptyArray();
    }


    private MyArray<Product> getProducts() {
        Set<String> productIds = initialOrders
                .map(new MyMapper<SOrder, String>() {
                    @Override
                    public String apply(SOrder val) {
                        return val.productId;
                    }
                }).asSet();

        MyArray<Product> result = MyArray.from(productIds)
                .map(new MyMapper<String, Product>() {
                    @Override
                    public Product apply(String productId) {
                        return sDealRef.findProduct(productId);
                    }
                });
        return result.filterNotNull();
    }

    private MyArray<WP> getWarehouseAndPrices() {
        Set<WP> r = new HashSet<>();
        for (SOrder o : initialOrders) {
            r.add(new WP(o.warehouseId, o.priceTypeId));
        }
        return MyArray.from(r);
    }

    private ValueSpinner prepareOrderType(SOrder sOrder) {
        MyArray<SpinnerOption> options = MyArray.from(
                /*new SpinnerOption("+", "+"),*/
                new SpinnerOption("-", "-")
        );

        SpinnerOption option = options.get(0);

        // if (sOrder.returnQuant.compareTo(BigDecimal.ZERO) != 0) {
        // option = options.get(1);
        // }
        return new ValueSpinner(options, option);
    }

    private ValueArray<VSDealOrder> makeSDealOrders(final SDBalance sdBalance,
                                                    final Warehouse warehouse,
                                                    final PriceType priceType,
                                                    final MyArray<Product> products) {

        MyArray<VSDealOrder> orders = initialOrders.filter(new MyPredicate<SOrder>() {
            @Override
            public boolean apply(SOrder sOrder) {
                return sOrder.warehouseId.equals(warehouse.id) &&
                        sOrder.priceTypeId.equals(priceType.id);
            }
        }).map(new MyMapper<SOrder, VSDealOrder>() {
            @Override
            public VSDealOrder apply(SOrder sOrder) {
                Product product = products.find(sOrder.productId, Product.KEY_ADAPTER);
                AppError.checkNull(product);
                RoundModel roundModel = sDealRef.holder.deal.roundModel;
                final ValueSpinner spinner = prepareOrderType(sOrder);
                return new VSDealOrder(product, sOrder, roundModel, sdBalance, spinner);
            }
        });

        return new ValueArray<>(orders);
    }

    private ValueArray<VSDealOrderForm> orderForms() {
        final MyArray<Product> products = getProducts();
        MyArray<WP> wps = getWarehouseAndPrices();

        final SDBalance sdBalance = new SDBalance(sDealRef);

        MyArray<VSDealOrderForm> result = wps.map(new MyMapper<WP, VSDealOrderForm>() {
            @Override
            public VSDealOrderForm apply(WP wp) {
                Warehouse warehouse = sDealRef.getWarehouse(wp.warehouseId);
                PriceType priceType = sDealRef.getPriceType(wp.priceTypeId);

                if (warehouse == null || priceType == null) {
                    throw AppError.NullPointer();
                }

                final Currency currency = sDealRef.getCurrency(priceType.currencyId);
                if (currency == null) return null;
                ValueArray<VSDealOrder> orders = makeSDealOrders(sdBalance, warehouse, priceType, products);
                return new VSDealOrderForm(module, warehouse, priceType, currency, orders);
            }
        }).filterNotNull();

        return new ValueArray<>(result);
    }

    public VSDealOrderModule build() {
        return new VSDealOrderModule(module, orderForms());
    }

}
