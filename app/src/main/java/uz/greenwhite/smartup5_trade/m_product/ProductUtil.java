package uz.greenwhite.smartup5_trade.m_product;// 16.08.2016

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.BitmapUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.FileInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.PhotoInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;

public class ProductUtil {

    public static String getProductBarcode(ArgProduct arg, Product product) {
        ProductBarcode barcode = arg.getScope().ref.getProductBarcode(product.id);
        if (barcode == null || barcode.barcodes.isEmpty()) {
            return null;
        }
        return barcode.barcodes.mkString(",");
    }

    @SuppressWarnings("ConstantConditions")
    public static String getExpireDates(ArgProduct arg, final Product product) {
        MyArray<ProductBalance> balances = arg.getScope().ref.getProductBalances()
                .filter(new MyPredicate<ProductBalance>() {
                    @Override
                    public boolean apply(ProductBalance val) {
                        return val.productId.equals(product.id);
                    }
                });
        return balances.map(new MyMapper<ProductBalance, String>() {
            @Override
            public String apply(ProductBalance val) {
                return val.expireDate;
            }
        }).mkString(", ");
    }

    public static String getScaleKindName(String scaleKind) {
        switch (scaleKind) {
            case Product.INPUT_BOX:
                return DS.getString(R.string.product_boxes);
            case Product.INPUT_QUANT:
                return DS.getString(R.string.product_quants);
            case Product.INPUT_ALL:
                return DS.getString(R.string.product_all);
            default:
                return "";
        }
    }

    public static MyArray<Tuple3> prepareProductFile(ProductFile file) {
        if (file == null || file.files.isEmpty()) {
            return MyArray.emptyArray();
        }
        List<Tuple3> result = new ArrayList<>();

        for (FileInfo item : file.files) {
            result.add(new Tuple3(item.title, item.note, item.fileSha));
        }
        return MyArray.from(result);
    }

    public static MyArray<Tuple3> prepareProductPhoto(ProductPhoto photo) {
        if (photo == null || photo.photos.isEmpty()) {
            return MyArray.emptyArray();
        }
        List<Tuple3> result = new ArrayList<>();
        for (PhotoInfo item : photo.photos) {
            result.add(new Tuple3(item.title, item.note, item.fileSha));
        }
        return MyArray.from(result);
    }

    public static Bitmap getPhotoInDisk(String accountId, String sha) {
        try {
            String photoPath = ProductPhoto.getPhotoPath(accountId);
            File file = new File(photoPath, sha);
            if (file.exists()) {
                return BitmapUtil.decodeFile(file, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
        }
        return null;
    }

    public static boolean hasPhoto(String accountId, String sha) {
        return new File(ProductPhoto.getPhotoPath(accountId), sha).exists();
    }

    public static boolean hasFile(String accountId, String sha) {
        return new File(ProductFile.getFilePath(accountId) + "/" + sha).exists();
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean hasMorePhoto(ArgProduct arg) {
        Scope scope = arg.getScope();
        ProductPhoto photo = scope.ref.getProductPhoto(arg.productId);
        return photo != null && photo.photos.nonEmpty();
    }

    public static boolean hasMoreFile(ArgProduct arg) {
        ProductFile file = ProductApi.getProductFile(arg);
        return file != null && file.files.nonEmpty();
    }

    public static String getProductFileMimiType(ArgProduct arg, String sha) {
        ProductFile file = ProductApi.getProductFile(arg);
        FileInfo fileInfo = file.files.find(sha, FileInfo.KEY_ADAPTER);
        if (fileInfo != null) {
            return fileInfo.contentType;
        }
        return null;
    }

    public static void openFile(Activity activity, final String accountId, String sha, String mimiType) {
        File file = new File(ProductFile.getFilePath(accountId) + "/" + sha);
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), mimiType);

        Uri uriForFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uriForFile = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            uriForFile = Uri.fromFile(file);
        }
        intent.setDataAndType(uriForFile, mimiType);
        try {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                UI.alertError(activity, e);
            } else {
                UI.dialog()
                        .title(R.string.warning)
                        .message(activity.getString(R.string.you_do_have_not_application_to_view_downloaded_file))
                        .show(activity);
            }
        }
    }
}
