package uz.greenwhite.smartup5_trade.m_take_location.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.GWSLOG;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldNewLocationDialog;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumParcellable;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.RootUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.SettingLocation;
import uz.greenwhite.smartup5_trade.m_take_location.TakeLocationUtil;
import uz.greenwhite.smartup5_trade.m_take_location.arg.ArgTakeMyLocation;

public class MyTakeLocationDialog extends MoldNewLocationDialog {

    public static final String TAKE_LOCATION_DIALOG = "MyTakeLocationDialog:take-location-dialog";
    private static final String IS_DISMISS = "MyTakeLocationDialog:is_dismiss";
    private static final String WAIT_TIME = "MyTakeLocationDialog:waitTime";

    public static void show(FragmentActivity activity, ArgTakeMyLocation arg) {
        MyTakeLocationDialog dialog = new MyTakeLocationDialog();
        dialog.setArguments(Mold.parcelableArgument(arg, ArgTakeMyLocation.UZUM_ADAPTER));
        dialog.show(activity.getSupportFragmentManager(), TAKE_LOCATION_DIALOG);
    }

    public static void show(FragmentActivity activity, ArgSession arg, boolean requiredLocation) {
        show(activity, new ArgTakeMyLocation(arg, requiredLocation));
    }

    public static void show(FragmentActivity activity, ArgSession arg) {
        show(activity, new ArgTakeMyLocation(arg, false));
    }

    @SuppressWarnings("ConstantConditions")
    public ArgTakeMyLocation getArgTakeLocation() {
        Bundle arguments = getArguments();
        UzumParcellable p = arguments.getParcelable("arg");
        return Uzum.toValue(p, ArgTakeMyLocation.UZUM_ADAPTER);
    }

    private CountDownTimer timer;
    private boolean isDismiss;
    private ViewSetup vsRoot;
    private long mWaitTime = 0;

    private final ArrayList<Location> foundLocation = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(5000);// 5 second

        if (savedInstanceState != null) {
            isDismiss = savedInstanceState.getBoolean(IS_DISMISS, false);
            mWaitTime = savedInstanceState.getLong(WAIT_TIME);
        }
        this.isDismiss = false;

        this.vsRoot = new ViewSetup(getActivity(), R.layout.take_location_dialog);

        vsRoot.id(R.id.tv_accuracy).setVisibility(View.GONE);

        vsRoot.id(R.id.img_radar_line).startAnimation(anim);

        return new AlertDialog.Builder(getActivity())
                .setView(vsRoot.view)
                .setCancelable(false)
                .setTitle(getString(R.string.determining_location))
                .setNegativeButton(getString(R.string.close), null)
                .create();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onCancelLocationSetting() {
        Manager.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 100);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocation();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DISMISS, isDismiss);
        outState.putLong(WAIT_TIME, mWaitTime);
    }

    @Override
    public void onStart() {
        super.onStart();

        //------------------------------------------------------------------------------------------
        final ProgressBar progressBar = vsRoot.id(R.id.progressbar);
        final TextView accuracyText = vsRoot.id(R.id.tv_accuracy_error);
        int waitTime = (2 * 60) * 1000;
        if (mWaitTime != 0) waitTime = (int) mWaitTime;
        progressBar.setMax(waitTime);
        progressBar.setIndeterminate(false);
        final int finalWaitTime = waitTime;
        timer = new CountDownTimer(finalWaitTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mWaitTime = millisUntilFinished;
                long pos = finalWaitTime - millisUntilFinished;
                if (pos < 0) {
                    pos = 0;
                }
                long second = millisUntilFinished / 1000;
                String stockSecond = "0" + (second % 60);
                vsRoot.textView(R.id.tv_time).setText(DS.getString(R.string.location_time,
                        "" + (second / 60) + ":" + stockSecond.substring(stockSecond.length() - 2)));

                progressBar.setProgress((int) pos);

                if (finalWaitTime / 2 < pos) {
                    if (accuracyText.getVisibility() == View.GONE) {
                        accuracyText.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                progressBar.setIndeterminate(true);
                if (!foundLocation.isEmpty()) {
                    Location location = TakeLocationUtil.getCorrectLocation(foundLocation);
                    onLocationChanged(location);
                } else {
                    defineInTheBackground();
                }
            }
        };
        //------------------------------------------------------------------------------------------

        timer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) timer.cancel();
        timer = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isDismiss = true;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onLocationChanged(final Location location) {
        if (isDismiss || location == null) return;

        if (RootUtil.isMockLocationMode(getActivity(), location)) {
            dismissAllowingStateLoss();
            return;
        }

        if (!foundLocation.isEmpty()) {
            Location correctLocation = TakeLocationUtil.getCorrectLocation(foundLocation);
            if (correctLocation.getAccuracy() > location.getAccuracy()) {
                foundLocation.add(location);
            }
        } else {
            foundLocation.add(location);
        }

        final Location correctLocation = TakeLocationUtil.getCorrectLocation(foundLocation);

        final ArgTakeMyLocation arg = getArgTakeLocation();
        ProgressBar progressBar = vsRoot.id(R.id.progressbar);


        if (arg.requiredLocation && !progressBar.isIndeterminate()) {
            float locationAccuracy = correctLocation.getAccuracy();
            if (locationAccuracy > SettingLocation.ACCURACY) {
                vsRoot.id(R.id.tv_accuracy).setVisibility(View.VISIBLE);
                String accuracy = String.valueOf((int) correctLocation.getAccuracy());
                vsRoot.textView(R.id.tv_accuracy).setText(DS.getString(R.string.accuracy_in_meter, accuracy));
                startLocation();
                return;
            }
        }

        isDismiss = true;
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            int locationAccuracy = (int) correctLocation.getAccuracy();
            if (!arg.requiredLocation || locationAccuracy <= SettingLocation.ACCURACY) {
                TakeLocationListener f = Mold.getContentFragment(activity);
                if (f != null) {
                    f.onLocationToken(correctLocation);
                }
            } else {
                String correctAccuracy = NumberUtil.formatMoney(new BigDecimal(locationAccuracy));
                String settingAccuracy = NumberUtil.formatMoney(new BigDecimal(SettingLocation.ACCURACY));

                UI.dialog()
                        .title(R.string.warning)
                        .message(DS.getString(R.string.location_accuracy_error, correctAccuracy, settingAccuracy))
                        .positive(R.string.ok, new Command() {
                            @Override
                            public void apply() {
                                TakeLocationListener f = Mold.getContentFragment(activity);
                                f.onLocationToken(correctLocation);
                            }
                        }).show(activity);
            }
            dismissAllowingStateLoss();
        }
    }

    private void defineInTheBackground() {
        final FragmentActivity activity = getActivity();
        UI.dialog()
                .title(R.string.warning)
                .message(DS.getString(R.string.locations_will_determine_in_the_background))
                .positive(R.string.ok, new Command() {
                    @Override
                    public void apply() {
                        TakeLocationListener f = Mold.getContentFragment(activity);
                        f.onLocationToken(null);
                    }
                }).show(activity);
        dismissAllowingStateLoss();
    }
}
