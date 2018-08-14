package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGift;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class GiftFilter {

    public final String formCode;
    public final ProductFilter product;
    public final FilterBoolean hasValue;

    public GiftFilter(String formCode,
                      ProductFilter product,
                      FilterBoolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public GiftFilterValue toValue() {
        return new GiftFilterValue(
                formCode,
                product.toValue(),
                hasValue.value.getValue()
        );
    }

    public MyPredicate<VDealGift> getPredicate() {

        MyPredicate<VDealGift> result = MyPredicate.True();
        result = result.and(getProductPredicate())
                .and(getHasValuePredicate());

        return result;
    }

    private MyPredicate<VDealGift> getProductPredicate() {
        return MyPredicate.cover(product.getPredicate(), new MyMapper<VDealGift, Product>() {
            @Override
            public Product apply(VDealGift val) {
                return val.product;
            }
        });
    }

    private MyPredicate<VDealGift> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VDealGift>() {
                @Override
                public boolean apply(VDealGift val) {
                    return val.hasValue();
                }
            };
        }
        return null;
    }

    public static MyMapper<GiftFilter, String> KEY_ADAPTER = new MyMapper<GiftFilter, String>() {
        @Override
        public String apply(GiftFilter orderFilter) {
            return orderFilter.formCode;
        }
    };
}
