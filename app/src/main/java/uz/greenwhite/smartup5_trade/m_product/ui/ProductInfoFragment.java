package uz.greenwhite.smartup5_trade.m_product.ui;// 16.08.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.PhotoInfo;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class ProductInfoFragment extends MoldContentFragment {

    private ViewSetup vsRoot;
    private ImagePageAdapter imagePageAdapter;
    private final JobMate jobMate = new JobMate();

    public static void open(ArgProduct arg) {
        Mold.openContent(ProductInfoFragment.class, Mold.parcelableArgument(arg, ArgProduct.UZUM_ADAPTER));
    }

    public ArgProduct getArgProduct() {
        return Mold.parcelableArgument(this, ArgProduct.UZUM_ADAPTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.product_info);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.product_info);
        ArgProduct arg = getArgProduct();

        if (ProductUtil.hasMoreFile(arg)) {
            addMenu(R.drawable.ic_attach_file_black_24dp, R.string.product_file, new Command() {
                @Override
                public void apply() {
                    FileFragment.open(getArgProduct());
                }
            });
        }

        Product product = arg.getProduct();

        final ProductPhoto photo = arg.getScope().ref.getProductPhoto(arg.productId);
        ViewPager vp = vsRoot.id(R.id.vp_product_photo);
        vp.setVisibility(photo == null ? View.GONE : View.VISIBLE);
        if (photo != null) {
            imagePageAdapter = new ImagePageAdapter(arg, photo.photos.sort(new Comparator<PhotoInfo>() {
                @Override
                public int compare(PhotoInfo l, PhotoInfo r) {
                    return CharSequenceUtil.compareToIgnoreCase(l.orderNo, r.orderNo);
                }
            }).map(new MyMapper<PhotoInfo, String>() {
                @Override
                public String apply(PhotoInfo photoInfo) {
                    return photoInfo.fileSha;
                }
            }), getActivity());
            vp.setAdapter(imagePageAdapter);
        }

        this.vsRoot.textView(R.id.product_name).setText(product.name);

        if (!TextUtils.isEmpty(product.measureName)) {
            this.vsRoot.textView(R.id.tv_measureName).setText(R.string.product_measure_name);
            this.vsRoot.textView(R.id.measureName).setText(product.measureName);
        } else {
            this.vsRoot.textView(R.id.tv_measureName).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.measureName).setVisibility(View.GONE);
        }
        if (product.measureScale > 0) {
            this.vsRoot.textView(R.id.tv_measureScale).setText(R.string.product_measure_scale);
            this.vsRoot.textView(R.id.measureScale).setText("" + product.measureScale);
        } else {
            this.vsRoot.textView(R.id.tv_measureScale).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.measureScale).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(product.boxName)) {
            this.vsRoot.textView(R.id.tv_boxName).setText(R.string.product_box_name);
            this.vsRoot.textView(R.id.boxName).setText(product.boxName);
        } else {
            this.vsRoot.textView(R.id.tv_boxName).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.boxName).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(product.boxQuant.toPlainString())) {
            this.vsRoot.textView(R.id.tv_boxQuant).setText(R.string.product_box_quant);
            this.vsRoot.textView(R.id.boxQuant).setText(product.boxQuant.toPlainString());
        } else {
            this.vsRoot.textView(R.id.tv_boxQuant).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.boxQuant).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(ProductUtil.getScaleKindName(product.saleKind))) {
            this.vsRoot.textView(R.id.tv_product_scale_kind).setText(R.string.product_scale_kind);
            this.vsRoot.textView(R.id.product_scale_kind).setText(ProductUtil.getScaleKindName(product.saleKind));
        } else {
            this.vsRoot.textView(R.id.tv_product_scale_kind).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.product_scale_kind).setVisibility(View.GONE);
        }
        String productBarcode = ProductUtil.getProductBarcode(arg, product);
        if (!TextUtils.isEmpty(productBarcode)) {
            this.vsRoot.textView(R.id.tv_product_barcode).setText(R.string.product_barcode);
            this.vsRoot.textView(R.id.product_barcode).setText(productBarcode);
        } else {
            this.vsRoot.textView(R.id.tv_product_barcode).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.product_barcode).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(ProductUtil.getExpireDates(arg, product))) {
            this.vsRoot.textView(R.id.tv_et_expire_date).setText(R.string.product_expiry_date);
            this.vsRoot.textView(R.id.et_expire_date).setText(ProductUtil.getExpireDates(arg, product));
        } else {
            this.vsRoot.textView(R.id.tv_et_expire_date).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.et_expire_date).setVisibility(View.GONE);
        }
        if (product.orderNo > 0) {
            this.vsRoot.textView(R.id.tv_product_orderno).setText(R.string.product_order_no);
            this.vsRoot.textView(R.id.product_orderno).setText("" + product.orderNo);
        } else {
            this.vsRoot.textView(R.id.tv_product_orderno).setVisibility(View.GONE);
            this.vsRoot.textView(R.id.product_orderno).setVisibility(View.GONE);
        }
    }
}
