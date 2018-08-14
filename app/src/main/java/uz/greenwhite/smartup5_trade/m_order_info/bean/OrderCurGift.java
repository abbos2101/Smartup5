package uz.greenwhite.smartup5_trade.m_order_info.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderCurGift {

    public final String productName;
    public final String solidQuant;
    public final String deliverQuant;
    public final String warehouseName;


    public OrderCurGift(String productName, String solidQuant, String deliverQuant, String warehouseName) {
        this.productName = productName;
        this.solidQuant = solidQuant;
        this.deliverQuant = deliverQuant;
        this.warehouseName = warehouseName;
    }

    public static final UzumAdapter<OrderCurGift> UZUM_ADAPTER = new UzumAdapter<OrderCurGift>() {
        @Override
        public OrderCurGift read(UzumReader in) {
            return new OrderCurGift(in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderCurGift val) {
            out.write(val.productName);
            out.write(val.solidQuant);
            out.write(val.deliverQuant);
            out.write(val.warehouseName);
        }
    };
}
