package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.NavigationFragment;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_order_info.arg.ArgOrderInfo;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderForm;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderInfo;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class OrderInfoIndexFragment extends NavigationFragment {

    public static void open(ArgOrderInfo arg) {
        Mold.openNavigation(OrderInfoIndexFragment.class, Mold.parcelableArgument(arg, ArgOrderInfo.UZUM_ADAPTER));
    }


    ArgOrderInfo getArgDealInfo() {
        return Mold.parcelableArgument(this, ArgOrderInfo.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        OrderInfoData data = Mold.getData(getActivity());
        if (data != null) {
            setItems(data.orderInfo.form.getForms());
        } else {
            requestOrderInfo();
        }
    }

    void requestOrderInfo() {
        ArgOrderInfo args = getArgDealInfo();
        jobMate.executeWithDialog(getActivity(), new ActionJob<String>(args, RT.URI_SV_ORDER_INFO,
                MyArray.from(args.dealId, args.state)))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) requestResult(result);
                        else {
                            Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }

    void requestResult(String json) {
        try {
            OrderInfo orderInfo = Uzum.toValue(json, OrderInfo.UZUM_ADAPTER);
            ArgOrderInfo arg = getArgDealInfo();
            OrderInfoData data = new OrderInfoData(arg.accountId, orderInfo);
            Mold.setData(getActivity(), data);
            setItems(orderInfo.form.getForms());
            showForm(OrderForm.INFO);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    @Override
    public boolean showForm(NavigationItem form) {
        MoldContentFragment content = make(form);
        if (content != null) {
            Mold.replaceContent(getActivity(), content, form);
            return true;
        }
        return false;
    }

    private MoldContentFragment make(NavigationItem form) {
        switch (form.id) {
            case OrderForm.INFO:
                return new OrderInfoFragment();
            case OrderForm.GIFT:
                return new OrderGiftFragment();
            case OrderForm.ACCOUNT:
                return new OrderPaymentFragment();
            case OrderForm.ORDER:
                return new OrderOrderFragment();
            case OrderForm.STOCK:
                return new OrderStockFragment();
            case OrderForm.PHOTO:
                return new OrderPhotoFragment();
            default:
                return null;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }
}
