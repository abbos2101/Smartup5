package uz.greenwhite.smartup5_trade.m_order_info.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderHeader {

    public final String name;
    public final String address;
    public final String category;
    public final String amount;
    public final String userName;
    public final String visitPeriod;
    public final String memo;

    public OrderHeader(String name,
                       String address,
                       String category,
                       String amount,
                       String userName,
                       String visitPeriod,
                       String memo) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.userName = userName;
        this.visitPeriod = visitPeriod;
        this.memo = memo;
    }

    public static final UzumAdapter<OrderHeader> UZUM_ADAPTER = new UzumAdapter<OrderHeader>() {
        @Override
        public OrderHeader read(UzumReader in) {
            return new OrderHeader(in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderHeader val) {
            out.write(val.name);
            out.write(val.address);
            out.write(val.category);
            out.write(val.amount);
            out.write(val.userName);
            out.write(val.visitPeriod);
            out.write(val.memo);
        }
    };
}
