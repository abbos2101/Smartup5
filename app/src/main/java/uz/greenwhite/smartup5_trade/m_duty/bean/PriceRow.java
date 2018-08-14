package uz.greenwhite.smartup5_trade.m_duty.bean;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.bean.PhotoInfo;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class PriceRow {

    public final Product product;
    public final String priceName;
    public final String price;

    @Nullable
    public final PhotoInfo photo;

    public PriceRow(Product product,
                    CharSequence priceName,
                    CharSequence price,
                    @Nullable PhotoInfo photo) {
        this.product = product;
        this.priceName = removeEnterIfStart(String.valueOf(priceName));
        this.price = removeEnterIfStart(String.valueOf(price));
        this.photo = photo;
    }

    public Bitmap getProductImage(ArgSession arg) {
        if (photo == null) return null;
        return ProductUtil.getPhotoInDisk(arg.accountId, photo.fileSha);
    }

    private String removeEnterIfStart(String string) {
        if (string.startsWith("\n")) {
            return string.substring(1, string.length());
        }
        return string;
    }
}