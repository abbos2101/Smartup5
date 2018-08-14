package uz.greenwhite.smartup5_trade.m_shipped.variable.order;// 09.09.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSDealOrderForm extends VSDealForm {

    public final Warehouse warehouse;
    public final PriceType priceType;
    public final Currency currency;
    public final ValueArray<VSDealOrder> orders;
    public VSDealOrderModule orderModule;

    public VSDealOrderForm(VisitModule module,
                           Warehouse warehouse,
                           PriceType priceType,
                           Currency currency,
                           ValueArray<VSDealOrder> orders) {
        super(module, "" + module.id + ":" + warehouse.id + ":" + priceType.id);
        this.warehouse = warehouse;
        this.priceType = priceType;
        this.currency = Util.nvl(currency, Currency.DEFAULT);
        this.orders = orders;
    }

    @Override
    public CharSequence getTitle() {
        return UI.html().v(warehouse.name).html();
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

    public BigDecimal getTotalQuantity() {
        return orders.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VSDealOrder>() {
            @Override
            public BigDecimal apply(BigDecimal acc, VSDealOrder v) {
                return acc.add(v.getQuantity());
            }
        });
    }

    public BigDecimal getTotalSum() {
        return orders.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VSDealOrder>() {
            @Override
            public BigDecimal apply(BigDecimal result, VSDealOrder val) {
                return result.add(val.getTotalSum());
            }
        });
    }

    @Override
    public boolean hasValue() {
        if (orders != null) {
            for (VSDealOrder dealProduct : orders.getItems()) {
                if (dealProduct.hasValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasReturn() {
        if (orders != null) {
            for (VSDealOrder dealProduct : orders.getItems()) {
                if (dealProduct.hasReturn()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void makeOrderQuant() {
        for (final VSDealOrder order : orders.getItems()) {
            order.otherOrdersQuantity = BigDecimal.ZERO;

            for (VSDealOrderForm orderForm : orderModule.forms.getItems()) {

                if (orderForm.warehouse.id.equals(this.warehouse.id) &&
                        !orderForm.priceType.id.equals(this.priceType.id)) {

                    MyArray<VSDealOrder> filterOrders = orderForm.orders.getItems().filter(new MyPredicate<VSDealOrder>() {
                        @Override
                        public boolean apply(VSDealOrder o2) {
                            boolean productEqual = order.product.id.equals(o2.product.id);
                            if (!priceType.withCard) {
                                return productEqual;
                            }
                            return productEqual &&
                                    order.order.cardCode.equals(o2.order.cardCode);
                        }
                    });
                    if (filterOrders.nonEmpty()) {
                        BigDecimal result = filterOrders.reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VSDealOrder>() {
                            @Override
                            public BigDecimal apply(BigDecimal result, VSDealOrder val) {
                                BigDecimal subtractQuant = val.order.originQuant.subtract(val.deliverQuant.getQuantity());
                                return result.add(val.returnQuant.getQuantity().add(subtractQuant));
                            }
                        });
                        order.otherOrdersQuantity = order.otherOrdersQuantity.add(result);
                    }
                }
            }
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(this.orders);
    }
}
