package uz.greenwhite.smartup5_trade.m_deal.builder;// 06.10.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealReturn;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealReturnModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturn;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class BuilderReturn {

    private final DealRef dealRef;
    private final VisitModule module;
    public final MyArray<DealReturn> initial;

    public BuilderReturn(DealRef dealRef) {
        this.dealRef = dealRef;
        this.module = new VisitModule(VisitModule.M_RETURN, false);
        this.initial = getInitialOrders();
    }

    private MyArray<DealReturn> getInitialOrders() {
        DealReturnModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.returns : MyArray.<DealReturn>emptyArray();
    }

    private MyArray<Warehouse> getWarehouses() {
        MyArray<String> warehouseIds = dealRef.room.warehouseIds;
        if (warehouseIds == null) {
            warehouseIds = MyArray.emptyArray();
        }
        Set<String> wIds = warehouseIds.asSet();
        for (DealReturn r : initial) {
            wIds.add(r.warehouseId);
        }
        ArrayList<Warehouse> w = new ArrayList<>();
        for (String id : wIds) {
            Warehouse warehouse = dealRef.getWarehouse(id);
            if (warehouse != null) w.add(warehouse);
        }
        return MyArray.from(w);
    }

    private MyArray<Product> getProducts() {
        MyArray<String> productIds = dealRef.getFilialProductIds();
        if (productIds == null) {
            productIds = MyArray.emptyArray();
        }
        Set<String> pIds = productIds.asSet();
        for (DealReturn r : initial) {
            pIds.add(r.productId);
        }
        ArrayList<Product> p = new ArrayList<>();
        for (String id : pIds) {
            Product product = dealRef.findProduct(id);
            if (product != null) p.add(product);
        }
        return MyArray.from(p);
    }

    private ValueArray<VDealReturn> makeRow(final Warehouse warehouse, MyArray<Product> products) {
        ArrayList<VDealReturn> result = new ArrayList<>();
        for (final Product product : products) {
            MyArray<DealReturn> filter = initial.filter(new MyPredicate<DealReturn>() {
                @Override
                public boolean apply(DealReturn val) {
                    return val.warehouseId.equals(warehouse.id) && val.productId.equals(product.id);
                }
            });

            if (filter.isEmpty()) {
                result.add(new VDealReturn(product, null, null, "", ""));

            } else {
                for (DealReturn find : filter) {
                    BigDecimal quantity = null;
                    BigDecimal price = null;
                    String expiryDate = "";
                    String cardCode = "";

                    if (find != null) {
                        quantity = find.getQuantity();
                        price = find.getPrice();
                        expiryDate = find.expiryDate;
                        cardCode = find.cardCode;
                    }
                    result.add(new VDealReturn(product, quantity, price, expiryDate, cardCode));
                }
            }
        }
        return new ValueArray<>(MyArray.from(result));
    }

    private ValueArray<VDealReturnForm> makeForm() {
        MyArray<Warehouse> warehouses = getWarehouses();
        final MyArray<Product> products = getProducts();

        MyArray<VDealReturnForm> forms = warehouses.map(new MyMapper<Warehouse, VDealReturnForm>() {
            @Override
            public VDealReturnForm apply(Warehouse warehouse) {
                ValueArray<VDealReturn> results = makeRow(warehouse, products);
                return new VDealReturnForm(module, warehouse, results);
            }
        });
        return new ValueArray<>(forms);
    }

    public VDealReturnModule build() {
        return new VDealReturnModule(module, makeForm());
    }
}
