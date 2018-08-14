package uz.greenwhite.smartup5_trade.m_session.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CellDecorator;

import java.util.Calendar;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_vp.ArgVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp.ui.OutletVisitFragment;

public class VisitPlanFragment extends MoldContentFragment
        implements CalendarPickerView.OnDateSelectedListener {

    public static VisitPlanFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(VisitPlanFragment.class,
                arg, ArgSession.UZUM_ADAPTER);
    }

    public static void open(ArgSession arg) {
        Mold.openContent(VisitPlanFragment.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private CalendarPickerView calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendar = (CalendarPickerView) inflater.inflate(R.layout.session_visit_plan, container, false);
        return calendar;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.session_visit_plan);
    }

    @Override
    public void onResume() {
        super.onResume();

        final MyArray<Outlet> outlets = MyArray.nvl(ScopeUtil.execute(getArgSession(), new OnScopeReadyCallback<MyArray<Outlet>>() {
            @Override
            public MyArray<Outlet> onScopeReady(Scope scope) {
                return DSUtil.getFilialOutlets(scope);
            }
        }));
        final MyArray<OutletPlan> visits = MyArray.nvl(ScopeUtil.execute(getArgSession(), new OnScopeReadyCallback<MyArray<OutletPlan>>() {
            @Override
            public MyArray<OutletPlan> onScopeReady(Scope scope) {
                return DSUtil.getOutletVisits(scope);
            }
        }));

        calendar.setCellDecorator(new CellDecorator() {
            @Override
            public CharSequence decorate(Date date, int day) {
                final String s = DateUtil.FORMAT_AS_NUMBER.get().format(date);
                int count = outlets.reduce(0, new MyReducer<Integer, Outlet>() {
                    @Override
                    public Integer apply(Integer acc, final Outlet outlet) {
                        if (visits.contains(new MyPredicate<OutletPlan>() {
                            @Override
                            public boolean apply(OutletPlan outletPlan) {
                                return outletPlan.outletId.equals(outlet.id) &&
                                        outletPlan.date.equals(s);
                            }
                        })) {
                            return acc + 1;
                        }
                        return acc;
                    }
                });
                if (count == 0) {
                    return Integer.toString(day);
                }
                return UI.html().v(String.valueOf(day)).br().v(String.valueOf(count)).html();
            }
        });

        final Calendar beginDate = Calendar.getInstance();
        beginDate.add(Calendar.MONTH, -1);

        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 3);

        calendar.init(beginDate.getTime(), endDate.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());
        calendar.setOnDateSelectedListener(this);
    }

    @Override
    public void onDateSelected(Date date) {
        final String curDate = DateUtil.FORMAT_AS_NUMBER.get().format(date);
        String sysdate = DateUtil.FORMAT_AS_NUMBER.get().format(new Date());
        if (Integer.parseInt(sysdate) > Integer.parseInt(curDate)) {
            Mold.makeSnackBar(getActivity(), DS.getString(R.string.session_visit_plan_error_select_date)).show();
            return;
        }

        final ArgSession arg = getArgSession();
        MyArray<Room> rooms = DSUtil.getFilialRooms(arg.getScope());
        UIUtils.showRoomDialog(getActivity(), rooms, new MyCommand<Room>() {
            @Override
            public void apply(Room val) {
                OutletVisitFragment.open(new ArgVisitPlan(getArgSession(), val.id, curDate));
            }
        });
    }

    @Override
    public void onDateUnselected(Date date) {
    }
}
