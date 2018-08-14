package uz.greenwhite.smartup5_trade.m_session.bean;// 12.08.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductGroup {

    public final String groupId;
    public final String name;

    public ProductGroup(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }


    public static final MyMapper<ProductGroup, String> KEY_ADAPTER = new MyMapper<ProductGroup, String>() {
        @Override
        public String apply(ProductGroup val) {
            return val.groupId;
        }
    };

    public static final UzumAdapter<ProductGroup> UZUM_ADAPTER = new UzumAdapter<ProductGroup>() {
        @Override
        public ProductGroup read(UzumReader in) {
            return new ProductGroup(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ProductGroup val) {
            out.write(val.groupId);
            out.write(val.name);
        }
    };
}
