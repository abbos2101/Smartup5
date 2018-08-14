package uz.greenwhite.smartup5_trade.m_session.job;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.smartup5_trade.SmartupApp;

public class LoadAddressJob implements ShortJob<Address> {

    public final LatLng latLng;

    public LoadAddressJob(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public Address execute() throws Exception {
        Geocoder geocoder = new Geocoder(SmartupApp.getContext(), Locale.getDefault());
        List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        return address.get(0);
    }
}
