package uz.greenwhite.smartup5_trade.m_product.bean;// 25.08.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class ProductPhoto {

    public final String productId;
    public final MyArray<PhotoInfo> photos;

    public ProductPhoto(String productId,
                        MyArray<PhotoInfo> photos) {
        this.productId = productId;
        this.photos = photos;
    }

    public static String getPhotoPath(String accountId) {
        String serverPath = DS.getServerPath(accountId);
        return serverPath + "/product_photo";
    }

    public static final MyMapper<ProductPhoto, String> KEY_ADAPTER = new MyMapper<ProductPhoto, String>() {
        @Override
        public String apply(ProductPhoto val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<ProductPhoto> UZUM_ADAPTER = new UzumAdapter<ProductPhoto>() {
        @Override
        public ProductPhoto read(UzumReader in) {
            return new ProductPhoto(in.readString(), in.readArray(PhotoInfo.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, ProductPhoto val) {
            out.write(val.productId);
            out.write(val.photos, PhotoInfo.UZUM_ADAPTER);
        }
    };
}
