package uz.greenwhite.smartup5_trade.m_product;// 17.08.2016

import android.graphics.Bitmap;

import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgPhoto;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;

public class ProductApi {

    public static Bitmap getPhotoFull(Scope scope, String photoSha) {
        return scope.ds.loadPhotoSha(photoSha);
    }

    public static Bitmap getPhotoFull(ArgPhoto arg) {
        return getPhotoFull(arg.getScope(), arg.sha);
    }

    @SuppressWarnings("ConstantConditions")
    public static ProductFile getProductFile(ArgProduct arg) {
        return arg.getScope().ref.getProductFile(arg.productId);
    }

}
