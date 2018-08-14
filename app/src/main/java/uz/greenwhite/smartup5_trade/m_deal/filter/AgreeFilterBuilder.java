package uz.greenwhite.smartup5_trade.m_deal.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterBuilder;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgree;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgreeForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgreeModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class AgreeFilterBuilder {

    final AgreeFilterValue value;
    final VDeal vDeal;

    public AgreeFilterBuilder(AgreeFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }

    public AgreeFilter build() {
        ProductFilter product = makeProductFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));

        return new AgreeFilter(value.formCode, product, hasValue);
    }

    public static AgreeFilter parse(VDeal vDeal, String source) {
        AgreeFilterValue value = AgreeFilterValue.DEFAULT;
        if (!TextUtils.isEmpty(source)) {
            value = Uzum.toValue(source, AgreeFilterValue.UZUM_ADAPTER);
        }
        AgreeFilterBuilder builder = new AgreeFilterBuilder(value, vDeal);
        return builder.build();
    }

    public static String stringify(AgreeFilter dealFilter) {
        String json = "";
        if (dealFilter != null) {
            AgreeFilterValue value = dealFilter.toValue();
            json = Uzum.toJson(value, AgreeFilterValue.UZUM_ADAPTER);
        }
        return json;
    }

    private ProductFilter makeProductFilter() {
        MyArray<ProductGroup> productGroups = vDeal.dealRef.getProductGroups();
        MyArray<ProductType> productTypes = vDeal.dealRef.getProductTypes();
        MyArray<ProductBarcode> productBarcode = vDeal.dealRef.getProductBarcode();
        MyArray<ProductPhoto> productPhotos = vDeal.dealRef.getProductPhotos();
        MyArray<ProductFile> productFiles = vDeal.dealRef.getProductFiles();

        VDealAgreeForm form = vDeal.findForm(value.formCode);

        MyArray<Product> products = form.agrees.getItems().map(new MyMapper<VDealAgree, Product>() {
            @Override
            public Product apply(VDealAgree vDealAgree) {
                return vDealAgree.product;
            }
        });

        ProductFilterBuilder builder = new ProductFilterBuilder(
                value.product, products, productGroups, productTypes,
                productBarcode, productPhotos, productFiles, MyArray.<String>emptyArray());

        return builder.build();
    }

    public static MyArray<AgreeFilter> build(final VDeal vDeal, final MyArray<AgreeFilterValue> values) {
        MyArray<VDealModule> modules = vDeal.modules.getItems();
        VDealAgreeModule module = (VDealAgreeModule) modules.find(VisitModule.M_AGREE, VDealModule.KEY_ADAPTER);
        if (module == null) {
            return MyArray.emptyArray();
        }
        MyArray<VDealAgreeForm> forms = MyArray.from(module.form);
        return forms.map(new MyMapper<VDealAgreeForm, AgreeFilter>() {
            @Override
            public AgreeFilter apply(VDealAgreeForm form) {
                AgreeFilterValue value = values.find(form.code, AgreeFilterValue.KEY_ADAPTER);
                if (value == null) {
                    value = AgreeFilterValue.makeDefault(form.code, false); //vDeal.dealRef.isMhlDefaultFilter
                }
                AgreeFilterBuilder builder = new AgreeFilterBuilder(value, vDeal);
                return builder.build();
            }
        });
    }
}