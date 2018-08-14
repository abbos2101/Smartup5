package uz.greenwhite.smartup5_trade.m_deal.builder;// 30.06.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.CardProduct;
import uz.greenwhite.smartup5_trade.m_deal.common.WP;
import uz.greenwhite.smartup5_trade.m_deal.common.WPProducts;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.OrderRecom;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletRecomProduct;
import uz.greenwhite.smartup5_trade.m_outlet.bean.RecomProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSimilar;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.SettingDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;

class BuilderOrder {

    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<DealOrder> initialOrders;

    private boolean hasViolationDiscount = false;
    private Set<String> hasViolationProductIds = new HashSet<>();

    private final MyArray<Product> productForSimilar;
    private final MyArray<ProductSimilar> productSimilars;

    BuilderOrder(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initialOrders = getInitialOrders();

        makeViolation();

        this.productSimilars = dealRef.getProductSimilars();
        this.productSimilars.checkUniqueness(ProductSimilar.KEY_ADAPTER);
        this.productForSimilar = getProductSimilar();
    }

    private void makeViolation() {
        for (Ban ban : dealRef.violationBans) {

            if (Ban.K_DISCOUNT.equals(ban.kind)) {
                hasViolationDiscount = true;
            }

            if (Ban.K_PRODUCT.equals(ban.kind)) {
                hasViolationProductIds.addAll(ban.kindSourceIds.asSet());
            }
        }
    }

    private MyArray<DealOrder> getInitialOrders() {
        DealOrderModule dealModule = dealRef.findDealModule(module.id);
        return dealModule != null ? dealModule.orders : MyArray.<DealOrder>emptyArray();
    }

    private MyArray<String> getProductIds() {
        Set<String> productIds = dealRef.getProductIds().asSet();
        for (DealOrder order : initialOrders) {
            productIds.add(order.productId);
        }
        return MyArray.from(productIds);
    }

    private MyArray<WP> getWarehouseAndPrices() {
        Set<WP> wps = dealRef.makeWP().asSet();

        for (DealOrder order : initialOrders) {
            wps.add(new WP(order.warehouseId, order.priceTypeId));
        }

        return MyArray.from(wps);
    }

    private MyArray<Product> getProductSimilar() {
        ArrayList<Product> result = new ArrayList<>();
        for (String productId : dealRef.filial.productIds) {
            ProductSimilar productSimilar = productSimilars.find(productId, ProductSimilar.KEY_ADAPTER);
            if (productSimilar == null) continue;
            Product product = dealRef.findProduct(productId);
            if (product != null) {
                result.add(product);
            }
        }
        return MyArray.from(result);
    }

    private MyArray<WPProducts> getWPAndProducts() {
        MyArray<WP> wps = getWarehouseAndPrices();
        MyArray<ProductPrice> prices = dealRef.getProductPrices();

        Map<WP, ArrayList<DealOrder>> wpdo = new HashMap<>();

        for (DealOrder order : initialOrders) {
            WP wp = new WP(order.warehouseId, order.priceTypeId);
            ArrayList<DealOrder> o = wpdo.get(wp);
            if (o == null) {
                o = new ArrayList<>();
                wpdo.put(wp, o);
            }
            o.add(order);
        }

        MyArray<String> productIds = getProductIds();
        Map<String, ArrayList<CardProduct>> pcp = new HashMap<>();
        for (ProductPrice pp : prices) {
            if (productIds.contains(pp.productId, MyMapper.<String>identity())) {
                ArrayList<CardProduct> cp = pcp.get(pp.priceTypeId);
                if (cp == null) {
                    cp = new ArrayList<>();
                    pcp.put(pp.priceTypeId, cp);
                }
                cp.add(new CardProduct(pp.cardCode, pp.productId));
            }
        }

        ArrayList<WPProducts> wpps = new ArrayList<>();
        for (WP wp : wps) {
            ArrayList<CardProduct> pIds = new ArrayList<>();

            ArrayList<DealOrder> dealOrders = wpdo.get(wp);
            if (dealOrders != null && !dealOrders.isEmpty()) {
                for (DealOrder order : dealOrders) {
                    pIds.add(new CardProduct(order.cardCode, order.productId));
                }
            }

            final Setter<MyArray<CardProduct>> cps = new Setter<>();
            cps.value = MyArray.from(pIds);
            cps.value.checkUniqueness(CardProduct.KEY_ADAPTER);

            ArrayList<CardProduct> cardProducts = pcp.get(wp.priceTypeId);
            if (cardProducts != null && !cardProducts.isEmpty()) {
                MyArray<CardProduct> result = MyArray.from(cardProducts).filter(new MyPredicate<CardProduct>() {
                    @Override
                    public boolean apply(CardProduct cardProduct) {
                        return !cps.value.contains(cardProduct.getKey(), CardProduct.KEY_ADAPTER);
                    }
                });
                cps.value = cps.value.append(result);
            }


            wpps.add(new WPProducts(wp.warehouseId, wp.priceTypeId, cps.value));
        }
        return MyArray.from(wpps);

    }

