package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgree;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class AgreeFilter {

    public final String formCode;
    public final ProductFilter product;
    public final FilterBoolean hasValue;

    public AgreeFilter(String formCode,
                       ProductFilter productFilter,
                       FilterBoolean hasValue) {
        this.formCode = formCode;
        this.product = productFilter;
        this.hasValue = hasValue;
    }

    public AgreeFilterValue toValue() {
        return new AgreeFilterValue(
                formCode,
                product.toValue(),
                hasValue.value.getValue());
    }

    public MyPredicate<VDealAgree> getPredicate() {
        MyPredicate<VDealAgree> result = MyPredicate.cover(product.getPredicate(), new MyMapper<VDealAgree, Product>() {
            @Override
            public Product apply(VDealAgree vDealAgree) {
                return vDealAgree.product;
            }
        });

        result = result.and(getHasValuePredicate());

        return result;
    }

    private MyPredicate<VDealAgree> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VDealAgree>() {
                @Override
                public boolean apply(VDealAgree vDealAgree) {
                    return vDealAgree.hasValue();
                }
            };
        }
        return null;
    }


    public static MyMapper<AgreeFilter, String> KEY_ADAPTER = new MyMapper<AgreeFilter, String>() {
        @Override
        public String apply(AgreeFilter filter) {
            return filter.formCode;
        }
    };

}
