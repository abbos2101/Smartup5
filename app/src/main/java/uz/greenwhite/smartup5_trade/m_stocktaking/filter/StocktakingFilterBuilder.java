package uz.greenwhite.smartup5_trade.m_stocktaking.filter;

import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterBuilder;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktakingProduct;

public class StocktakingFilterBuilder {

    final Scope scope;
    final StocktakingFilterValue value;
    final VStocktaking vStocktaking;

    public StocktakingFilterBuilder(Scope scope, StocktakingFilterValue value, VStocktaking vStocktaking) {
        this.scope = scope;
        this.value = value;
        this.vStocktaking = vStocktaking;
    }

    public StocktakingFilter build() {
        ProductFilter product = makeProductFilter();
        FilterSpinner productCardCode = getProductCardCodeFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));

        return new StocktakingFilter(product, productCardCode, hasValue);
    }

    private ProductFilter makeProductFilter() {
        ProductFilterBuilder builder = new ProductFilterBuilder(value.product,
                vStocktaking.vProducts.getItems().map(VStocktakingProduct.MAP_PRODUCT),
                scope.ref.getProductGroups(),
                scope.ref.getProductTypes(),
                scope.ref.getProductBarcodes(),
                MyArray.<ProductPhoto>emptyArray(),
                MyArray.<ProductFile>emptyArray(),
                MyArray.<String>emptyArray());

        return builder.build();
    }

    private FilterSpinner getProductCardCodeFilter() {
        Set<String> cardCodes = new HashSet<>();
        for (VStocktakingProduct val : vStocktaking.vProducts.getItems()) {
            cardCodes.add(val.card.code);
        }
        MyArray<SpinnerOption> options = MyArray.from(cardCodes).map(new MyMapper<String, SpinnerOption>() {
            @Override
            public SpinnerOption apply(String s) {
                return new SpinnerOption(s, s);
            }
        }).sort(SpinnerOption.SORT_BY_NAME);
        return FilterSpinner.build(DS.getString(R.string.deal_card_number), value.cardCode, options);
    }


    public static StocktakingFilter build(Scope scope, final VStocktaking vStocktaking, final StocktakingFilterValue value) {
        StocktakingFilterBuilder builder = new StocktakingFilterBuilder(scope, value, vStocktaking);
        return builder.build();
    }
}
