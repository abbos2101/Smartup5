package uz.greenwhite.smartup5_trade.m_product.ui;// 30.08.2016

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.widget.Toast;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgPhoto;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;

public class PhotoFragment extends MoldContentRecyclerFragment<Tuple3> {

    public static void open(ArgProduct arg) {
        Mold.openContent(PhotoFragment.class, Mold.parcelableArgument(arg, ArgProduct.UZUM_ADAPTER));
    }

    public ArgProduct getArgProduct() {
        return Mold.parcelableArgument(this, ArgProduct.UZUM_ADAPTER);
    }

    private ArgProduct arg;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.product_photo);

        cRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        this.arg = getArgProduct();
        ProductPhoto photo = arg.getScope().ref.getProductPhoto(arg.productId);
        setListItems(ProductUtil.prepareProductPhoto(photo));
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, Tuple3 item) {
        ArgProduct arg = getArgProduct();
        if (ProductUtil.hasPhoto(arg.accountId, (String) item.third)) {
            ArgPhoto argPhoto = new ArgPhoto(arg.accountId, arg.filialId, (String) item.third, true);
            ProductPhotoFullFragment.open(argPhoto);
            return;
        }
        Toast.makeText(getActivity(), R.string.product_not_download_photo, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_photo_info_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, Tuple3 item) {
        vsItem.textView(R.id.tv_title).setText((String) item.first);
        vsItem.textView(R.id.tv_detail).setText((String) item.second);
        Bitmap bitmap = ProductUtil.getPhotoInDisk(arg.accountId, (String) item.third);
        if (bitmap != null) {
            vsItem.imageView(R.id.photo).setImageBitmap(bitmap);
        }
    }
}
