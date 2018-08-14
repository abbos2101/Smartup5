package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.method.ScrollingMovementMethod;

import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.VerticalScrollingTextView;
import uz.greenwhite.smartup5_trade.m_order_info.arg.ArgOrderPhoto;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurPhoto;

public class OrderPhotoFragment extends MoldContentRecyclerFragment<OrderCurPhoto> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        OrderInfoData data = Mold.getData(getActivity());
        setListItems(data.orderInfo.form.curPhotos);
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, OrderCurPhoto item) {
        OrderInfoIndexFragment fragment = Mold.getIndexFragment(getActivity());
        ArgOrderPhoto argOrderPhoto = new ArgOrderPhoto(fragment.getArgDealInfo(), item.sha);
        Mold.addContent(getActivity(), PhotoInfoFragment.newInstance(argOrderPhoto));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_photo_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final OrderCurPhoto item) {
        VerticalScrollingTextView tvDetails = (VerticalScrollingTextView) vsItem.textView(R.id.tv_detail);
        tvDetails.setText(item.photoTypeName);
        tvDetails.setContinuousScrolling(true);
        tvDetails.setMovementMethod(new ScrollingMovementMethod());
        tvDetails.scroll();

        vsItem.textView(R.id.tv_date).setText(DateUtil.convert(item.date, DateUtil.FORMAT_AS_DATE));

        OrderInfoData data = Mold.getData(getActivity());

        jobMate.execute(new FetchImageJob(data.accountId, item.sha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved && result != null) {
                            vsItem.imageView(R.id.photo).setImageBitmap(result);
                        } else if (error != null) {
                            error.printStackTrace();
                        }
                    }
                });

    }
}
