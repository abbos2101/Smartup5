package uz.greenwhite.smartup5_trade.m_session.ui.person;// 27.06.2016

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.SessionUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;

public class ReturnFragment extends PersonFragment<OutletRow> {

    public static ReturnFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(ReturnFragment.class,
                arg, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(OutletRow item, String text) {
                return CharSequenceUtil.containsIgnoreCase(item.title, text);
            }
        });

        setEmptyIcon(R.drawable.empty);
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void reloadContent() {
        final ArgSession arg = getArgSession();
        ScopeUtil.execute(jobMate, arg, new OnScopeReadyCallback<MyArray<OutletRow>>() {
            @Override
            public MyArray<OutletRow> onScopeReady(final Scope scope) {
                return SessionUtil.getOutletRow(false, scope);
            }

            @Override
            public void onDone(MyArray<OutletRow> result) {
                setListItems(result);
                makeFooter();
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
            }
        });
    }

    private void makeFooter() {
        final ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.person_count_foter);
        vsFooter.textView(R.id.tv_persons).setText(DS.getString(R.string.session_menu_return));
        vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void populate() {
                vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                this.populate();
            }

            public void onChanged() {
                super.onChanged();
                this.populate();
            }
        });
        BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, OutletRow item) {
        OutletIndexFragment.open(new ArgOutlet(getArgSession(), item.outlet.id));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_outlet_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final OutletRow item) {
        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<OutletRow> filteredItems = adapter.getFilteredItems();
            OutletRow lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        Tuple2 icon = item.getIcon();
        if (icon != null) {
            vsItem.viewGroup(R.id.lf_state).setBackground((Drawable) icon.second);
            vsItem.imageView(R.id.state).setImageDrawable((Drawable) icon.first);
            vsItem.id(R.id.lf_state).setVisibility(View.VISIBLE);
        } else {
            vsItem.id(R.id.lf_state).setVisibility(View.GONE);
        }
        vsItem.textView(R.id.title).setText(item.title);
        vsItem.textView(R.id.detail).setText(item.detail);

        vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
        vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);

        TextView tvInfo = vsItem.textView(R.id.tv_info);
        if (item.lastInfo != null && item.lastInfo.hasLastDate()) {
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(item.getLstVisitDate());
        } else tvInfo.setVisibility(View.GONE);

        CharSequence balanceReceivable = item.getBalanceReceivable();
        if (!TextUtils.isEmpty(balanceReceivable)) {
            vsItem.textView(R.id.tv_balance_receivable).setText(balanceReceivable);
            vsItem.id(R.id.tv_balance_receivable).setVisibility(View.VISIBLE);
        } else vsItem.id(R.id.tv_balance_receivable).setVisibility(View.GONE);

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
            vsItem.id(R.id.tv_outlet_distance).setVisibility(TextUtils.isEmpty(outletDistance) ? View.GONE : View.VISIBLE);
            vsItem.textView(R.id.tv_outlet_distance).setText(outletDistance);
        } else {
            vsItem.id(R.id.tv_outlet_distance).setVisibility(View.GONE);
        }
    }
}
