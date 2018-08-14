package uz.greenwhite.smartup5_trade.m_session.ui.person;


import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.location.LocationHelper;
import uz.greenwhite.lib.location.LocationResult;
import uz.greenwhite.lib.mold.MoldContentSwipeRecyclerFragment;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.common.dialog.SyncDialog;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSync;

public abstract class PersonFragment<E> extends MoldContentSwipeRecyclerFragment<E> {

    protected final JobMate jobMate = new JobMate();

    protected abstract ArgSession getArgSession();

    @SuppressWarnings("MissingPermission")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                        if (isAdded() && getActivity() != null && adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }).startListener();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ArgSession argSession = getArgSession();
        jobMate.listenKey(TapeSyncJob.key(argSession.accountId), new SyncListener());

        reloadContent();
    }

    @Override
    public void onRefresh() {
        SyncDialog.show(getActivity(), new ArgSync(getArgSession(), true));
    }

    @Override
    public void onStop() {
        super.onStop();
        this.jobMate.stopListening();
    }


    private class SyncListener implements LongJobListener<ProgressValue> {

        @Override
        public void onStart() {
            startRefresh();
        }

        @Override
        public void onStop(Throwable error) {
            stopRefresh();
            reloadContent();
        }

        @Override
        public void onProgress(ProgressValue progress) {
        }
    }
}
