package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductSet {

    public final String productSetId;
    public final MyArray<String> productId;

    public ProductSet(String productSetId, MyArray<String> productId) {
        this.productSetId = productSetId;
        this.productId = productId;
    }

    public static final MyMapper<ProductSet, String> KEY_ADAPTER = new MyMapper<ProductSet, String>() {
        @Override
        public String apply(ProductSet productSet) {
            return productSet.productSetId;
        }
    };

    public static final UzumAdapter<ProductSet> UZUM_ADAPTER = new UzumAdapter<ProductSet>() {
        @Override
        public ProductSet read(UzumReader in) {
            return new ProductSet(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, ProductSet val) {
            out.write(val.productSetId);
            out.write(val.productId, STRING_ARRAY);
        }
    };
}
