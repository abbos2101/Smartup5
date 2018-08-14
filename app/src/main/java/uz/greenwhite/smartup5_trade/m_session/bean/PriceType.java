package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PriceType {

    public final String id;
    public final String name;
    public final String currencyId;
    public final boolean withCard;
    public final MyArray<String> paymentTypeIds;

    public PriceType(String id, String name, String currencyId, boolean withCard, MyArray<String> paymentTypeIds) {
        this.id = id;
        this.name = name;
        this.currencyId = Util.nvl(currencyId);
        this.withCard = withCard;
        this.paymentTypeIds = MyArray.nvl(paymentTypeIds);
    }

    public static final MyMapper<PriceType, String> KEY_ADAPTER = new MyMapper<PriceType, String>() {
        @Override
        public String apply(PriceType val) {
            return val.id;
        }
    };

    public static final UzumAdapter<PriceType> UZUM_ADAPTER = new UzumAdapter<PriceType>() {
        @Override
        public PriceType read(UzumReader in) {
            return new PriceType(in.readString(),
                    in.readString(), in.readString(),
                    in.readBoolean(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, PriceType val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.currencyId);
            out.write(val.withCard);
            out.write(val.paymentTypeIds, STRING_ARRAY);
        }
    };
}
