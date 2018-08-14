package uz.greenwhite.smartup5_trade.m_deal.filter;

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
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class RetailAuditFilterBuilder {

    final RetailAuditFilterValue value;
    final VDeal vDeal;

    public RetailAuditFilterBuilder(RetailAuditFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }


    public RetailAuditFilter build() {
        ProductFilter product = makeProductFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));

        return new RetailAuditFilter(value.formCode, product, hasValue);
    }

    private ProductFilter makeProductFilter() {
        MyArray<ProductGroup> productGroups = vDeal.dealRef.getProductGroups();
        MyArray<ProductType> productTypes = vDeal.dealRef.getProductTypes();
        MyArray<ProductBarcode> productBarcode = vDeal.dealRef.getProductBarcode();
        MyArray<ProductPhoto> productPhotos = vDeal.dealRef.getProductPhotos();
        MyArray<ProductFile> productFiles = vDeal.dealRef.getProductFiles();

        VDealRetailAuditForm form = vDeal.findForm(value.formCode);

        MyArray<Product> products = form.retailAudits.getItems().map(new MyMapper<VDealRetailAudit, Product>() {
            @Override
            public Product apply(VDealRetailAudit item) {
                return item.product;
            }
        });

        ProductFilterBuilder builder = new ProductFilterBuilder(
                value.product, products, productGroups, productTypes,
                productBarcode, productPhotos, productFiles, MyArray.<String>emptyArray());

        return builder.build();
    }

    public static MyArray<RetailAuditFilter> build(final VDeal vDeal, final MyArray<RetailAuditFilterValue> values) {
        MyArray<VDealModule> modules = vDeal.modules.getItems();
        VDealRetailAuditModule module = (VDealRetailAuditModule) modules.find(VisitModule.M_RETAIL_AUDIT, VDealModule.KEY_ADAPTER);
        if (module == null) {
            return MyArray.emptyArray();
        }
        MyArray<VDealRetailAuditForm> forms = MyArray.from(module.forms.getItems().get(0));
        return forms.map(new MyMapper<VDealRetailAuditForm, RetailAuditFilter>() {
            @Override
            public RetailAuditFilter apply(VDealRetailAuditForm form) {
                RetailAuditFilterValue value = values.find(form.code, RetailAuditFilterValue.KEY_ADAPTER);
                if (value == null) {
                    value = RetailAuditFilterValue.makeDefault(form.code, false); //vDeal.dealRef.isMhlDefaultFilter
                }
                RetailAuditFilterBuilder builder = new RetailAuditFilterBuilder(value, vDeal);
                return builder.build();
            }
        });
    }
}