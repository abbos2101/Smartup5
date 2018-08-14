package uz.greenwhite.smartup5_trade.m_incoming.filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterBuilder;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncoming;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;

public class IncomingFilterBuilder {

    final Scope scope;
    final IncomingFilterValue value;
    final VIncoming vIncoming;

    public IncomingFilterBuilder(Scope scope, IncomingFilterValue value, VIncoming vIncoming) {
        this.scope = scope;
        this.value = value;
        this.vIncoming = vIncoming;
    }

    public IncomingFilter build() {
        ProductFilter product = makeProductFilter();
        return new IncomingFilter(product);
    }

    private ProductFilter makeProductFilter() {
        ProductFilterBuilder builder = new ProductFilterBuilder(value.product,
                vIncoming.products,
                scope.ref.getProductGroups(),
                scope.ref.getProductTypes(),
                scope.ref.getProductBarcodes(),
                MyArray.<ProductPhoto>emptyArray(),
                MyArray.<ProductFile>emptyArray(),
                MyArray.<String>emptyArray());

        return builder.build();
    }


    public static IncomingFilter build(Scope scope, final VIncoming vIncoming, final IncomingFilterValue value) {
        IncomingFilterBuilder builder = new IncomingFilterBuilder(scope, value, vIncoming);
        return builder.build();
    }
}
