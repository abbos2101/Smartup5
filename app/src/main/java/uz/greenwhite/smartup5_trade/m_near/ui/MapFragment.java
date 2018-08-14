package uz.greenwhite.smartup5_trade.m_near.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;
import uz.greenwhite.smartup5_trade.m_near.bean.MapItem;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;

public class MapFragment extends MoldContentFragment {

    public static MapFragment newInstance(ArgNearOutlet arg) {
        return Mold.parcelableArgumentNewInstance(MapFragment.class,
                arg, ArgNearOutlet.UZUM_ADAPTER);
    }

    private SupportMapFragment mapView;
    private LatLngBounds bounds;
    private Marker oldMarker;
    private int oldState = 0;
    private List<MapItem> mapList;
    private MapItem selectPlace;

    public ArgNearOutlet getArgNearOutlet() {
        return Mold.parcelableArgument(this, ArgNearOutlet.UZUM_ADAPTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ViewSetup(inflater, container, R.layout.near_map).view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();

        this.mapView = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
        if (this.mapView == null) {
            this.mapView = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapView, this.mapView).commit();
        }

        MapsInitializer.initialize(getActivity().getApplicationContext());
        this.mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initSettingMap(googleMap);
            }
        });

        MapUtil.checkPlayServices(getActivity());
    }

    private void initSettingMap(@NonNull GoogleMap googleMap) {
        final ArgNearOutlet args = getArgNearOutlet();
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().equals(args.getLatLng())) {
                    return true;
                }
                if (oldMarker != null) {
                    oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                    oldMarker.hideInfoWindow();
                }
                if (marker.equals(oldMarker)) {
                    oldMarker = null;
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_select));
                marker.showInfoWindow();
                oldMarker = marker;

                selectPlace = getPlaceFromMarker(marker);

                NearOutletFragment contentFragment = Mold.getContentFragment(getActivity());
                MapListFragment mapList = (MapListFragment) contentFragment.getContentFragment(R.id.map_list);
                mapList.setMapScreen(true, selectPlace);
                return true;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                NearOutletFragment contentFragment = Mold.getContentFragment(getActivity());
                MapListFragment mapList = (MapListFragment) contentFragment.getContentFragment(R.id.map_list);
                mapList.setMapScreen(true, selectPlace);
            }
        });
    }

    public void setSearch(List<MapItem> argNearMap) {
        this.mapList = argNearMap;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (MapItem nearMap : mapList) {
                    Marker marker;

                    marker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(nearMap.latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                                    .anchor(0.5F, 0.5F));
                    nearMap.marker = (marker);
                    marker.hideInfoWindow();
                }

                ArgNearOutlet args = getArgNearOutlet();
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(args.getLatLng()));

                try {
                    LatLngBounds.Builder builderOfBounds = new LatLngBounds.Builder();
                    for (MapItem nearMap : mapList) {
                        builderOfBounds.include(nearMap.latLng);
                    }
                    builderOfBounds.include(args.getLatLng());
                    bounds = builderOfBounds.build();
                    selectMarker(mapList.get(0), true);
                    setCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setCamera() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
                int dist = ((height * 65) / 100);
                googleMap.setPadding(0, 0, 0, dist);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
            }
        });
    }

    public void resetCamera(final int state) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();

                int dist = ((height * 65) / 100);
                try {
                    switch (state) {
                        case 1:
                            if (state != oldState) {
                                googleMap.setPadding(0, 0, 0, 0);
                                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 110));
                            }
                            break;
                        case 2:
                            googleMap.setPadding(0, 0, 0, dist);
                            googleMap.getUiSettings().setRotateGesturesEnabled(false);
                            googleMap.getUiSettings().setScrollGesturesEnabled(false);
                            googleMap.getUiSettings().setZoomGesturesEnabled(false);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                oldState = state;
            }
        });

    }

    public void selectMarker(final MapItem nearMap, boolean firstTime) {
        try {
            int index = this.mapList.indexOf(nearMap);
            if (index == -1) return;
            this.selectPlace = this.mapList.get(index);
            int indexIfInMapList = mapList.indexOf(nearMap);
            if (indexIfInMapList == -1) return;
            Marker marker = mapList.get(indexIfInMapList).marker;
            if (oldMarker != null) {
                oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                oldMarker.hideInfoWindow();
            }
            if (marker.equals(oldMarker)) {
                oldMarker = null;
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_select));
            if (!firstTime) {
                marker.showInfoWindow();
            }
            oldMarker = marker;
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(nearMap.latLng));
                }
            });

        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
        }
    }

    private MapItem getPlaceFromMarker(Marker marker) {
        MapItem result = null;
        for (MapItem nearMap : mapList) {
            if (marker.equals(nearMap.marker)) {
                result = nearMap;
            }
        }
        return result;
    }
}
