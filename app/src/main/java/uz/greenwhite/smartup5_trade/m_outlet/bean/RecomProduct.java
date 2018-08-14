package uz.greenwhite.smartup5_trade.m_outlet.bean;// 22.11.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RecomProduct {

    public final String productId;
    public final String recomData;
    public final String lookUpDay;

    public RecomProduct(String productId, String recomData, String lookUpDay) {
        this.productId = productId;
        this.recomData = recomData;
        this.lookUpDay = lookUpDay;
    }

    public static final MyMapper<RecomProduct, String> KEY_ADAPTER = new MyMapper<RecomProduct, String>() {
        @Override
        public String apply(RecomProduct val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<RecomProduct> UZUM_ADAPTER = new UzumAdapter<RecomProduct>() {
        @Override
        public RecomProduct read(UzumReader in) {
            return new RecomProduct(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, RecomProduct val) {
            out.write(val.productId);
            out.write(val.recomData);
            out.write(val.lookUpDay);
        }
    };
}
