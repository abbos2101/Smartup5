package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;

public class DealFilter {

    public final MyArray<AgreeFilter> agrees;
    public final MyArray<OrderFilter> orders;
    public final MyArray<StockFilter> stocks;
    public final MyArray<GiftFilter> gifts;
    public final MyArray<RetailAuditFilter> retailAudits;

    public DealFilter(MyArray<AgreeFilter> agrees,
                      MyArray<OrderFilter> orders,
                      MyArray<StockFilter> stocks,
                      MyArray<GiftFilter> gifts,
                      MyArray<RetailAuditFilter> retailAudits) {
        this.agrees = MyArray.nvl(agrees);
        this.orders = MyArray.nvl(orders);
        this.stocks = MyArray.nvl(stocks);
        this.gifts = MyArray.nvl(gifts);
        this.retailAudits = MyArray.nvl(retailAudits);
    }

    public DealFilterValue toValue() {
        MyArray<OrderFilterValue> orderValues = orders.map(new MyMapper<OrderFilter, OrderFilterValue>() {
            @Override
            public OrderFilterValue apply(OrderFilter filter) {
                return filter.toValue();
            }
        });

        MyArray<StockFilterValue> stockValues = stocks.map(new MyMapper<StockFilter, StockFilterValue>() {
            @Override
            public StockFilterValue apply(StockFilter filter) {
                return filter.toValue();
            }
        });

        MyArray<GiftFilterValue> giftValues = gifts.map(new MyMapper<GiftFilter, GiftFilterValue>() {
            @Override
            public GiftFilterValue apply(GiftFilter filter) {
                return filter.toValue();
            }
        });

        MyArray<AgreeFilterValue> agreeValues = agrees.map(new MyMapper<AgreeFilter, AgreeFilterValue>() {
            @Override
            public AgreeFilterValue apply(AgreeFilter filter) {
                return filter.toValue();
            }
        });

        MyArray<RetailAuditFilterValue> retailAuditValue = retailAudits.map(new MyMapper<RetailAuditFilter, RetailAuditFilterValue>() {
            @Override
            public RetailAuditFilterValue apply(RetailAuditFilter filter) {
                return filter.toValue();
            }
        });
        return new DealFilterValue(agreeValues, orderValues, stockValues, giftValues, retailAuditValue);
    }

    public AgreeFilter findAgree(String formCode) {
        return agrees.find(formCode, AgreeFilter.KEY_ADAPTER);
    }

    public OrderFilter findOrder(String formCode) {
        return orders.find(formCode, OrderFilter.KEY_ADAPTER);
    }

    public StockFilter findStock(String formCode) {
        return stocks.find(formCode, StockFilter.KEY_ADAPTER);
    }

    public GiftFilter findGift(String formCode) {
        return gifts.find(formCode, GiftFilter.KEY_ADAPTER);
    }

    public RetailAuditFilter findRetailAudit(String formCode) {
        return retailAudits.find(formCode, RetailAuditFilter.KEY_ADAPTER);
    }
}
