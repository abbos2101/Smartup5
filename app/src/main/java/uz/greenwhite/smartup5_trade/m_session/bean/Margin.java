package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Margin {

    public final String id;
    public final String name;
    public final BigDecimal percent;

    public Margin(String id, String name, BigDecimal percent) {
        this.id = id;
        this.name = name;
        this.percent = percent;
    }

    public static MyMapper<Margin, String> KEY_ADAPTER = new MyMapper<Margin, String>() {
        @Override
        public String apply(Margin margin) {
            return margin.id;
        }
    };

    public static final UzumAdapter<Margin> UZUM_ADAPTER = new UzumAdapter<Margin>() {
        @Override
        public Margin read(UzumReader in) {
            return new Margin(in.readString(), in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, Margin val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.percent);
        }
    };
}
