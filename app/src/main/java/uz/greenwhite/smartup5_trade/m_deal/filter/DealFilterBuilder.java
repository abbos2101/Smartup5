package uz.greenwhite.smartup5_trade.m_deal.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;


public class DealFilterBuilder {

    public final DealFilterValue value;
    public final VDeal vDeal;

    public DealFilterBuilder(DealFilterValue value, VDeal vDeal) {
        this.value = value;
        this.vDeal = vDeal;
    }

    public DealFilter build() {
        return new DealFilter(
                AgreeFilterBuilder.build(vDeal, value.agrees),
                OrderFilterBuilder.build(vDeal, value.orders),
                StockFilterBuilder.build(vDeal, value.stocks),
                GiftFilterBuilder.build(vDeal, value.gifts),
                RetailAuditFilterBuilder.build(vDeal, value.retailAudits)
        );
    }

    public static DealFilter parse(VDeal vDeal, String source) {
        DealFilterValue value = DealFilterValue.DEFAULT;
        if (!TextUtils.isEmpty(source)) {
            value = Uzum.toValue(source, DealFilterValue.UZUM_ADAPTER);
        }
        DealFilterBuilder builder = new DealFilterBuilder(value, vDeal);
        return builder.build();
    }

    public static String stringify(DealFilter dealFilter) {
        String json = "";
        if (dealFilter != null) {
            DealFilterValue value = dealFilter.toValue();
            json = Uzum.toJson(value, DealFilterValue.UZUM_ADAPTER);
        }
        return json;
    }
}