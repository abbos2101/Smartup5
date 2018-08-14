package uz.greenwhite.smartup5_trade.m_duty;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_duty.arg.ArgCondition;
import uz.greenwhite.smartup5_trade.m_duty.bean.FilialActionRow;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class FilialActionFragment extends MoldContentRecyclerFragment<FilialActionRow> {

    public static void open(Activity activity, ArgSession arg) {
        Mold.openContent(activity, FilialActionFragment.class,
                Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }


    private final JobMate jobMate = new JobMate();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), DS.getString(R.string.menu_filial_action));

        ScopeUtil.execute(getArgSession(), new OnScopeReadyCallback<MyArray<FilialActionRow>>() {
            @Override
            public MyArray<FilialActionRow> onScopeReady(Scope scope) {
                return DutyApi.getPersonActions(scope);
            }

            @Override
            public void onDone(MyArray<FilialActionRow> personActions) {
                setListItems(personActions);
            }
        });
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, FilialActionRow item) {
        FilialActionInfoFragment.open(getActivity(), new ArgCondition(getArgSession(), item.action.actionId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jobMate.stopListening();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.duty_action;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, FilialActionRow item) {
        vsItem.textView(R.id.tv_name).setText(item.action.name);
        vsItem.textView(R.id.tv_warehouse).setText(DS.getString(R.string.duty_action_warehouse, item.warehouse.name));
        vsItem.textView(R.id.tv_action_kind).setText(DS.getString(R.string.duty_action_kind, item.getActionKind()));
        vsItem.textView(R.id.tv_date).setText(DS.getString(R.string.duty_action_date, item.action.startDate, item.action.endDate));
    }
}
