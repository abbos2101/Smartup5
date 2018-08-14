package uz.greenwhite.smartup5_trade.m_deal.variable.service;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealServiceForm extends VDealForm {

    public final PriceType priceType;
    public final Currency currency;
    public final ValueArray<VDealService> services;
    public final ValueSpinner discount;

    public VDealServiceForm(VisitModule module,
                            PriceType priceType,
                            Currency currency,
                            ValueArray<VDealService> services,
                            ValueSpinner discount) {
        super(module, "" + module.id + ":" + priceType.id);
        this.priceType = priceType;
        this.currency = currency;
        this.services = services;
        this.discount = discount;
    }

    @Override
    public CharSequence getTitle() {
        return UI.html().v(priceType.name).html();
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

    public BigDecimal getTotalPrice() {
        return services.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealService>() {
            @Override
            public BigDecimal apply(BigDecimal result, VDealService val) {
                return result.add(val.getTotalPrice());
            }
        });
    }

    public BigDecimal getTotalCount() {
        return services.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealService>() {
            @Override
            public BigDecimal apply(BigDecimal r, VDealService v) {
                return r.add(v.quant.getQuantity());
            }
        });
    }

    @Override
    public boolean hasValue() {
        for (VDealService v : services.getItems()) {
            if (v.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return services.getItems().toSuper();
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence tvHeaderTotalQuantity() {
        return UI.html().v(DS.getString(R.string.deal_service_count,
                NumberUtil.formatMoney(getTotalCount()))).html();
    }

    public CharSequence tvHeaderTotalAmount() {
        return UI.html().v(DS.getString(R.string.deal_service_sum,
                NumberUtil.formatMoney(getTotalPrice()))).html();
    }
}
