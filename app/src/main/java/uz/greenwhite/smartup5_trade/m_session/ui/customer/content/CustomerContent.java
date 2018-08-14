package uz.greenwhite.smartup5_trade.m_session.ui.customer.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldPageContentRecycler;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.BarcodeUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.CustomerRow;

public class CustomerContent extends MoldPageContentRecycler<CustomerRow> {

    public static <C extends CustomerContent> C newCustomerInstance(Class<? extends MoldPageContent> cls, ArgSession arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER);
        return (C) newContentInstance(cls, bundle, title);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    protected final JobMate jobMate = new JobMate();

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);

        setSearchMenu(new MoldPageSearchQuery() {
            @Override
            public boolean filter(CustomerRow item, String text) {

                boolean filter = CharSequenceUtil.containsIgnoreCase(item.outlet.name, text);

                if (!filter && !TextUtils.isEmpty(item.outlet.address)) {
                    filter = CharSequenceUtil.containsIgnoreCase(item.outlet.address, text);
                }

                if (!filter && !TextUtils.isEmpty(item.outlet.addressGuide)) {
                    filter = CharSequenceUtil.containsIgnoreCase(item.outlet.addressGuide, text);
                }

                if (!filter && !TextUtils.isEmpty(item.outlet.barcode)) {
                    filter = CharSequenceUtil.containsIgnoreCase(item.outlet.barcode, text);
                }

                return filter;
            }
        });

        addSubMenu(DS.getString(R.string.barcode), new Command() {
            @Override
            public void apply() {
                BarcodeUtil.showBarcodeDialog(getContentFragment());
            }
        });

        setEmptyIcon(R.drawable.ic_store_black_48dp);
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void notifyFilterChange(){

    }

    @CallSuper
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String barcode = BarcodeUtil.getBarcodeInActivityResult(getActivity(), requestCode, resultCode, data);
        if (!TextUtils.isEmpty(barcode)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Mold.setSearchViewText(getActivity(), barcode);
                }
            }, 500);
        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_customer_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final CustomerRow item) {

        if (item.icon != null) {
            vsItem.viewGroup(R.id.fl_state).setBackground((Drawable) item.icon.second);
            vsItem.imageView(R.id.iv_state).setImageDrawable((Drawable) item.icon.first);
            vsItem.id(R.id.fl_state).setVisibility(View.VISIBLE);
        } else {
            vsItem.id(R.id.fl_state).setVisibility(View.GONE);
        }

        vsItem.textView(R.id.tv_title).setText(item.title);
        vsItem.textView(R.id.tv_sub_title).setText(item.subTitle);
        vsItem.textView(R.id.tv_info_title).setText(item.infoTitle);
        vsItem.textView(R.id.tv_detail).setText(item.detail);
        vsItem.textView(R.id.tv_sub_detail).setText(item.subDetail);
        vsItem.textView(R.id.tv_balance_receivable).setText(item.balanceReceivable);

        vsItem.id(R.id.tv_title).setVisibility(TextUtils.isEmpty(item.title) ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_sub_title).setVisibility(TextUtils.isEmpty(item.subTitle) ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_info_title).setVisibility(TextUtils.isEmpty(item.infoTitle) ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_detail).setVisibility(TextUtils.isEmpty(item.detail) ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_sub_detail).setVisibility(TextUtils.isEmpty(item.subDetail) ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_balance_receivable).setVisibility(TextUtils.isEmpty(item.balanceReceivable) ? View.GONE : View.VISIBLE);


        vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
        vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);


        jobMate.execute(new FetchImageJob(getArgSession().accountId, item.outlet.photoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsItem.imageView(R.id.iv_avatar).setImageBitmap(result);
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.GONE);
                            } else {
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            }
                        } else {
                            vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                            vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });

        Location mLocation = SmartupApp.getLocation();
        if (mLocation != null) {
            CharSequence outletDistance = item.getOutletDistance(mLocation);
            vsItem.id(R.id.tv_distance).setVisibility(TextUtils.isEmpty(outletDistance) ? View.GONE : View.VISIBLE);
            vsItem.textView(R.id.tv_distance).setText(outletDistance);
        } else {
            vsItem.id(R.id.tv_distance).setVisibility(View.GONE);
        }
    }
}
