package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PaymentType {

    public static final String K_BANK = "B";
    public static final String K_CARD = "R";
    public static final String K_CASH = "C";

    public final String id;
    public final String name;
    public final String orderNo;
    public final String currencyId;
    public final String kind;

    public PaymentType(String id, String name, String orderNo, String currencyId, String kind) {
        this.id = id;
        this.name = name;
        this.orderNo = orderNo;
        this.currencyId = Util.nvl(currencyId);
        this.kind = Util.nvl(kind);
    }

    public static final MyMapper<PaymentType, String> KEY_ADAPTER = new MyMapper<PaymentType, String>() {
        @Override
        public String apply(PaymentType paymentType) {
            return paymentType.id;
        }
    };

    public static final UzumAdapter<PaymentType> UZUM_ADAPTER = new UzumAdapter<PaymentType>() {
        @Override
        public PaymentType read(UzumReader in) {
            return new PaymentType(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PaymentType val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.orderNo);
            out.write(val.currencyId);
            out.write(val.kind);
        }
    };
}
