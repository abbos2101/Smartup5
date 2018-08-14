package uz.greenwhite.smartup5_trade.m_session.bean;// 12.08.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductGroupValue {

    public final String groupId;
    public final String typeId;

    public ProductGroupValue(String groupId, String typeId) {
        this.groupId = groupId;
        this.typeId = typeId;
    }

    public static final MyMapper<ProductGroupValue, String> KEY_ADAPTER = new MyMapper<ProductGroupValue, String>() {
        @Override
        public String apply(ProductGroupValue val) {
            return val.groupId;
        }
    };

    public static final UzumAdapter<ProductGroupValue> UZUM_ADAPTER = new UzumAdapter<ProductGroupValue>() {
        @Override
        public ProductGroupValue read(UzumReader in) {
            return new ProductGroupValue(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ProductGroupValue val) {
            out.write(val.groupId);
            out.write(val.typeId);
        }
    };


}
