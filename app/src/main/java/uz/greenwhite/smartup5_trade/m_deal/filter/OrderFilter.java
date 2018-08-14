package uz.greenwhite.smartup5_trade.m_deal.filter;

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class OrderFilter {

    public final String formCode;
    public final ProductFilter product;
    public final FilterSpinner productCard;
    public final FilterBoolean hasValue;
    public final FilterBoolean hasDiscount;
    public final FilterBoolean warehouseAvail;
    public final FilterBoolean sortFirstMll;

    public OrderFilter(String formCode,
                       ProductFilter product,
                       FilterSpinner productCard,
                       FilterBoolean hasValue,
                       FilterBoolean hasDiscount,
                       FilterBoolean warehouseAvail,
                       FilterBoolean sortFirstMll) {
        this.formCode = formCode;
        this.product = product;
        this.productCard = productCard;
        this.hasValue = hasValue;
        this.hasDiscount = hasDiscount;
        this.warehouseAvail = warehouseAvail;
        this.sortFirstMll = sortFirstMll;
    }

    public OrderFilterValue toValue() {
        String productCardCode = "";
        if (productCard != null) {
            productCardCode = productCard.value.getValue().code;
        }
        return new OrderFilterValue(
                formCode,
                product.toValue(),
                productCardCode,
                hasValue.value.getValue(),
                hasDiscount.value.getValue(),
                warehouseAvail.value.getValue(),
                sortFirstMll.value.getValue());
    }

    public MyPredicate<VDealOrder> getPredicate() {

        MyPredicate<VDealOrder> result = MyPredicate.True();
        result = result.and(getProductPredicate())
                .and(getProductCardCodePredicate())
                .and(getHasValuePredicate())
                .and(getHasDiscountPredicate())
                .and(getWarehouseAvailPredicate());
        return result;
    }

    private MyPredicate<VDealOrder> getProductPredicate() {
        return MyPredicate.cover(product.getPredicate(), new MyMapper<VDealOrder, Product>() {
            @Override
            public Product apply(VDealOrder vDealOrder) {
                return vDealOrder.product;
            }
        });
    }

    private MyPredicate<VDealOrder> getProductCardCodePredicate() {
        if (productCard != null) {
            final SpinnerOption value = productCard.value.getValue();
            if (productCard.isNotSelected()) {
                return MyPredicate.True();
            }
            if (!TextUtils.isEmpty(value.code)) {
                return new MyPredicate<VDealOrder>() {
                    @Override
                    public boolean apply(VDealOrder vDealOrder) {
                        return value.code.equals(vDealOrder.price.cardCode);
                    }
                };
            }
        }
        return null;
    }

    private MyPredicate<VDealOrder> getHasDiscountPredicate() {
        if (hasDiscount.value.getValue()) {
            return new MyPredicate<VDealOrder>() {
                @Override
                public boolean apply(VDealOrder vDealOrder) {
                    return vDealOrder.margin.nonZero();
                }
            };
        }
        return null;
    }

    private MyPredicate<VDealOrder> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VDealOrder>() {
                @Override
                public boolean apply(VDealOrder vDealOrder) {
                    return vDealOrder.hasValue();
                }
            };
        }
        return null;
    }

    private MyPredicate<VDealOrder> getWarehouseAvailPredicate() {
        if (warehouseAvail.value.getValue()) {
            return new MyPredicate<VDealOrder>() {
                @Override
                public boolean apply(VDealOrder vDealOrder) {
                    BigDecimal warehouseAvail = vDealOrder.getBalanceOfWarehouse();
                    BigDecimal totalOrderPrice = vDealOrder.getTotalOrderPrice();

                    return warehouseAvail.compareTo(BigDecimal.ZERO) != 0 ||
                            totalOrderPrice.compareTo(BigDecimal.ZERO) != 0;
                }
            };
        }
        return null;
    }

    public static MyMapper<OrderFilter, String> KEY_ADAPTER = new MyMapper<OrderFilter, String>() {
        @Override
        public String apply(OrderFilter orderFilter) {
            return orderFilter.formCode;
        }
    };
}
