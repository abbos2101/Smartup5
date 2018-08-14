package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealFilterValue {

    public final MyArray<AgreeFilterValue> agrees;
    public final MyArray<OrderFilterValue> orders;
    public final MyArray<StockFilterValue> stocks;
    public final MyArray<GiftFilterValue> gifts;
    public final MyArray<RetailAuditFilterValue> retailAudits;

    public DealFilterValue(MyArray<AgreeFilterValue> agrees,
                           MyArray<OrderFilterValue> orders,
                           MyArray<StockFilterValue> stocks,
                           MyArray<GiftFilterValue> gifts,
                           MyArray<RetailAuditFilterValue> retailAudits) {
        this.agrees = MyArray.nvl(agrees);
        this.orders = MyArray.nvl(orders);
        this.stocks = MyArray.nvl(stocks);
        this.gifts = MyArray.nvl(gifts);
        this.retailAudits = MyArray.nvl(retailAudits);
    }

    public static final DealFilterValue DEFAULT = new DealFilterValue(null, null, null, null, null);

    public static final UzumAdapter<DealFilterValue> UZUM_ADAPTER = new UzumAdapter<DealFilterValue>() {
        @Override
        public DealFilterValue read(UzumReader in) {
            return new DealFilterValue(
                    in.readArray(AgreeFilterValue.UZUM_ADAPTER),
                    in.readArray(OrderFilterValue.UZUM_ADAPTER),
                    in.readArray(StockFilterValue.UZUM_ADAPTER),
                    in.readArray(GiftFilterValue.UZUM_ADAPTER),
                    in.readArray(RetailAuditFilterValue.UZUM_ADAPTER)
            );
        }

        @Override
        public void write(UzumWriter out, DealFilterValue val) {
            out.write(val.agrees, AgreeFilterValue.UZUM_ADAPTER);
            out.write(val.orders, OrderFilterValue.UZUM_ADAPTER);
            out.write(val.stocks, StockFilterValue.UZUM_ADAPTER);
            out.write(val.gifts, GiftFilterValue.UZUM_ADAPTER);
            out.write(val.retailAudits, RetailAuditFilterValue.UZUM_ADAPTER);
        }
    };
}
