package uz.greenwhite.smartup5_trade.m_session.bean.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OverloadLoad {

    public final String loadId;
    public final MyArray<OverloadProduct> products;

    public OverloadLoad(String loadId, MyArray<OverloadProduct> products) {
        this.loadId = loadId;
        this.products = products;
    }

    public static final UzumAdapter<OverloadLoad> UZUM_ADAPTER = new UzumAdapter<OverloadLoad>() {
        @Override
        public OverloadLoad read(UzumReader in) {
            return new OverloadLoad(in.readString(), in.readArray(OverloadProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OverloadLoad val) {
            out.write(val.loadId);
            out.write(val.products, OverloadProduct.UZUM_ADAPTER);
        }
    };
}
