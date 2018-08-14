package uz.greenwhite.smartup5_trade.m_outlet.bean;// 24.10.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletRecomProduct {

    public final String outletId;
    public final MyArray<RecomProduct> recomProducts;

    public OutletRecomProduct(String outletId, MyArray<RecomProduct> recomProducts) {
        this.outletId = outletId;
        this.recomProducts = recomProducts;
    }

    public static final MyMapper<OutletRecomProduct, String> KEY_ADAPTER = new MyMapper<OutletRecomProduct, String>() {
        @Override
        public String apply(OutletRecomProduct val) {
            return val.outletId;
        }
    };

    public static final UzumAdapter<OutletRecomProduct> UZUM_ADAPTER = new UzumAdapter<OutletRecomProduct>() {
        @Override
        public OutletRecomProduct read(UzumReader in) {
            return new OutletRecomProduct(in.readString(), in.readArray(RecomProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OutletRecomProduct val) {
            out.write(val.outletId);
            out.write(val.recomProducts, RecomProduct.UZUM_ADAPTER);
        }
    };
}
