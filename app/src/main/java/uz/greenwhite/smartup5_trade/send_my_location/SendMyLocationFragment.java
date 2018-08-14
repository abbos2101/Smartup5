package uz.greenwhite.smartup5_trade.send_my_location;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Locale;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldNewLocationFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.RootUtil;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;
import uz.greenwhite.smartup5_trade.m_session.job.LoadAddressJob;
import uz.greenwhite.smartup5_trade.m_session.job.LoadPhotoJob;

public class SendMyLocationFragment extends MoldNewLocationFragment {

    public static void open(ArgSession arg) {
        Mold.openContent(SendMyLocationFragment.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    private ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;
    private JobMate jobMate = new JobMate();
    private Location myLocation;
    private BottomSheetBehavior bottomSheet;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.duty_current_location);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.send_my_location);

        ViewSetup vs = new ViewSetup(getActivity(), R.layout.duty_send_location_footer);
        this.bottomSheet = Mold.makeBottomSheet(getActivity(), vs.view);
        this.bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        vs.button(R.id.btn_refresh_current_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeCurrentLocation();
            }
        });

        vs.button(R.id.btn_send_current_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMyLocation();
            }
        });

//       Takes current location data automatically
        takeCurrentLocation();
    }

    public void takeCurrentLocation() {
        enableProgressBar(true);
        startLocation();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onLocationChanged(final Location location) {
        if (location == null) {
            Toast.makeText(getActivity(), getString(R.string.unable_to_determine_your_location),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (RootUtil.isMockLocationMode(getActivity(), location)) {
            return;
        }
        if (bottomSheet.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        myLocation = location;
        showLocationInformation();
    }

    private void enableProgressBar(boolean enable) {
        vsRoot.id(R.id.iv_current_location).setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        vsRoot.id(R.id.progress).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void showLocationInformation() {
        if (jobMate == null && !isAdded()) {
            return;
        }
        final LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        final String dates = getDataToString();
        final float accuracy = myLocation.getAccuracy();

        vsRoot.textView(R.id.tv_accuracy).setText(getString(R.string.accuracy, String.valueOf(accuracy)));

        jobMate.execute(new LoadAddressJob(latLng)).done(new Promise.OnDone<Address>() {
            @Override
            public void onDone(Address result) {
                vsRoot.textView(R.id.tv_current_location).setText(String.valueOf(result.getFeatureName()));
                vsRoot.textView(R.id.tv_address).setText(String.valueOf(result.getLocality()));
                vsRoot.textView(R.id.tv_date).setText(String.valueOf(dates));

            }
        });

        String url = "http://maps.googleapis.com/maps/api/staticmap?markers="
                + myLocation.getLatitude() + "," + myLocation.getLongitude()
                + "&zoom=14&size=900x900&sensor=false";

        jobMate.execute(new LoadPhotoJob(url)).done(new Promise.OnDone<Bitmap>() {
            @Override
            public void onDone(Bitmap result) {
                vsRoot.imageView(R.id.iv_current_location).setImageBitmap(result);
                enableProgressBar(false);
            }
        });
    }

    private String getDataToString() {
        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(myLocation.getTime());
        return DateUtil.format(cal.getTime(), DateUtil.FORMAT_AS_DATETIME);
    }

    private void sendMyLocation() {
        if (myLocation == null) {
            takeCurrentLocation();
            return;
        }

        String location = myLocation.getLatitude() + "," + myLocation.getLongitude();
        String date = getDataToString();
        MyArray<String> map = MyArray.from(
                location, date, String.valueOf(myLocation.getAccuracy()),
                String.valueOf(myLocation.getSpeed()), "gps".equals(myLocation.getProvider()) ? "G" : "O");

        jobMate.executeWithDialog(getActivity(), new ActionJob<>(getArgSession(), RT.URI_CHECKIN_LOCATION, map))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolve, String result, Throwable error) {
                        if (resolve) showSuccessDialog();
                        else
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(error).message);
                    }
                });
    }

    private void showSuccessDialog() {
        UI.dialog()
                .cancelable(false)
                .title(R.string.sent)
                .message(R.string.you_location_has_been_successfully_sent)
                .positive(R.string.ok, new Command() {
                    @Override
                    public void apply() {
                        getActivity().finish();
                    }
                }).show(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    public void onCancelLocationSetting() {
        Manager.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        }, 100);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
}
