package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductSimilar {

    public final String productId;
    public final MyArray<String> similarIds;

    public ProductSimilar(String productId, MyArray<String> similarIds) {
        this.productId = productId;
        this.similarIds = similarIds;
    }

    public static final MyMapper<ProductSimilar, String> KEY_ADAPTER = new MyMapper<ProductSimilar, String>() {
        @Override
        public String apply(ProductSimilar val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<ProductSimilar> UZUM_ADAPTER = new UzumAdapter<ProductSimilar>() {
        @Override
        public ProductSimilar read(UzumReader in) {
            return new ProductSimilar(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, ProductSimilar val) {
            out.write(val.productId);
            out.write(val.similarIds, STRING_ARRAY);
        }
    };
}
