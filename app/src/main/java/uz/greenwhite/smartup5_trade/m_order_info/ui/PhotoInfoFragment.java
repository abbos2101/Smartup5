package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealFormContentFragment;
import uz.greenwhite.smartup5_trade.m_order_info.arg.ArgOrderPhoto;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderCurPhoto;

public class PhotoInfoFragment extends DealFormContentFragment {

    public static PhotoInfoFragment newInstance(ArgOrderPhoto arg) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgOrderPhoto.UZUM_ADAPTER);
        return Mold.parcelableArgumentNewInstance(PhotoInfoFragment.class, bundle);
    }

    public ArgOrderPhoto getArgOrderPhoto() {
        return Mold.parcelableArgument(this, ArgOrderPhoto.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_photo_info);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.photo);

        final ArgOrderPhoto arg = getArgOrderPhoto();
        OrderInfoData data = Mold.getData(getActivity());

        final OrderCurPhoto orderCurPhoto = data.orderInfo.form.curPhotos.find(arg.sha, OrderCurPhoto.KEY_ADAPTER);

        addMenu(R.drawable.about, R.string.info, new Command() {
            @Override
            public void apply() {
                showEditDialog(orderCurPhoto);
            }
        });

        jobMate.execute(new FetchImageJob(data.accountId, orderCurPhoto.sha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved && result != null) {
                            vsRoot.imageView(R.id.pv_photo).setImageBitmap(result);
                        } else if (error != null) {
                            error.printStackTrace();
                        }
                    }
                });


    }

    private void showEditDialog(OrderCurPhoto orderCurPhoto) {
        ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_photo_edit);

        Spinner spinner = vs.spinner(R.id.sp_photo_type);
        spinner.setEnabled(false);
        UI.bind(spinner, new ValueSpinner(MyArray.from(new SpinnerOption("", orderCurPhoto.photoTypeName))), true);
        EditText editText = vs.editText(R.id.et_photo_note);
        editText.setEnabled(false);
        editText.setText(orderCurPhoto.note);
        UI.bottomSheet().contentView(vs.view).show(getActivity());
    }
}
