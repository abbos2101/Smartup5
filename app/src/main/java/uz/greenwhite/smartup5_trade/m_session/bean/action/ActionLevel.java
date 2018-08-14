package uz.greenwhite.smartup5_trade.m_session.bean.action;// 07.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ActionLevel {

    public final BigDecimal min;
    public final BigDecimal max;
    public final MyArray<LevelProduct> products;

    public ActionLevel(BigDecimal min, BigDecimal max, MyArray<LevelProduct> products) {
        this.min = min;
        this.max = max;
        this.products = products;
    }

    public static final UzumAdapter<ActionLevel> UZUM_ADAPTER = new UzumAdapter<ActionLevel>() {
        @Override
        public ActionLevel read(UzumReader in) {
            return new ActionLevel(in.readBigDecimal(), in.readBigDecimal(), in.readArray(LevelProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, ActionLevel val) {
            out.write(val.min);
            out.write(val.max);
            out.write(val.products, LevelProduct.UZUM_ADAPTER);
        }
    };
}
