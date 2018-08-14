package uz.greenwhite.smartup5_trade.m_location.ui;// 14.10.2016

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldLocationFragment;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_location.arg.ArgMap;

public class LocationFragment extends MoldLocationFragment {

    public static LocationFragment newInstance(ArgMap arg) {
        return Mold.parcelableArgumentNewInstance(LocationFragment.class, arg, ArgMap.UZUM_ADAPTER);
    }

    public ArgMap getArgMap() {
        return Mold.parcelableArgument(this, ArgMap.UZUM_ADAPTER);
    }

    private SupportMapFragment mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.z_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.map);

        ArgMap arg = getArgMap();
        if (!TextUtils.isEmpty(arg.location)) {
            setFragmentData(arg.getLocation());
        }

        addMenu(R.drawable.ic_done_black_24dp, R.string.choose, new Command() {
            @Override
            public void apply() {
                LatLng latLng = getFragmentData();
                Mold.popContent(getActivity(), latLng);
            }
        });

        FragmentManager fm = getChildFragmentManager();
        mapView = (SupportMapFragment) fm.findFragmentById(R.id.ll_map);
        if (mapView == null) {
            mapView = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.ll_map, mapView).commit();
        }
        mapView.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        initMap();
    }

    @SuppressWarnings({"deprecation", "MissingPermission"})
    private void initMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = getFragmentData();
                if (latLng != null) {
                    googleMap.addMarker(new MarkerOptions().position(latLng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100));
                }

                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        setNewLocation(latLng);
                    }
                });
            }
        });
    }

    private void setNewLocation(final LatLng latLng) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100));
            }
        });
        setFragmentData(latLng);
    }


    @Override
    public void onConnected(Bundle bundle) {
        ArgMap arg = getArgMap();
        if (TextUtils.isEmpty(arg.location)) {
            requestLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setNewLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onCancelLocationSetting() {
        Toast.makeText(getActivity(), DS.getString(R.string.location_rejected_by_user), Toast.LENGTH_SHORT).show();
    }
}
