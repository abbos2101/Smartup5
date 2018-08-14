package uz.greenwhite.smartup5_trade.m_product.ui;// 17.08.2016

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_product.ProductApi;
import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgPhoto;

public class ProductPhotoFullFragment extends MoldContentFragment {

    public static void open(ArgPhoto arg) {
        Mold.openContent(ProductPhotoFullFragment.class, Mold.parcelableArgument(arg, ArgPhoto.UZUM_ADAPTER));
    }

    public ArgPhoto getArgProduct() {
        return Mold.parcelableArgument(this, ArgPhoto.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.z_photo_full);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArgPhoto arg = getArgProduct();
        Bitmap bitmap;
        if (arg.disk) {
            bitmap = ProductUtil.getPhotoInDisk(arg.accountId, arg.sha);
        } else {
            bitmap = ProductApi.getPhotoFull(arg);
        }
        this.vsRoot.imageView(R.id.pv_photo).setImageBitmap(bitmap);
    }
}
