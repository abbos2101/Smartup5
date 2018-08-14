package uz.greenwhite.smartup5_trade.m_stocktaking.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktakingProduct;

public class StocktakingFilter {

    public final ProductFilter product;
    public final FilterSpinner productCard;
    public final FilterBoolean hasValue;

    public StocktakingFilter(ProductFilter product, FilterSpinner productCard, FilterBoolean hasValue) {
        this.product = product;
        this.productCard = productCard;
        this.hasValue = hasValue;
    }

    public StocktakingFilterValue toValue() {
        String productCardCode = "";
        if (productCard != null) {
            productCardCode = productCard.value.getValue().code;
        }
        return new StocktakingFilterValue(
                product.toValue(),
                productCardCode,
                hasValue.value.getValue());
    }

    public MyPredicate<VStocktakingProduct> getPredicate() {

        MyPredicate<VStocktakingProduct> result = MyPredicate.True();
        result = result.and(getProductPredicate())
                .and(getProductCardCodePredicate())
                .and(getHasValuePredicate());
        return result;
    }

    private MyPredicate<VStocktakingProduct> getProductPredicate() {
        return MyPredicate.cover(product.getPredicate(), new MyMapper<VStocktakingProduct, Product>() {
            @Override
            public Product apply(VStocktakingProduct item) {
                return item.product;
            }
        });
    }

    private MyPredicate<VStocktakingProduct> getProductCardCodePredicate() {
        if (productCard != null) {
            final SpinnerOption value = productCard.value.getValue();
            if (productCard.isNotSelected()) {
                return MyPredicate.True();
            }
            if (!TextUtils.isEmpty(value.code)) {
                return new MyPredicate<VStocktakingProduct>() {
                    @Override
                    public boolean apply(VStocktakingProduct item) {
                        return value.code.equals(item.card.code);
                    }
                };
            }
        }
        return null;
    }

    private MyPredicate<VStocktakingProduct> getHasValuePredicate() {
        if (hasValue.value.getValue()) {
            return new MyPredicate<VStocktakingProduct>() {
                @Override
                public boolean apply(VStocktakingProduct item) {
                    return item.hasValue();
                }
            };
        }
        return null;
    }
}
