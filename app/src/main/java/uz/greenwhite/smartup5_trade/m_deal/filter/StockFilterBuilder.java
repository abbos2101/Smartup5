package uz.greenwhite.smartup5_trade.m_deal.filter;

import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterBuilder;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class StockFilterBuilder {

    final StockFilterValue value;
    final VDeal vDeal;

    public StockFilterBuilder(StockFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }

    public StockFilter build() {
        ProductFilter product = makeProductFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));

        return new StockFilter(value.formCode, product, hasValue);
    }

    private MyArray<String> getMmlProductIds() {
        Set<String> productIds = vDeal.dealRef.getMmlPersonTypeProducts()
                .map(new MyMapper<MmlPersonTypeProduct, String>() {
                    @Override
                    public String apply(MmlPersonTypeProduct mmlPersonTypeProduct) {
                        return mmlPersonTypeProduct.productId;
                    }
                }).asSet();
        return MyArray.from(productIds);
    }

    private ProductFilter makeProductFilter() {
        MyArray<ProductGroup> productGroups = vDeal.dealRef.getProductGroups();
        MyArray<ProductType> productTypes = vDeal.dealRef.getProductTypes();
        MyArray<ProductBarcode> productBarcode = vDeal.dealRef.getProductBarcode();
        MyArray<ProductPhoto> productPhotos = vDeal.dealRef.getProductPhotos();
        MyArray<ProductFile> productFiles = vDeal.dealRef.getProductFiles();
        MyArray<String> mmlProductIds = getMmlProductIds();

        VDealStockForm form = vDeal.findForm(value.formCode);

        MyArray<Product> products = form.stockProducts.getItems().map(new MyMapper<VDealStockProduct, Product>() {
            @Override
            public Product apply(VDealStockProduct s) {
                return s.product;
            }
        });

        ProductFilterBuilder builder = new ProductFilterBuilder(
                value.product, products, productGroups, productTypes,
                productBarcode, productPhotos, productFiles, mmlProductIds);

        return builder.build();
    }


    public static MyArray<StockFilter> build(final VDeal vDeal, final MyArray<StockFilterValue> values) {
        MyArray<VDealModule> modules = vDeal.modules.getItems();
        VDealStockModule module = (VDealStockModule) modules.find(VisitModule.M_STOCK, VDealModule.KEY_ADAPTER);
        if (module == null) {
            return MyArray.emptyArray();
        }
        return MyArray.from(module.form).map(new MyMapper<VDealStockForm, StockFilter>() {
            @Override
            public StockFilter apply(VDealStockForm form) {
                StockFilterValue value = values.find(form.code, StockFilterValue.KEY_ADAPTER);
                if (value == null) {
                    value = StockFilterValue.makeDefault(form.code);
                }
                StockFilterBuilder builder = new StockFilterBuilder(value, vDeal);
                return builder.build();
            }
        });
    }
}
