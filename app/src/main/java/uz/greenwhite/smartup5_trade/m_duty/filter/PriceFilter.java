package uz.greenwhite.smartup5_trade.m_duty.filter;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_duty.bean.PriceRow;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class PriceFilter {

    public final ProductFilter productFilter;

    public PriceFilter(ProductFilter productFilter) {
        this.productFilter = productFilter;
    }

    public MyPredicate<PriceRow> getPredicate() {
        final MyPredicate<Product> productPredicate = productFilter.getPredicate();
        return new MyPredicate<PriceRow>() {
            @Override
            public boolean apply(PriceRow priceRow) {
                return productPredicate.apply(priceRow.product);
            }
        };
    }
}

