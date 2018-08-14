package uz.greenwhite.smartup5_trade.m_incoming.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_incoming.bean.Incoming;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingProduct;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncoming;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingHeader;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProductDetail;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class BuilderIncoming {

    public static String stringify(VIncoming variable) {
        IncomingHolder old = variable.holder;
        Incoming incoming = variable.convertToIncoming();
        IncomingHolder holder = new IncomingHolder(incoming, old.state);
        return Uzum.toJson(holder, IncomingHolder.UZUM_ADAPTER);
    }

    public static VIncoming make(Scope scope, IncomingHolder holder) {
        VIncomingHeader header = makeHeader(scope, holder);
        MyArray<Product> filialProducts = scope.ref.getProducts();
        MyArray<ProductBarcode> productBarcodes = scope.ref.getProductBarcodes();
        ValueArray<VIncomingProduct> products = makeProduct(scope, holder, filialProducts);
        return new VIncoming(holder, header, products, filialProducts, productBarcodes);
    }

    private static VIncomingHeader makeHeader(final Scope scope, IncomingHolder holder) {
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

        SpinnerOption currencySelect = currencies.find(holder.incoming.header.currencyId, SpinnerOption.KEY_ADAPTER);

        final MyArray<Outlet> outlets = scope.ref.getOutlets();
        MyArray<SpinnerOption> providerPersons = filial.providerPersonIds.map(new MyMapper<String, SpinnerOption>() {
            @Override
            public SpinnerOption apply(String personId) {
                Outlet outlet = outlets.find(personId, Outlet.KEY_ADAPTER);
                if (outlet == null) return null;
                return new SpinnerOption(outlet.id, outlet.name, outlet);
            }
        }).filterNotNull();

        SpinnerOption personSelect = providerPersons.find(holder.incoming.header.providerPersonId, SpinnerOption.KEY_ADAPTER);

        ValueSpinner currency = new ValueSpinner(currencies, currencySelect);
        ValueSpinner providerPerson = new ValueSpinner(providerPersons, personSelect);
        return new VIncomingHeader(holder.incoming.header, currency, providerPerson);
    }

    public static ValueArray<VIncomingProduct> makeProduct(final Scope scope, IncomingHolder holder, MyArray<Product> filialProducts) {
        MyArray<IncomingProduct> products = holder.incoming.products;
        MyArray<ProductBarcode> productBarcodes = scope.ref.getProductBarcodes();

        Map<String, ArrayList<IncomingProduct>> incomingProductMap = new HashMap<>();
        for (IncomingProduct val : products) {
            ArrayList<IncomingProduct> result = incomingProductMap.get(val.productId);
            if (result == null) {
                result = new ArrayList<>();
                incomingProductMap.put(val.productId, result);
            }
            result.add(val);
        }

        Filial filial = scope.ref.getFilial(scope.filialId);

        ArrayList<VIncomingProduct> result = new ArrayList<>();
        for (String productId : filial.productIds) {
            final Product product = filialProducts.find(productId, Product.KEY_ADAPTER);

            ArrayList<IncomingProduct> filterProduct = incomingProductMap.get(productId);
            if (filterProduct == null || filterProduct.isEmpty()) continue;

            ProductBarcode barcode = productBarcodes.find(productId, ProductBarcode.KEY_ADAPTER);


            if (!filterProduct.isEmpty()) {
                MyArray<VIncomingProductDetail> productDetails = MyArray.from(filterProduct).map(new MyMapper<IncomingProduct, VIncomingProductDetail>() {
                    @Override
                    public VIncomingProductDetail apply(IncomingProduct val) {
                        return new VIncomingProductDetail(val.cardNumber, val.getManufacturerPrice(),
                                val.quantity, val.expireDate, val.getPrice());
                    }
                });
                result.add(new VIncomingProduct(product, barcode, new ValueArray<>(productDetails)));
            }
        }

        Collections.sort(result, new Comparator<VIncomingProduct>() {
            @Override
            public int compare(VIncomingProduct l, VIncomingProduct r) {
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
