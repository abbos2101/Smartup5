package uz.greenwhite.smartup5_trade.m_duty.filter;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterBuilder;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;

public class PriceFilterBuilder {

    public final PriceFilterValue value;
    final MyArray<ProductGroup> productGroups;
    final MyArray<ProductType> productTypes;
    private MyArray<Product> products;

    public PriceFilterBuilder(PriceFilterValue value,
                              MyArray<Product> products,
                              MyArray<ProductGroup> productGroups,
                              MyArray<ProductType> productTypes) {
        this.value = value;
        this.productGroups = productGroups;
        this.productTypes = productTypes;
        this.products = products;
    }


    public PriceFilter build() {
        ProductFilter product = makeProductFilter();
        return new PriceFilter(product);
    }

    private ProductFilter makeProductFilter() {
        ProductFilterBuilder builder = new ProductFilterBuilder
                (value.product, products, productGroups, productTypes,
                        MyArray.<ProductBarcode>emptyArray(),
                        MyArray.<ProductPhoto>emptyArray(),
                        MyArray.<ProductFile>emptyArray(),
                        MyArray.<String>emptyArray());

        return builder.build();
    }

}