package uz.greenwhite.smartup5_trade.m_stocktaking.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductLastInputPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductLastPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.WarehouseBalance;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.Stocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingProduct;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktakingHeader;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktakingProduct;

public class BuilderStocktaking {

    public static String stringify(VStocktaking variable) {
        StocktakingHolder old = variable.holder;
        Stocktaking stocktaking = variable.convertToValue();
        StocktakingHolder holder = new StocktakingHolder(stocktaking, old.state);
        return Uzum.toJson(holder, StocktakingHolder.UZUM_ADAPTER);
    }

    public static VStocktaking make(Scope scope, StocktakingHolder holder) {
        VStocktakingHeader header = makeHeader(scope, holder);
        ValueArray<VStocktakingProduct> products = makeProduct(scope, holder);
        return new VStocktaking(holder, header, products);
    }

    private static VStocktakingHeader makeHeader(final Scope scope, StocktakingHolder holder) {
        Filial filial = scope.ref.getFilial(scope.filialId);

        MyArray<SpinnerOption> currencies = filial.currencyIds.map(new MyMapper<String, SpinnerOption>() {
            @Override
            public SpinnerOption apply(String currencyId) {
                Currency currency = scope.ref.getCurrency(currencyId);
                if (currency == null) {
                    return null;
                }
                return new SpinnerOption(currencyId, currency.getName(), currency);
            }
        }).filterNotNull();

        SpinnerOption currencySelect = currencies.find(holder.stocktaking.header.currencyId, SpinnerOption.KEY_ADAPTER);

        ValueSpinner currency = new ValueSpinner(currencies, currencySelect);
        return new VStocktakingHeader(holder.stocktaking.header, currency);
    }

    public static ValueArray<VStocktakingProduct> makeProduct(final Scope scope, StocktakingHolder holder) {
        MyArray<StocktakingProduct> products = holder.stocktaking.products;

        Map<String, ArrayList<StocktakingProduct>> stocktakingProductMap = new HashMap<>();
        for (StocktakingProduct val : products) {
            ArrayList<StocktakingProduct> result = stocktakingProductMap.get(val.productId);
            if (result == null) {
                result = new ArrayList<>();
                stocktakingProductMap.put(val.productId, result);
            }
            result.add(val);
        }

        Filial filial = scope.ref.getFilial(scope.filialId);
        final MyArray<Product> allProducts = scope.ref.getProducts();

        Map<String, ArrayList<WarehouseBalance>> productBalanceMap = new HashMap<>();
        for (WarehouseBalance val : scope.ref.getWarehouseBalances()) {
            if (!val.warehouseId.equals(holder.stocktaking.warehouseId)) {
                continue;
            }
            ArrayList<WarehouseBalance> balances = productBalanceMap.get(val.productId);
            if (balances == null) {
                balances = new ArrayList<>();
                productBalanceMap.put(val.productId, balances);
            }

            balances.add(val);
        }

        Map<String, Map<String, BigDecimal>> lastInputPriceMap = new HashMap<>();
        MyArray<ProductLastInputPrice> productLastInputPrice = scope.ref.getProductLastInputPrice();
        for (ProductLastInputPrice val : productLastInputPrice) {
            for (ProductLastPrice item : val.prices) {
                Map<String, BigDecimal> map = lastInputPriceMap.get(item.productId);
                if (map == null) lastInputPriceMap.put(item.productId, (map = new HashMap<>()));
                map.put(val.currencyId, item.price);
            }
        }

        MyArray<ProductBarcode> productBarcodes = scope.ref.getProductBarcodes();
        Set<String> productKeys = new HashSet<>();
        ArrayList<VStocktakingProduct> result = new ArrayList<>();
        for (String productId : filial.productIds.asSet()) {
            final Product product = allProducts.find(productId, Product.KEY_ADAPTER);

            ArrayList<StocktakingProduct> filterProduct = stocktakingProductMap.get(productId);
            ArrayList<WarehouseBalance> filterBalance = productBalanceMap.get(productId);

            ProductBarcode productBarcode = productBarcodes.find(productId, ProductBarcode.KEY_ADAPTER);

            Map<String, BigDecimal> lastPrice = lastInputPriceMap.get(productId);

            if (filterProduct == null || filterProduct.isEmpty()) {
                if (filterBalance == null || filterBalance.isEmpty()) {
                    result.add(new VStocktakingProduct(product, Card.EMPTY, null, lastPrice, productBarcode, null, null));
                } else {
                    for (WarehouseBalance val : filterBalance) {
                        result.add(new VStocktakingProduct(product, Card.make(val.cardCode), val, lastPrice, productBarcode, null, null));
                    }
                }

            } else {
                if (filterBalance == null || filterBalance.isEmpty()) {
                    for (StocktakingProduct val : filterProduct) {
                        result.add(new VStocktakingProduct(product, Card.EMPTY, null, lastPrice, productBarcode, val.quantity, val.getPrice()));
                    }
                } else {
                    for (WarehouseBalance balance : filterBalance) {
                        boolean canSet = false;
                        for (StocktakingProduct val : filterProduct) {
                            if (val.cardCode.equals(balance.cardCode) && val.expireDate.equals(balance.expireDate)) {
                                result.add(new VStocktakingProduct(product, Card.make(balance.cardCode), balance, lastPrice, productBarcode, val.quantity, val.getPrice()));
                                canSet = true;
                            }
                        }

                        if (!canSet){
                            result.add(new VStocktakingProduct(product, Card.make(balance.cardCode), balance, lastPrice, productBarcode, null, null));
                        }
                    }
                }
            }
        }

        Collections.sort(result, new Comparator<VStocktakingProduct>() {
            @Override
            public int compare(VStocktakingProduct l, VStocktakingProduct r) {
                int compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                }
                return compare;
            }
        });
        return new ValueArray<>(MyArray.from(result));
    }

}