    private MyArray<SpinnerOption> makeDiscountOptions(MyArray<Margin> discounts) {
        if (hasViolationDiscount) {
            return MyArray.emptyArray();
        }
        return discounts.map(new MyMapper<Margin, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Margin d) {
                return new SpinnerOption(d.id, d.name, d.percent);
            }
        });
    }

    private ValueSpinner makeDiscountForm(MyArray<SpinnerOption> discounts, MyArray<VDealOrder> orders) {
        MyArray<SpinnerOption> result = discounts;
        boolean isOrderContainDiscount = false;

        if (result.isEmpty()) {
            isOrderContainDiscount = orders.contains(new MyPredicate<VDealOrder>() {
                @Override
                public boolean apply(VDealOrder vDealOrder) {
                    return vDealOrder.margin.nonZero();
                }
            });
        }

        ValueSpinner discountSpinner = null;
        if (result.nonEmpty() || isOrderContainDiscount) {
            result = result.append(new SpinnerOption(0, DS.getString(R.string.deal_remove_margin), BigDecimal.ZERO));
            discountSpinner = new ValueSpinner(result);
        }
        return discountSpinner;
    }

    private MyArray<PaymentType> getPaymentTypes() {
        return dealRef.room.paymentTypeIds.map(new MyMapper<String, PaymentType>() {
            @Override
            public PaymentType apply(String paymentTypeId) {
                return dealRef.getPaymentType(paymentTypeId);
            }
        }).filterNotNull();
    }

    private MyArray<String> getMmlProductIds() {
        Set<String> productIds = dealRef.getMmlPersonTypeProducts().map(new MyMapper<MmlPersonTypeProduct, String>() {
            @Override
            public String apply(MmlPersonTypeProduct mmlPersonTypeProduct) {
                return mmlPersonTypeProduct.productId;
            }
        }).asSet();
        return MyArray.from(productIds);
    }

    private MyArray<VDealOrder> makeOrders(Warehouse warehouse,
                                           PriceType priceType,
                                           MyArray<CardProduct> cardProducts,
                                           MyArray<ProductPrice> prices,
                                           MyArray<ProductBalance> balances,
                                           OutletRecomProduct recoms,
                                           PriceEditable priceEditable,
                                           MyArray<String> mmlProductIds) {
        ArrayList<VDealOrder> orders = new ArrayList<>();
        for (final CardProduct cp : cardProducts) {
            if (hasViolationProductIds.contains(cp.productId)) {
                continue;
            }

            Tuple4 initialOrderKey = DealOrder.getKey(cp.productId, warehouse.id, priceType.id, cp.cardCode);
            Tuple3 productPriceKey = ProductPrice.getKey(priceType.id, cp.productId, cp.cardCode);
            Tuple3 balanceKey = ProductBalance.getKey(warehouse.id, cp.productId, cp.cardCode);

            DealOrder order = initialOrders.find(initialOrderKey, DealOrder.KEY_ADAPTER);
            ProductPrice price = prices.find(productPriceKey, ProductPrice.KEY_ADAPTER);
            ProductBalance balance = balances.find(balanceKey, ProductBalance.KEY_ADAPTER);

            if (price == null) {
                continue;
            }

            Card card = priceType.withCard ? Card.make(price.cardCode) : Card.ANY;

            Product product = dealRef.findProduct(cp.productId);
            ProductBarcode barcode = dealRef.getProductBarcode(cp.productId);
            RoundModel roundModel = dealRef.dealHolder.deal.header.roundModel;
            WarehouseProductStock wps = dealRef.balance.getBalance(warehouse.id, cp.productId);

            if (product == null || (!dealRef.setting.deal.allowDealDraft && (wps == null || wps.nonBalance(card)))) {
                continue;
            }

            if ((wps == null || wps.nonBalance(card)) && dealRef.setting.deal.onlyHasBalance) {
                continue;
            }

            boolean orderNotFound = order == null;
            String productUnitId = orderNotFound ? "" : order.productUnitId;
            BigDecimal quant = orderNotFound ? null : order.quantity;
            BigDecimal margin = orderNotFound ? null : order.discount;
            BigDecimal pricePerQuant = orderNotFound ? (price == null ? BigDecimal.ZERO : price.price) : order.pricePerQuant;
            String bonusId = orderNotFound ? null : order.bonusId;

            if (!orderNotFound && wps != null) {
                wps.bookQuantity(card, price.priceTypeId, quant);
            }

            OrderRecom recom = null;
            RecomProduct rp = recoms == null ? null : recoms.recomProducts.find(cp.productId, RecomProduct.KEY_ADAPTER);
            if (rp != null) {
                SettingDeal setting = dealRef.setting.deal;
                recom = new OrderRecom(setting.recomDay, setting.recomCoef, rp.recomData, rp.lookUpDay);
            }

            boolean mmlProduct = mmlProductIds.contains(product.id, MyMapper.<String>identity());

            if (mmlProductIds.isEmpty() || !dealRef.setting.deal.mml || mmlProduct) {
                orders.add(new VDealOrder(product, productUnitId, price, balance, priceEditable, card, mmlProduct,
                        pricePerQuant, quant, margin, wps, roundModel, barcode, recom, bonusId));
            }
        }

        Collections.sort(orders, new Comparator<VDealOrder>() {
            @Override
            public int compare(VDealOrder l, VDealOrder r) {
                int compare = MyPredicate.compare(l.mmlProduct ? 0 : 1, r.mmlProduct ? 0 : 1);
                if (compare == 0) {
                    compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                    if (compare == 0) {
                        return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                    }
                }
                return compare;
            }
        });

        return MyArray.from(orders);
    }

    private ValueArray<VDealOrderForm> makeOrderForms() {
        MyArray<Margin> margins = dealRef.getMargins();
        MyArray<ProductPrice> prices = dealRef.getProductPrices();
        MyArray<ProductBalance> balances = dealRef.getProductBalances();
        OutletRecomProduct recomProduct = dealRef.getOutletRecomProduct();
        MyArray<PaymentType> paymentTypes = getPaymentTypes();
        MyArray<String> mmlProductIds = getMmlProductIds();

        MyArray<WPProducts> wpps = getWPAndProducts();
        MyArray<SpinnerOption> discountOptions = makeDiscountOptions(margins);

        ArrayList<VDealOrderForm> result = new ArrayList<>(wpps.size());
        for (WPProducts wpp : wpps) {
            Warehouse warehouse = dealRef.getWarehouse(wpp.warehouseId);
            PriceType priceType = dealRef.getPriceType(wpp.priceTypeId);

            if (warehouse != null && priceType != null) {
                final Currency currency = dealRef.getCurrency(priceType.currencyId);
                if (currency == null) continue;

                boolean contains = paymentTypes.contains(new MyPredicate<PaymentType>() {
                    @Override
                    public boolean apply(PaymentType paymentType) {
                        return paymentType.currencyId.equals(currency.currencyId);
                    }
                });
                if (!contains || currency.price.compareTo(BigDecimal.ZERO) == 0) continue;

                PriceEditable priceEditable = dealRef.getPriceEditable(priceType.id);
                if (priceEditable == null) {
                    priceEditable = PriceEditable.makeDefault(priceType.id);
                }

                MyArray<VDealOrder> orders = makeOrders(warehouse, priceType,
                        wpp.cardProducts, prices, balances, recomProduct, priceEditable, mmlProductIds);

                if (orders.nonEmpty()) {
                    result.add(new VDealOrderForm(dealRef, module, warehouse, priceType,
                            currency, new ValueArray<>(orders),
                            makeDiscountForm(discountOptions, orders), priceEditable, this.productForSimilar, this.productSimilars));
                }
            }
        }

        Collections.sort(result, new Comparator<VDealOrderForm>() {
            @Override
            public int compare(VDealOrderForm l, VDealOrderForm r) {
                int compare = CharSequenceUtil.compareToIgnoreCase(l.warehouse.name, r.warehouse.name);
                if (compare == 0) {
                    if (!r.priceType.withCard) {
                        return -1;
                    }
                    return CharSequenceUtil.compareToIgnoreCase(l.priceType.name, r.priceType.name);
                }
                return compare;
            }
        });
        return new ValueArray<>(MyArray.from(result));
    }

    public VDealOrderModule build() {
        return new VDealOrderModule(module, makeOrderForms());
    }
}
