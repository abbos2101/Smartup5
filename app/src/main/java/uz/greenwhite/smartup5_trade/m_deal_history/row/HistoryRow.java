package uz.greenwhite.smartup5_trade.m_deal_history.row;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealAmount;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory;

public class HistoryRow {

    public final Outlet outlet;
    public final DealHistory history;

    public final String amount;
    public final String currency;

    public HistoryRow(Outlet outlet, DealHistory history, MyArray<Currency> currencies) {
        this.outlet = outlet;
        this.history = history;

        this.amount = getDealAmount(history);
        this.currency = getCurrencyName(history, currencies);
    }

    private String getDealAmount(DealHistory history) {
        return history.dealAmount.map(new MyMapper<DealAmount, String>() {
            @Override
            public String apply(DealAmount dealAmount) {
                return NumberUtil.formatMoney(dealAmount.totalAmount);
            }
        }).mkString("\n");
    }

    private String getCurrencyName(DealHistory history, final MyArray<Currency> currencies) {
        return history.dealAmount.map(new MyMapper<DealAmount, String>() {
            @Override
            public String apply(DealAmount dealAmount) {
                return currencies.find(dealAmount.currencyId, Currency.KEY_ADAPTER).getName();
            }
        }).filterNotNull().mkString("\n");
    }
}
