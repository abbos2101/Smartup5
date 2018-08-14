package uz.greenwhite.smartup5_trade.m_deal.filter;

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
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class OrderFilterBuilder {

    final OrderFilterValue value;
    final VDeal vDeal;

    public OrderFilterBuilder(OrderFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }

    public OrderFilter build() {
        ProductFilter product = makeProductFilter();
        FilterSpinner productCardCode = getProductCardCodeFilter();
        FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));
        FilterBoolean hasDiscount = new FilterBoolean(DS.getString(R.string.deal_filter_has_discount), new ValueBoolean(value.hasDiscount));
        FilterBoolean warehouseAvail = new FilterBoolean(DS.getString(R.string.deal_filter_warehouse_avail), new ValueBoolean(value.warehouseAvail));
        FilterBoolean sortFirstMll = new FilterBoolean(DS.getString(R.string.deal_filter_sort_first_mll), new ValueBoolean(value.sortFirstMll));

        return new OrderFilter(value.formCode, product, productCardCode, hasValue, hasDiscount, warehouseAvail, sortFirstMll);
    }

    private FilterSpinner getProductCardCodeFilter() {
        VDealOrderForm form = vDeal.findForm(VisitModule.M_ORDER, value.formCode);
        if (!form.priceType.withCard) {
            return null;
        }
        Set<String> cardCodes = new HashSet<>();
        for (VDealOrder order : form.orders.getItems()) {
            cardCodes.add(order.price.cardCode);
        }
        MyArray<SpinnerOption> options = MyArray.from(cardCodes).map(new MyMapper<String, SpinnerOption>() {
            @Override
            public SpinnerOption apply(String s) {
                return new SpinnerOption(s, s);
            }
        });
        return FilterSpinner.build(DS.getString(R.string.deal_card_number), value.cardCode, options);
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

        VDealOrderForm form = vDeal.findForm(value.formCode);
        ProductFilterBuilder builder = new ProductFilterBuilder(value.product,
                form.orders.getItems().map(new MyMapper<VDealOrder, Product>() {
                    @Override
                    public Product apply(VDealOrder o) {
                        return o.product;
                    }
                }),
                productGroups, productTypes, productBarcode, productPhotos, productFiles, mmlProductIds);

        return builder.build();
    }


    public static MyArray<OrderFilter> build(final VDeal vDeal, final MyArray<OrderFilterValue> values) {
        MyArray<VDealModule> modules = vDeal.modules.getItems();
        VDealOrderModule module = (VDealOrderModule) modules.find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        if (module == null) {
            return MyArray.emptyArray();
        }
        MyArray<VDealOrderForm> forms = module.orderForms.getItems();
        final Boolean warehouseAvail = !vDeal.dealRef.setting.deal.allowDealDraft;

        return forms.map(new MyMapper<VDealOrderForm, OrderFilter>() {
            @Override
            public OrderFilter apply(VDealOrderForm form) {
                OrderFilterValue value = values.find(form.code, OrderFilterValue.KEY_ADAPTER);
                if (value == null) {
                    value = OrderFilterValue.makeDefault(form.code, warehouseAvail);
                }
                OrderFilterBuilder builder = new OrderFilterBuilder(value, vDeal);
                return builder.build();
            }
        });
    }

}
