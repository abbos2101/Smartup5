package uz.greenwhite.smartup5_trade.m_order_info.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderInfo {

    public final OrderHeader header;
    public final OrderForm form;

    public OrderInfo(OrderHeader header, OrderForm form) {
        this.header = header;
        this.form = form;
    }

    public static final UzumAdapter<OrderInfo> UZUM_ADAPTER = new UzumAdapter<OrderInfo>() {
        @Override
        public OrderInfo read(UzumReader in) {
            return new OrderInfo(in.readValue(OrderHeader.UZUM_ADAPTER), in.readValue(OrderForm.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OrderInfo val) {
            out.write(val.header, OrderHeader.UZUM_ADAPTER);
            out.write(val.form, OrderForm.UZUM_ADAPTER);
        }
    };
}
