package uz.greenwhite.smartup5_trade.m_deal.builder;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealService;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealServiceModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealService;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderService {

    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<DealService> initial;

    public BuilderService(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private MyArray<DealService> getInitial() {
        DealServiceModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.items : MyArray.<DealService>emptyArray();
    }

    private MyArray<Product> getServiceIds() {
        Set<String> productIds = initial.map(new MyMapper<DealService, String>() {
            @Override
            public String apply(DealService val) {
                return val.productId;
            }
        }).asSet();

        Set<String> serviceIds = dealRef.filialSetting.serviceIds.asSet();
        return MyArray.from(serviceIds).union(MyArray.from(productIds))
                .map(new MyMapper<String, Product>() {
                    @Override
                    public Product apply(String s) {
                        return dealRef.findProduct(s);
                    }
                }).filterNotNull();
    }

    private MyArray<SpinnerOption> makeDiscountOptions(MyArray<Margin> discounts) {
        return discounts.map(new MyMapper<Margin, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Margin d) {
                return new SpinnerOption(d.id, d.name, d.percent);
            }
        });
    }

    private ValueSpinner makeDiscountForm(MyArray<SpinnerOption> discounts, MyArray<VDealService> services) {
        MyArray<SpinnerOption> result = discounts;
        boolean isOrderContainDiscount = false;

        if (result.isEmpty()) {
            isOrderContainDiscount = services.contains(new MyPredicate<VDealService>() {
                @Override
                public boolean apply(VDealService vDealOrder) {
                    return vDealOrder.margin.nonZero();
                }
            });
        }

        ValueSpinner discountSpinner = null;
        if (result.nonEmpty() || isOrderContainDiscount) {
            result = result.append(new SpinnerOption(0, DS.getString(R.string.deal_remove_margin), BigDecimal.ZERO));
            discountSpinner = new ValueSpinner(result);
        }
        return discountSpinner;
    }

    @Nullable
    private ValueSpinner makeMarginSpinner(MyArray<Margin> margins, BigDecimal discount) {
        MyArray<SpinnerOption> discountOptions = makeDiscountOptions(margins);
        boolean isOrderContainDiscount = false;
        if (discountOptions.isEmpty()) {
            isOrderContainDiscount = discount != null &&
                    discount.compareTo(BigDecimal.ZERO) != 0;
        }
        ValueSpinner discountSpinner = null;
        if (discountOptions.nonEmpty() || isOrderContainDiscount) {
            discountOptions = discountOptions.prepend(
                    new SpinnerOption(0, DS.getString(R.string.deal_remove_margin), BigDecimal.ZERO)
            );
            discountSpinner = new ValueSpinner(discountOptions);
        }
        return discountSpinner;
    }

    private MyArray<VDealService> makeService(final PriceType priceType,
                                              MyArray<Product> services,
                                              final MyArray<ProductPrice> prices,
                                              final MyArray<Margin> margins) {
        return services.map(new MyMapper<Product, VDealService>() {
            @Override
            public VDealService apply(Product service) {
                Tuple3 priceKey = ProductPrice.getKey(priceType.id, service.id, "");
                ProductPrice price = prices.find(priceKey, ProductPrice.KEY_ADAPTER);

                BigDecimal realPrice = price != null ? price.price : null;
                BigDecimal quantity = null;
                BigDecimal margin = null;
                String productUnitId = "";

                Tuple2 initKey = DealService.getKey(service.id, priceType.id);
                DealService dealService = initial.find(initKey, DealService.KEY_ADAPTER);
                if (dealService != null) {
                    realPrice = dealService.price;
                    productUnitId = dealService.productUnitId;
                    quantity = dealService.quantity;
                    margin = dealService.margin;
                }

                if (realPrice == null) return null;

                ValueSpinner marginSpinner = makeMarginSpinner(margins, null);
                return new VDealService(service, productUnitId, dealRef.filial.roundModel, realPrice,
                        quantity, margin, marginSpinner);
            }
        }).filterNotNull();
    }

    private ValueArray<VDealServiceForm> makeForms() {
        final MyArray<Margin> margins = dealRef.getMargins();
        final MyArray<SpinnerOption> discountOptions = makeDiscountOptions(margins);
        final MyArray<ProductPrice> prices = dealRef.getProductPrices();
        final MyArray<Product> services = getServiceIds();
        MyArray<String> priceTypeIds = dealRef.getPriceTypeIds();

        ArrayList<VDealServiceForm> form = new ArrayList<>();
        for (String id : priceTypeIds) {
            PriceType priceType = dealRef.getPriceType(id);
            if (priceType == null || priceType.withCard) continue;
            Currency currency = dealRef.getCurrency(priceType.currencyId);
            if (currency == null) continue;
            MyArray<VDealService> items = makeService(priceType, services, prices, margins);
            if (items.isEmpty()) continue;
            ValueSpinner formMargin = makeDiscountForm(discountOptions, items);
            form.add(new VDealServiceForm(module, priceType, currency,
                    new ValueArray<>(items), formMargin));
        }

        return new ValueArray<>(MyArray.from(form));
    }

    public VDealServiceModule build() {
        return new VDealServiceModule(module, makeForms());
    }

}
