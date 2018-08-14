package uz.greenwhite.smartup5_trade.m_deal.bean;// 01.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CardQuantity {

    public final String cardCode;
    public final BigDecimal quantity;

    public CardQuantity(String cardCode, BigDecimal quantity) {
        this.cardCode = cardCode;
        this.quantity = quantity;
    }

    public static final MyMapper<CardQuantity, String> KEY_ADAPTER = new MyMapper<CardQuantity, String>() {
        @Override
        public String apply(CardQuantity val) {
            return val.cardCode;
        }
    };

    public static final UzumAdapter<CardQuantity> UZUM_ADAPTER = new UzumAdapter<CardQuantity>() {
        @Override
        public CardQuantity read(UzumReader in) {
            return new CardQuantity(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, CardQuantity val) {
            out.write(val.cardCode);
            out.write(val.quantity);
        }
    };
}
