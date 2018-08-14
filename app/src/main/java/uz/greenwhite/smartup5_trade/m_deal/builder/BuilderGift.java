package uz.greenwhite.smartup5_trade.m_deal.builder;// 25.10.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGift;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGift;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;

class BuilderGift {

    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<DealGift> initial;

    private Set<String> hasViolationProductIds = new HashSet<>();

    BuilderGift(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
        makeViolation();
    }

    private void makeViolation() {
        for (Ban ban : dealRef.violationBans) {
            if (Ban.K_PRODUCT.equals(ban.kind)) {
                hasViolationProductIds.addAll(ban.kindSourceIds.asSet());
            }
        }
    }

    private MyArray<DealGift> getInitial() {
        DealGiftModule giftModule = dealRef.findDealModule(module.id);
        return giftModule != null ? giftModule.gifts : MyArray.<DealGift>emptyArray();
    }

    private MyArray<Warehouse> getWarehouses() {
        Set<String> warehouseIds = dealRef.room.warehouseIds.asSet();
        for (DealGift val : initial) {
            warehouseIds.add(val.warehouseId);
        }
        return MyArray.from(warehouseIds)
                .map(new MyMapper<String, Warehouse>() {
                    @Override
                    public Warehouse apply(String warehouseId) {
                        return dealRef.getWarehouse(warehouseId);
                    }
                }).filterNotNull();
    }

    private MyArray<Product> getProducts() {
        Set<String> result = dealRef.getGiftProductIds().asSet();
        for (DealGift val : initial) {
            result.add(val.productId);
        }

        ArrayList<Product> products = new ArrayList<>();
        for (String productId : result) {
            Product product = dealRef.findProduct(productId);
            if (product != null) products.add(product);
        }
        return MyArray.from(products);
    }

    private ValueArray<VDealGift> makeGifts(Warehouse warehouse, MyArray<Product> products) {
        ArrayList<VDealGift> result = new ArrayList<>();
        for (Product product : products) {
            if (hasViolationProductIds.contains(product.id)) {
                continue;
            }

            WarehouseProductStock balance = dealRef.balance.getBalance(warehouse.id, product.id);

            if (balance != null && balance.hasBalance(Card.ANY)) {
                Tuple2 key = DealGift.getKey(product.id, warehouse.id);
                DealGift find = initial.find(key, DealGift.KEY_ADAPTER);
                String formKey = "" + module.id + ":" + warehouse.id;
                BigDecimal quant = null;
                String productUnitId = "";
                if (find != null) {
                    productUnitId = find.productUnitId;
                    balance.bookQuantity(Card.ANY, formKey, (quant = find.quantity));
                }

                result.add(new VDealGift(product, productUnitId, balance, formKey, quant));
            }
        }

        Collections.sort(result, new Comparator<VDealGift>() {
            @Override
            public int compare(VDealGift l, VDealGift r) {
                int compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                }
                return compare;
            }
        });

        return new ValueArray<>(MyArray.from(result));
    }

    private ValueArray<VDealGiftForm> makeForms() {
        MyArray<Warehouse> warehouses = getWarehouses();
        MyArray<Product> products = getProducts();

        ArrayList<VDealGiftForm> result = new ArrayList<>();
        for (Warehouse warehouse : warehouses) {
            ValueArray<VDealGift> gifts = makeGifts(warehouse, products);
            if (gifts.getItems().nonEmpty()) {
                result.add(new VDealGiftForm(module, warehouse, gifts));
            }
        }
        return new ValueArray<>(MyArray.from(result));
    }

    public VDealGiftModule build() {
        return new VDealGiftModule(module, makeForms());
    }
}
