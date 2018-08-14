package uz.greenwhite.smartup5_trade.m_session.ui.customer;// 26.11.2016

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.location.LocationHelper;
import uz.greenwhite.lib.location.LocationResult;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldPageTabFragment;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.arg.ArgPersonCustomer;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.CustomerContent;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.DebtorContent;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.DeliveryContent;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.PersonContent;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.ReturnContent;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.RouteContent;
import uz.greenwhite.smartup5_trade.m_take_location.ui.TakeLocationListener;

public class CustomerFragment extends MoldPageTabFragment implements TakeLocationListener {

    public static final int K_PERSON = 1;
    public static final int K_ROUTE = 2;
    public static final int K_DEBTOR = 3;
    public static final int K_DELIVERY = 4;
    public static final int K_RETURN = 5;
    public static final int K_DOCTOR = 6;
    public static final int K_PHARM = 7;

    public static CustomerFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(CustomerFragment.class, arg, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();

    @SuppressWarnings("MissingPermission")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.legal_person);

        if (getFragmentData() == null) {
            ScopeUtil.execute(jobMate, getArgSession(), new OnScopeReadyCallback<CustomerData>() {
                @Override
                public CustomerData onScopeReady(Scope scope) {
                    return new CustomerData(scope);
                }

                @Override
                public void onDone(CustomerData customerData) {
                    setFragmentData(customerData);
                }
            });
        }

        Location mLocation = SmartupApp.getLocation();
        long minute = mLocation == null ? 0 : (mLocation.getTime() - System.currentTimeMillis() / 1000) / 60;
        if (mLocation == null || minute > 5) {
            if (SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                LocationHelper.getOneLocation(getActivity(), new LocationResult() {
                    @Override
                    public void onLocationChanged(Location location) {
                        location.setTime(System.currentTimeMillis());
                        SmartupApp.setLocation(location);

                        if (getActivity() != null && adapter != null) {
                            int currentItem = viewPager.getCurrentItem();
                            CustomerContent item = (CustomerContent) adapter.getItem(currentItem);
                            item.notifyDataSetChanged();
                        }
                    }
                }).startListener();
            }
        }

        ArgSession arg = getArgSession();
        MyArray<CustomerContent> items = MyArray.from(
                PersonContent.newInstance(new ArgPersonCustomer(arg, K_PERSON), "Все"),
                RouteContent.newInstance(arg, "Маршруты"),
                DebtorContent.newInstance(arg, "Должники"),
                DeliveryContent.newInstance(arg, "Доставки"),
                ReturnContent.newInstance(arg, "Возвраты")
        );

        setSections(items.<MoldPageContent>toSuper());
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    public void notifyFilterChange() {
        int currentItem = viewPager.getCurrentItem();
        CustomerContent content = (CustomerContent) adapter.getItem(currentItem);
        content.notifyFilterChange();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int currentItem = viewPager.getCurrentItem();
        CustomerContent content = (CustomerContent) adapter.getItem(currentItem);
        content.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationToken(Location location) {
        int currentItem = viewPager.getCurrentItem();
        CustomerContent content = (CustomerContent) adapter.getItem(currentItem);
        if (content instanceof TakeLocationListener) {
            ((TakeLocationListener) content).onLocationToken(location);
        }
    }
}
