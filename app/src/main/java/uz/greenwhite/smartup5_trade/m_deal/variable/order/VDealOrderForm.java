package uz.greenwhite.smartup5_trade.m_deal.variable.order;// 30.06.2016


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSimilar;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;

public class VDealOrderForm extends VDealForm {

    private final DealRef dealRef;
    public final Warehouse warehouse;
    public final PriceType priceType;
    public final Currency currency;
    public final ValueArray<VDealOrder> orders;
    public final ValueSpinner discount;
    public final PriceEditable priceEditable;

    public final MyArray<Product> products;
    public final MyArray<ProductSimilar> productSimilars;


    public VDealOrderForm(DealRef dealRef,
                          VisitModule module,
                          Warehouse warehouse,
                          PriceType priceType,
                          Currency currency,
                          ValueArray<VDealOrder> orders,
                          ValueSpinner discount,
                          PriceEditable priceEditable,
                          MyArray<Product> products,
                          MyArray<ProductSimilar> productSimilars) {
        super(module, "" + module.id + ":" + warehouse.id + ":" + priceType.id);
        this.dealRef = dealRef;
        this.orders = orders;
        this.priceType = priceType;
        this.currency = Util.nvl(currency, Currency.DEFAULT);
        this.warehouse = warehouse;
        this.discount = discount;
        this.priceEditable = priceEditable;

        this.products = products;
        this.productSimilars = productSimilars;
    }

    @Override
    public CharSequence getTitle() {
        return UI.html().v(warehouse.name).html();
    }

    @Override
    public CharSequence getSubtitle() {
        return priceType.name;
    }

    @Override
    public CharSequence getDetail() {
        ErrorResult error = getError();
        ShortHtml html = UI.html();

        if (error.isError()) {
            html.fRed();
        } else if (hasValue()) {
            html.c("#09a667");
        }

        html.v(DS.getString(R.string.deal_form_price_type)).i().v(priceType.name).i().br();
        html.v(DS.getString(R.string.deal_form_currency)).i().v(currency.getName()).i();

        if (error.isError()) {
            html.br().v(error.getErrorMessage());
            html.fRed();
        } else if (hasValue()) {
            html.c();
        }
        return html.html();

    }

    public MyArray<BigDecimal> getOrderHeaderInfo() {
        MyArray<VDealOrder> items = orders.getItems();
        Set<String> sku = new HashSet<>();
        int position = 0;
        BigDecimal totalSum = BigDecimal.ZERO;
        BigDecimal totalCount = BigDecimal.ZERO;
        for (VDealOrder o : items) {
            if (o.hasValue()) {
                position++;
                sku.add(o.product.id);
                totalSum = totalSum.add(o.getTotalPrice());
                totalCount = totalCount.add(o.getQuantity());
            }
        }
        return MyArray.from(
                new BigDecimal(position),
                new BigDecimal(sku.size()),
                totalSum,
                totalCount);
    }

    public BigDecimal getTotalPrice() {
        return orders.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealOrder>() {
            @Override
            public BigDecimal apply(BigDecimal result, VDealOrder vDealOrder) {
                return result.add(vDealOrder.getTotalPrice());
            }
        });
    }

    public void clearOrder() {
        for (VDealOrder o : orders.getItems()) {
            o.setQuantity(BigDecimal.ZERO);
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return orders.getItems().toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        if (dealRef.violationBans.nonEmpty()) {
            BigDecimal totalPrice = getTotalPrice();
            for (Ban b : dealRef.violationBans) {
                if (Ban.K_PRICE_TYPE.equals(b.kind) &&
                        b.kindSourceIds.contains(priceType.id, MyMapper.<String>identity()) &&
                        b.kindValue.intValue() < totalPrice.intValue()) {
                    return ErrorResult.make(DS.getString(R.string.deal_ban_error_1, b.kindValue.toPlainString()));
                } else if (Ban.K_PREPAYMENT.equals(b.kind)) {
                    boolean contains = new BigDecimal("-1").multiply(b.kindValue).intValue() > dealRef.lastDealBalance.subtract(totalPrice).intValue();
                    if (contains) {
                        return ErrorResult.make(DS.getString(R.string.deal_ban_error_2, b.kindValue.toPlainString()));
                    }
                }
            }
        }
        return ErrorResult.NONE;
    }

    @Override
    public boolean hasValue() {
        for (VDealOrder order : orders.getItems()) {
            if (order.hasValue()) {
                return true;
            }
        }
        return false;
    }

}

