package uz.greenwhite.smartup5_trade.m_debtor.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgPrepaymentOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class PrepaymentOutletFragment extends MoldContentRecyclerFragment<Outlet> {

    public static void open(ArgPrepaymentOutlet arg) {
        Mold.openContent(PrepaymentOutletFragment.class, Mold.parcelableArgument(arg, ArgPrepaymentOutlet.UZUM_ADAPTER));
    }

    public ArgPrepaymentOutlet getArgDebtorOutlet() {
        return Mold.parcelableArgument(this, ArgPrepaymentOutlet.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.select);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(Outlet item, String text) {
                return CharSequenceUtil.containsIgnoreCase(item.name, text) ||
                        CharSequenceUtil.containsIgnoreCase(item.address, text) ||
                        CharSequenceUtil.containsIgnoreCase(item.addressGuide, text);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ScopeUtil.execute(jobMate, getArgDebtorOutlet(), new OnScopeReadyCallback<MyArray<Outlet>>() {
            @Override
            public MyArray<Outlet> onScopeReady(Scope scope) {
                return DSUtil.getFilialOutlets(scope).filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        return outlet.isOutlet();
                    }
                });
            }

            @Override
            public void onDone(MyArray<Outlet> outlets) {
                super.onDone(outlets);
                setListItems(outlets);
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                UI.alertError(getActivity(), throwable);
            }
        });
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, Outlet item) {
        ArgPrepaymentOutlet arg = getArgDebtorOutlet();
        ArgOutlet argOutlet = new ArgOutlet(arg, item.id);
        PrepaymentFragment.open(new ArgDebtor(argOutlet, arg.paymentKind));
        getActivity().finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_outlet_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final Outlet item) {
        vsItem.id(R.id.lf_state).setVisibility(View.GONE);

        final int iconBackground = item.getIconBackground();
        vsItem.imageView(R.id.iv_avatar).setBackgroundResource(iconBackground);

        vsItem.textView(R.id.title).setText(item.name);
        vsItem.textView(R.id.detail).setText(item.getAddress());

        jobMate.execute(new FetchImageJob(getArgDebtorOutlet().accountId, item.photoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsItem.imageView(R.id.iv_avatar).setImageBitmap(result);
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.GONE);
                            } else {
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                vsItem.imageView(R.id.iv_avatar).setBackgroundResource(iconBackground);
                            }
                        } else {
                            vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                            vsItem.imageView(R.id.iv_avatar).setBackgroundResource(iconBackground);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });
    }
}
