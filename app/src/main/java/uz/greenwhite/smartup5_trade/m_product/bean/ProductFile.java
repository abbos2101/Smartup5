package uz.greenwhite.smartup5_trade.m_product.bean;// 25.08.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class ProductFile {

    public final String productId;
    public final MyArray<FileInfo> files;

    public ProductFile(String productId,
                       MyArray<FileInfo> files) {
        this.productId = productId;
        this.files = files;
    }

    public static String getFilePath(String accountId) {
        String serverPath = DS.getServerPath(accountId);
        return serverPath + "/product_file";
    }

    public static final MyMapper<ProductFile, String> KEY_ADAPTER = new MyMapper<ProductFile, String>() {
        @Override
        public String apply(ProductFile val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<ProductFile> UZUM_ADAPTER = new UzumAdapter<ProductFile>() {
        @Override
        public ProductFile read(UzumReader in) {
            return new ProductFile(in.readString(), in.readArray(FileInfo.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, ProductFile val) {
            out.write(val.productId);
            out.write(val.files, FileInfo.UZUM_ADAPTER);
        }
    };
}
