package uz.greenwhite.smartup5_trade;// 16.05.2016

import android.Manifest;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import uz.greenwhite.lib.GWSLOG;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.lib.mold.FragmentListener;
import uz.greenwhite.lib.mold.MoldActivity;
import uz.greenwhite.lib.mold.MoldApi;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.AnorApp;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.arg.ArgFilial;
import uz.greenwhite.smartup.anor.bean.user.UserFilial;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.common.google_analytics.TrackingFragmentNames;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.ui.SessionIndexFragment;
import uz.greenwhite.smartup5_trade.schedule.Scheduler;

public class SmartupApp extends AnorApp {

    private static Location mLocation;
    private static Tracker mTracker;

    synchronized public static Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getContext());
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static Location getLocation() {
        return mLocation;
    }

    public static void setLocation(Location location) {
        if (location != null) {
            mLocation = location;
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        GWSLOG.DEBUG = BuildConfig.DEBUG;

        TrackingFragmentNames.init();
        MoldActivity.fragmentListener = getFragmentListener();

        MoldApi.APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
        MoldApi.APPLICATION_VERSION_CODE = BuildConfig.VERSION_CODE;
        Scheduler.start(this);

        try {
            new AppVersionNameLoader().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (SysUtil.checkSelfPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    SysUtil.checkSelfPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                if (mLocation == null) {
                    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                    mLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mLocation == null) {
                        mLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                String localeCode = AdminApi.getLocaleCode();

                Locale locale = new Locale(localeCode);
                Locale.setDefault(locale);
                Resources resources = activity.getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public MyArray<String> getSSlUrls() {
        return super.getSSlUrls().append("crashlytics.com").append("analytics.google.com").append("play.google.com");
    }

    @Override
    public String getAccountId() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    public boolean openSessionFragment(Activity activity, ArgFilial arg) {
        DS.initScope(arg.accountId, arg.filialId);
        Scope scope = DS.getScope(arg.accountId, arg.filialId);
        MyArray<Role> roles = scope.ref.getRoles();
        if (!"0".equals(arg.filialId) && roles.isEmpty()) {
            UI.alertError(activity, DS.getString(R.string.session_filia_not_have_role));
            return false;
        }
        try {
            if (!RootUtil.isDeviceRooted(scope)) {
                SessionIndexFragment.open(new ArgSession(arg.accountId, arg.filialId));
                return true;
            } else {
                UI.alertError(activity, DS.getString(R.string.root_error_msg));
                return false;
            }
        } catch (Exception e) {
            UI.alertError(activity, (String) ErrorUtil.getErrorMessage(e).message);
        }
        return false;
    }

    @Override
    public void clearCacheScope(final String accountId) {
        // removes unread messages info (old) from preferences
        if (!AdminApi.loadNewMsgCountFromPref().isEmpty()) {
            AdminApi.removeMsgPref();
        }

        Manager.handler.post(new Runnable() {
            @Override
            public void run() {
                JobApi.execute(new ShortJob<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        DS.initScope(accountId, Filial.FILIAL_HEAD);
                        Scope scope = DS.getScope(accountId, Filial.FILIAL_HEAD);

                        MyArray<UserFilial> userFilials = scope.ref.getUser().filials;
                        if (userFilials.isEmpty()) return null;

                        DS.initScope(accountId, userFilials.get(0).filialId);
                        scope = DS.getScope(accountId, userFilials.get(0).filialId);
                        Filial filial = scope.ref.getFilial(scope.filialId);

                        MyArray<String> unreadMessageIds = filial.unreadMessageIds;
                        AdminApi.saveNewMsgCountToPref(unreadMessageIds, true);

                        MyArray<String> unviewedTaskIds = filial.unviewedTaskIds;
                        AdminApi.saveNewTaskCountToPref(unviewedTaskIds, true);

                        return null;
                    }
                });

                DS.clearScopeCache(accountId);
            }
        });
    }

    @Override
    public String getPrefName() {
        return "SMARTUP5_TRADE_DS_PREFERENCE_V2";
    }

    @Override
    public int getDatabaseVersion() {
        return 7;
    }

    public FragmentListener getFragmentListener() {
        return new FragmentListener() {
            @Override
            public void onActivityCreated(Fragment fragment) {
                String name = TrackingFragmentNames.getName(fragment);
                if (name != null) {
                    Tracker mTracker = getDefaultTracker();
                    mTracker.setScreenName(name);
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                    GWSLOG.log("GoogleAnalytics:sending");
                }
            }
        };
    }

}
