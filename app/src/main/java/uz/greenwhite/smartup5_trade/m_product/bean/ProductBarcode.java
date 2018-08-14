package uz.greenwhite.smartup5_trade.m_product.bean;// 25.08.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductBarcode {

    public final String productId;
    public final MyArray<String> barcodes;

    public ProductBarcode(String productId, MyArray<String> barcodes) {
        this.productId = productId;
        this.barcodes = barcodes;
    }

    public static final MyMapper<ProductBarcode, String> KEY_ADAPTER = new MyMapper<ProductBarcode, String>() {
        @Override
        public String apply(ProductBarcode val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<ProductBarcode> UZUM_ADAPTER = new UzumAdapter<ProductBarcode>() {
        @Override
        public ProductBarcode read(UzumReader in) {
            return new ProductBarcode(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, ProductBarcode val) {
            out.write(val.productId);
            out.write(val.barcodes, STRING_ARRAY);
        }
    };
}
