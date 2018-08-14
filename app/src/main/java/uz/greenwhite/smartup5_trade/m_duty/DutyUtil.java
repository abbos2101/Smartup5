package uz.greenwhite.smartup5_trade.m_duty;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyFlatMapper;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_duty.bean.PriceRow;
import uz.greenwhite.smartup5_trade.m_product.bean.PhotoInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;

public class DutyUtil {

    private static MyArray<Product> getRoomSectorProducts(final Scope scope) {
        Set<String> productIds = DSUtil.getFilialRooms(scope).flatMap(new MyFlatMapper<Room, String>() {
            @Override
            public MyArray<String> apply(Room element) {
                return element.productIds;
            }
        }).asSet();
        return MyArray.from(productIds).map(new MyMapper<String, Product>() {
            @Override
            public Product apply(String productId) {
                return scope.ref.getProduct(productId);
            }
        }).filterNotNull();
    }

    private static Map<String, ArrayList<ProductPrice>> getProductPricesMap(Scope scope) {
        MyArray<ProductPrice> productPrices = scope.ref.getProductPrices();
        Map<String, ArrayList<ProductPrice>> result = new HashMap<>();
        for (ProductPrice item : productPrices) {
            if (item.price.compareTo(BigDecimal.ZERO) == 0) continue;
            ArrayList<ProductPrice> prices = result.get(item.productId);
            if (prices == null) {
                prices = new ArrayList<>();
                result.put(item.productId, prices);
            }
            prices.add(item);
        }
        return result;
    }


    public static MyArray<PriceRow> getPriceRows(final Scope scope) {
        assert scope.ref != null;

        MyArray<ProductPhoto> productPhotos = scope.ref.getProductPhotos();
        MyArray<PriceType> priceTypes = scope.ref.getPriceTypes();
        MyArray<Product> products = getRoomSectorProducts(scope);
        Map<String, ArrayList<ProductPrice>> productPriceMaps = getProductPricesMap(scope);

        ArrayList<PriceRow> result = new ArrayList<>();
        for (Product product : products) {

            ArrayList<ProductPrice> prices = productPriceMaps.get(product.id);
            if (prices == null) continue;

            ShortHtml pName = UI.html();
            ShortHtml pAmount = UI.html();
            boolean firstLoop = true;
            for (ProductPrice price : prices) {
                PriceType priceType = priceTypes.find(price.priceTypeId, PriceType.KEY_ADAPTER);
                if (!firstLoop) {
                    pName.br();
                    pAmount.br();
                }
                pName.v(priceType.name);
                pAmount.v(NumberUtil.formatMoney(price.price));
                firstLoop = false;
            }

            ProductPhoto productPhoto = productPhotos.find(product.id, ProductPhoto.KEY_ADAPTER);
            PhotoInfo photoInfo = null;
            if (productPhoto != null && productPhoto.photos.nonEmpty()) {
                photoInfo = productPhoto.photos.sort(new Comparator<PhotoInfo>() {
                    @Override
                    public int compare(PhotoInfo l, PhotoInfo r) {
                        return CharSequenceUtil.compareToIgnoreCase(l.orderNo, r.orderNo);
                    }
                }).get(0);
            }

            result.add(new PriceRow(product, pName.html(), pAmount.html(), photoInfo));
        }

        return MyArray.from(result).sort(new Comparator<PriceRow>() {
            @Override
            public int compare(PriceRow l, PriceRow r) {
                int compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                if (compare == 0) {
                    compare = CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                }
                return compare;
            }
        });
    }
}
