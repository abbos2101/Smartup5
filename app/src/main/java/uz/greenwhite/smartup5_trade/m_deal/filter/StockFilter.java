package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class StockFilter {

    public final String formCode;
    public final ProductFilter product;
    public final FilterBoolean hasValue;

    public StockFilter(String formCode,
                       ProductFilter product,
                       FilterBoolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public StockFilterValue toValue() {
        return new StockFilterValue(
                formCode,
                product.toValue(),
                hasValue.value.getValue()
        );
    }

    public MyPredicate<VDealStockProduct> getPredicate() {

        MyPredicate<VDealStockProduct> result = MyPredicate.True();
        result = result.and(getProductPredicate())
                .and(getHasValuePredicate());

        return result;
    }

    private MyPredicate<VDealStockProduct> getProductPredicate() {
        return MyPredicate.cover(product.getPredicate(), new MyMapper<VDealStockProduct, Product>() {
            @Override
            public Product apply(VDealStockProduct vDealOrder) {
                return vDealOrder.product;
            }
        });
    }

    private MyPredicate<VDealStockProduct> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VDealStockProduct>() {
                @Override
                public boolean apply(VDealStockProduct vDealOrder) {
                    return vDealOrder.hasValue();
                }
            };
        }
        return null;
    }

    public static MyMapper<StockFilter, String> KEY_ADAPTER = new MyMapper<StockFilter, String>() {
        @Override
        public String apply(StockFilter orderFilter) {
            return orderFilter.formCode;
        }
    };
}
