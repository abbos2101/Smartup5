package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.SeekBarChangeListener;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;
import uz.greenwhite.smartup5_trade.m_tracking.TrackingApi;
import uz.greenwhite.smartup5_trade.m_tracking.arg.ArgTracking;
import uz.greenwhite.smartup5_trade.m_tracking.bean.DealInfo;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TRGps;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TROutlet;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingHeader;

public class TrackingMapFragment extends MoldContentFragment {

    public static TrackingMapFragment newInstance(ArgTracking arg) {
        return Mold.parcelableArgumentNewInstance(TrackingMapFragment.class, arg, ArgTracking.UZUM_ADAPTER);
    }

    public ArgTracking getArgTracking() {
        return Mold.parcelableArgument(this, ArgTracking.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    private SupportMapFragment mapFragment;
    private MyArray<Marker> markers = MyArray.emptyArray();

    private BottomSheetBehavior bottomSheetBehavior;
    private ViewSetup vsFooter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.tracking_map);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.session_tracking_mp);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                UiSettings us = googleMap.getUiSettings();
                us.setZoomControlsEnabled(false);
                us.setMyLocationButtonEnabled(true);
            }
        });

        LocationUtil.checkGoogleMapsLocation(this);

        this.vsFooter = new ViewSetup(getActivity(), R.layout.tracking_seekbar);
        this.bottomSheetBehavior = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        init();
    }

    void init() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        final String key = marker.getTitle();
                        if (!TextUtils.isEmpty(key)) {
                            TrackingData data = Mold.getData(getActivity());
                            TrackingHeader tr = data.getTracking();
                            if (tr == null) return;
                            MyArray<DealInfo> dealItems = TrackingApi.makeDealItem(tr.outlets, key);
                            if (dealItems.nonEmpty()) {
                                TROutlet toFound = tr.outlets.find(key, TROutlet.KEY_ADAPTER);
                                TrackingApi.showDealInfo(getActivity(), dealItems, toFound.latLon, getArgTracking());
                            }
                        }
                    }
                });

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        final String key = marker.getTitle();
                        if (!TextUtils.isEmpty(key)) {
                            TrackingData data = Mold.getData(getActivity());
                            TrackingHeader tr = data.getTracking();
                            if (tr == null) return null;
                            final MyArray<DealInfo> dealItems = TrackingApi.makeDealItem(tr.outlets, key);
                            if (dealItems.nonEmpty()) {
                                final TROutlet toFound = tr.outlets.find(key, TROutlet.KEY_ADAPTER);

                                ViewSetup vs = new ViewSetup(getActivity(), R.layout.tracking_map_title);
                                vs.textView(R.id.title).setText(Html.fromHtml(
                                        getString(R.string.tracking_map_title, toFound.name, toFound.address)
                                ));

                                vs.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TrackingApi.showDealInfo(getActivity(), dealItems, toFound.latLon, getArgTracking());
                                    }
                                });
                                vs.textView(R.id.detail).setText(Html.fromHtml(TrackingApi.getOutletDetail(toFound)));
                                return vs.view;
                            }
                        }
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });


            }
        });
    }

    //----------------------------------------------------------------------------------------------

    public void prepareTrackingResult() {
        TrackingData data = Mold.getData(getActivity());
        TrackingHeader tracking = data.getTracking();
        if (tracking == null) return;
        vsRoot.textView(R.id.v_detail).setText(tracking.visited + "/" + tracking.outletPlan);
        vsRoot.textView(R.id.e_detail).setText(tracking.extraordinary);
        vsRoot.id(R.id.ll_extraordinary).setVisibility("0".equals(tracking.extraordinary) ? View.GONE : View.VISIBLE);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.clear();
                TrackingData data = Mold.getData(getActivity());
                TrackingHeader tracking = data.getTracking();
                if (tracking == null) return;
                MyArray<TROutlet> finalResult = tracking.outlets.filter(new MyPredicate<TROutlet>() {
                    @Override
                    public boolean apply(TROutlet to) {
                        return !TROutlet.NOT_VISITED.equals(to.state) || !to.isTrackingLocationEmpty();
                    }
                });

                final Setter<Boolean> hasLocationBounds = new Setter<>();
                hasLocationBounds.value = false;
                final LatLngBounds.Builder builderOfBounds = new LatLngBounds.Builder();
                if (data.outletLocation.getValue()) {
                    markers = finalResult.map(new MyMapper<TROutlet, Marker>() {
                        @Override
                        public Marker apply(TROutlet item) {
                            LatLng trackingLatLng = item.getTrackingLatLng();
                            if (trackingLatLng == null) return null;
                            builderOfBounds.include(trackingLatLng);
                            hasLocationBounds.value = true;
                            return googleMap.addMarker(
                                    new MarkerOptions()
                                            .title(item.outletId)
                                            .position(trackingLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(item.getMarkerIcon()))
                                            .anchor(0.5F, 0.5F));
                        }
                    }).filterNotNull();
                }
                MyArray<TRGps> trackingLocations = tracking.getTrackingLocations();
                if (data.userTracking.getValue()) {
                    if (trackingLocations.isEmpty()) {
                        Mold.makeSnackBar(getActivity(), R.string.tracking_gps_tracking_is_missing).show();
                        vsFooter.view.setVisibility(View.GONE);
                    } else {
                        PolylineOptions options = newPolylineOptions();
                        for (int i = 0; i < trackingLocations.size(); i++) {
                            TRGps ta = trackingLocations.get(i);
                            LatLng latLng = ta.getLatLng();
                            options.add(latLng);
                            builderOfBounds.include(latLng);
                            hasLocationBounds.value = true;
                        }
                        googleMap.addPolyline(options);

                        showLocationSeekBar(googleMap, trackingLocations);
                    }
                } else {
                    vsFooter.view.setVisibility(View.GONE);
                }

                if ((data.outletLocation.getValue() && finalResult.nonEmpty()) ||
                        data.userTracking.getValue() && trackingLocations.nonEmpty()) {
                    if (hasLocationBounds.value) {
                        setCamera(builderOfBounds.build());
                    }
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private void showLocationSeekBar(final GoogleMap googleMap, final MyArray<TRGps> at) {

        final TRGps trGps = at.get(at.size() - 1);
        final Marker trackMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(trGps.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .anchor(0.5F, 0.5F));

        vsFooter.view.setVisibility(View.VISIBLE);

        SeekBar sbTracking = vsFooter.id(R.id.sb_gps_tracking);
        sbTracking.setMax((at.size() - 1));
        sbTracking.setProgress(sbTracking.getMax());
        sbTracking.setOnSeekBarChangeListener(new SeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress) {
                TRGps item = at.get(progress);
                trackMarker.setPosition(item.getLatLng());
                vsFooter.textView(R.id.sb_start_time).setText(item.getTime());


                CameraPosition position = new CameraPosition.Builder().
                        target(item.getLatLng()).zoom(15).build();

                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new TrackingTuningFragment();
    }

    public void prepareMapType() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                TrackingData data = Mold.getData(getActivity());
                googleMap.setMapType(data.mapType.getValue().getId());
            }
        });
    }

    private PolylineOptions newPolylineOptions() {
        return new PolylineOptions()
                .width(5)
                .color(Color.BLUE)
                .geodesic(true);
    }

    public void setCamera(final LatLngBounds bounds) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
            }
        });
    }

    public void showInMarker(final String key, final LatLng latLng) {
        if (latLng == null) return;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                String outletId = key;
                if (TextUtils.isEmpty(outletId)) outletId = Util.nvl(outletId);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                for (Marker m : markers) {
                    String markerKey = m.getTitle();
                    LatLng position = m.getPosition();
                    if (position.equals(latLng) && outletId.equals(markerKey)) {
                        m.showInfoWindow();
                    }
                }
            }
        });

    }
}
