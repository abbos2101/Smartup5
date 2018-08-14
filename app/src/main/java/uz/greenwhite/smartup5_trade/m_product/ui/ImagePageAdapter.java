package uz.greenwhite.smartup5_trade.m_product.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_product.ProductUtil;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;

public class ImagePageAdapter extends PagerAdapter {

    private final ArgProduct arg;
    private final MyArray<String> items;
    private final Activity activity;

    public ImagePageAdapter(ArgProduct arg, MyArray<String> items, Activity activity) {
        this.arg = arg;
        this.items = items;
        this.activity = activity;
    }

    public void destroyItem(ViewGroup viewGroup, int position, Object object) {
        viewGroup.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewSetup viewSetup = new ViewSetup(activity, R.layout.product_row);

        Bitmap bitmap = ProductUtil.getPhotoInDisk(arg.accountId, items.get(position));

        if (bitmap != null) {
            viewSetup.imageView(R.id.siv_image).setImageBitmap(bitmap);
        } else {
            viewSetup.imageView(R.id.siv_image).setImageResource(R.drawable.display_photo);
        }
        container.addView(viewSetup.view);

        viewSetup.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoFragment.open(arg);
            }
        });
        return viewSetup.view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
