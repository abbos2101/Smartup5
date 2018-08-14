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
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGift;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftModule;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class GiftFilterBuilder {

    final GiftFilterValue value;
    final VDeal vDeal;

    public GiftFilterBuilder(GiftFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }

    public GiftFilter build() {
        ProductFilter product = makeProductFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));

        return new GiftFilter(value.formCode, product, hasValue);
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

        VDealGiftForm form = vDeal.findForm(value.formCode);

        MyArray<Product> products = form.gifts.getItems().map(new MyMapper<VDealGift, Product>() {
            @Override
            public Product apply(VDealGift s) {
                return s.product;
            }
        });

        ProductFilterBuilder builder = new ProductFilterBuilder(
                value.product, products, productGroups, productTypes,
                productBarcode, productPhotos, productFiles, mmlProductIds);

        return builder.build();
    }


    public static MyArray<GiftFilter> build(final VDeal vDeal, final MyArray<GiftFilterValue> values) {
        MyArray<VDealModule> modules = vDeal.modules.getItems();
        VDealGiftModule module = (VDealGiftModule) modules.find(VisitModule.M_GIFT, VDealModule.KEY_ADAPTER);
        if (module == null) {
            return MyArray.emptyArray();
        }
        return module.forms.getItems().map(new MyMapper<VDealGiftForm, GiftFilter>() {
            @Override
            public GiftFilter apply(VDealGiftForm form) {
                GiftFilterValue value = values.find(form.code, GiftFilterValue.KEY_ADAPTER);
                if (value == null) {
                    value = GiftFilterValue.makeDefault(form.code);
                }
                GiftFilterBuilder builder = new GiftFilterBuilder(value, vDeal);
                return builder.build();
            }
        });
    }
}
