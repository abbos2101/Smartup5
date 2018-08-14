package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class RetailAuditFilter {

    public final String formCode;
    public final ProductFilter product;
    public final FilterBoolean hasValue;

    public RetailAuditFilter(String formCode,
                             ProductFilter productFilter,
                             FilterBoolean hasValue) {
        this.formCode = formCode;
        this.product = productFilter;
        this.hasValue = hasValue;
    }

    public RetailAuditFilterValue toValue() {
        return new RetailAuditFilterValue(
                formCode,
                product.toValue(),
                hasValue.value.getValue());
    }

    public MyPredicate<VDealRetailAudit> getPredicate() {
        MyPredicate<VDealRetailAudit> result = MyPredicate.cover(product.getPredicate(), new MyMapper<VDealRetailAudit, Product>() {
            @Override
            public Product apply(VDealRetailAudit item) {
                return item.product;
            }
        });

        result = result.and(getHasValuePredicate());

        return result;
    }

    private MyPredicate<VDealRetailAudit> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VDealRetailAudit>() {
                @Override
                public boolean apply(VDealRetailAudit item) {
                    return item.hasValue();
                }
            };
        }
        return null;
    }

    public static MyMapper<RetailAuditFilter, String> KEY_ADAPTER = new MyMapper<RetailAuditFilter, String>() {
        @Override
        public String apply(RetailAuditFilter filter) {
            return filter.formCode;
        }
    };
}