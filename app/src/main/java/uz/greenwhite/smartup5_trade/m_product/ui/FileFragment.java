package uz.greenwhite.smartup5_trade.m_product.ui;// 30.08.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_product.ProductApi;
import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;

public class FileFragment extends MoldContentRecyclerFragment<Tuple3> {

    public static void open(ArgProduct arg) {
        Mold.openContent(FileFragment.class, Mold.parcelableArgument(arg, ArgProduct.UZUM_ADAPTER));
    }

    public ArgProduct getArgProduct() {
        return Mold.parcelableArgument(this, ArgProduct.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.product_file);

        ProductFile file = ProductApi.getProductFile(getArgProduct());
        setListItems(ProductUtil.prepareProductFile(file));
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, Tuple3 item) {
        ArgProduct arg = getArgProduct();
        if (ProductUtil.hasFile(arg.accountId, (String) item.third)) {
            String mimiType = ProductUtil.getProductFileMimiType(arg, (String) item.third);
            if (!TextUtils.isEmpty(mimiType)) {
                ProductUtil.openFile(getActivity(), arg.accountId, (String) item.third, mimiType);
                return;
            }
            Toast.makeText(getActivity(), R.string.product_mimitype_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(), R.string.product_not_download_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return android.R.layout.simple_list_item_2;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, Tuple3 item) {
        vsItem.textView(android.R.id.text1).setText((String) item.first);
        vsItem.textView(android.R.id.text2).setText((String) item.second);
    }
}
