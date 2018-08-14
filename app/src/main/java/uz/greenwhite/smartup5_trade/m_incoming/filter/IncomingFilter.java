package uz.greenwhite.smartup5_trade.m_incoming.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class IncomingFilter {

    public final ProductFilter product;

    public IncomingFilter(ProductFilter product) {
        this.product = product;
    }

    public IncomingFilterValue toValue() {
        return new IncomingFilterValue(product.toValue());
    }

    public MyPredicate<VIncomingProduct> getPredicate() {

        MyPredicate<VIncomingProduct> result = MyPredicate.True();
        result = result.and(getProductPredicate());
        return result;
    }

    private MyPredicate<VIncomingProduct> getProductPredicate() {
        return MyPredicate.cover(product.getPredicate(), new MyMapper<VIncomingProduct, Product>() {
            @Override
            public Product apply(VIncomingProduct item) {
                return item.product;
            }
        });
    }
}
