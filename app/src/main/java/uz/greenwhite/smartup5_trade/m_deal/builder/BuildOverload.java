package uz.greenwhite.smartup5_trade.m_deal.builder;

import java.math.BigDecimal;
import java.util.Set;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverload;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverload;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadLoad;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadRule;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadLoad;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadRule;

public class BuildOverload {

    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<DealOverload> initialOverload;

    BuildOverload(DealRef dealRef) {
        this.dealRef = dealRef;
        this.module = new VisitModule(VisitModule.M_OVERLOAD, false);
        this.initialOverload = getInitial();
    }

    private MyArray<DealOverload> getInitial() {
        DealOverloadModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.overloads : MyArray.<DealOverload>emptyArray();
    }

    private MyArray<String> getOverloadIds() {
        Set<String> actIds = dealRef.filial.overloadIds.asSet();
        for (DealOverload item : initialOverload) {
            actIds.add(item.overloadId);
        }
        return MyArray.from(actIds);
    }

    private MyArray<VOverloadProduct> makeProduct(final Overload overload, final OverloadLoad load) {
        return load.products.map(new MyMapper<OverloadProduct, VOverloadProduct>() {
            @Override
            public VOverloadProduct apply(OverloadProduct item) {
                String overloadKey = "overload_load_bonus_" + load.loadId;

                final Product product = dealRef.findProduct(item.productId);
                if (product == null) {
                    return null;
                }

                BigDecimal quantity = BigDecimal.ZERO;
                Tuple3 key = DealOverload.getKey(overload.overloadId, load.loadId, item.productId);
                DealOverload dealOverload = initialOverload.find(key, DealOverload.KEY_ADAPTER);

                String warehouseId = "";
                String priceTypeId = "";
                String cardCode = "";
                String productUnitId = "";
                WarehouseProductStock balance = null;
                if (dealOverload != null) {
                    quantity = dealOverload.quantity;
                    productUnitId = dealOverload.productUnitId;

                    warehouseId = dealOverload.warehouseId;
                    priceTypeId = dealOverload.priceTypeId;
                    cardCode = dealOverload.cardCode;
                    balance = dealRef.balance.getBalance(warehouseId, item.productId);
                }


                if (balance != null && !balance.hasBalance(Card.ANY)) {
                    return null;
                } else if (balance != null) {
                    balance.bookQuantity(Card.ANY, overloadKey, quantity);
                }

                final PriceType priceType = dealRef.getPriceType(priceTypeId);
                ProductPrice productPrice = null;

                if (priceType != null) {
                    final String finalCardCode = cardCode;
                    productPrice = dealRef.getProductPrices().findFirst(new MyPredicate<ProductPrice>() {
                        @Override
                        public boolean apply(ProductPrice val) {
                            return product.id.equals(val.productId) &&
                                    priceType.id.equals(val.priceTypeId) &&
                                    finalCardCode.equals(val.cardCode);
                        }
                    });
                }
                return new VOverloadProduct(product, productUnitId, item, warehouseId, quantity, balance, productPrice, priceType, overloadKey);
            }
        }).filterNotNull();
    }

    private ValueArray<VOverloadLoad> makeLoad(final Overload overload, final OverloadRule rule) {

        MyArray<VOverloadLoad> result = rule.loads.map(new MyMapper<OverloadLoad, VOverloadLoad>() {
            @Override
            public VOverloadLoad apply(OverloadLoad load) {
                MyArray<VOverloadProduct> result = makeProduct(overload, load);
                if (result.isEmpty()) {
                    return null;
                }

                boolean isTake = result.contains(new MyPredicate<VOverloadProduct>() {
                    @Override
                    public boolean apply(VOverloadProduct vOverloadProduct) {
                        return vOverloadProduct.quantity.nonZero();
                    }
                });
                return new VOverloadLoad(dealRef, load, result, isTake);
            }
        }).filterNotNull();
        return new ValueArray<>(result);
    }

    private ValueArray<VOverloadRule> makeRule(final Overload overload) {

        MyArray<VOverloadRule> vOverloadRules = overload.rules.map(new MyMapper<OverloadRule, VOverloadRule>() {
            @Override
            public VOverloadRule apply(OverloadRule overloadRule) {
                ValueArray<VOverloadLoad> result = makeLoad(overload, overloadRule);
                if (result.getItems().isEmpty()) {
                    return null;
                }
                return new VOverloadRule(overload, overloadRule, result);
            }
        }).filterNotNull();

        return new ValueArray<>(vOverloadRules);
    }

    private VOverloadForm makeForm() {
        final MyArray<Overload> overloads = dealRef.getOverloads();
        MyArray<VOverload> vOverloads = getOverloadIds().map(new MyMapper<String, VOverload>() {
            @Override
            public VOverload apply(String overloadId) {
                Overload overload = overloads.find(overloadId, Overload.KEY_ADAPTER);
                if (overload == null) return null;

                ValueArray<VOverloadRule> vOverloadRule = makeRule(overload);

                if (vOverloadRule.getItems().isEmpty()) {
                    return null;
                }
                return new VOverload(overload, vOverloadRule);
            }
        }).filterNotNull();

        return new VOverloadForm(module, new ValueArray<>(vOverloads));
    }

    public VOverloadModule build() {
        return new VOverloadModule(module, makeForm());
    }
}
