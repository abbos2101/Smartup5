package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentSwipeRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;
import uz.greenwhite.smartup5_trade.m_tracking.arg.ArgTracking;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingUser;

public class TrackingFragment extends MoldContentSwipeRecyclerFragment<TrackingUser> {

    public static void open(ArgSession argSession) {
        Mold.openContent(TrackingFragment.class, Mold.parcelableArgument(argSession, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.session_tracking_mp);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(TrackingUser item, String text) {
                return TextUtils.isEmpty(text) ||
                        CharSequenceUtil.containsIgnoreCase(item.name, text);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        setEmptyText(R.string.list_is_empty);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        startRefresh();
        jobMate.execute(new ActionJob<TrackingUser>(getArgSession(), RT.URI_SV_AGENTS))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        stopRefresh();
                        if (resolved) {
                            setListItems(Uzum.toValue(result, TrackingUser.UZUM_ADAPTER.toArray()));
                        } else {
                            Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, final TrackingUser item) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            boolean calling = true;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (calling) {
                    calling = false;
                    Calendar c = Calendar.getInstance();
                    c.set(year, monthOfYear, dayOfMonth, 0, 0);
                    Date date = c.getTime();
                    String result = DateUtil.FORMAT_AS_DATE.get().format(date);
                    TrackingIndexFragment.open(new ArgTracking(getArgSession(), item.id, result, getListItems()));
                }
            }
        };

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        DatePickerDialog dpd = new DatePickerDialog(
                getActivity(), onDateSetListener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.tracking_agent_item;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, TrackingUser item) {
        vsItem.textView(R.id.tv_user_name).setText(item.name);
        vsItem.textView(R.id.tv_detail).setText(item.getDetail());

        jobMate.execute(new FetchImageJob(getArgSession().accountId, item.photoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsItem.imageView(R.id.iv_avatar).setImageBitmap(result);
                            } else {
                                vsItem.imageView(R.id.iv_avatar).setImageResource(R.drawable.default_userpic);
                            }
                        } else {
                            vsItem.imageView(R.id.iv_avatar).setImageResource(R.drawable.default_userpic);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });
    }
}



