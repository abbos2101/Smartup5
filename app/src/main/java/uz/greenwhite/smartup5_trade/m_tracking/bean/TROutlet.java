package uz.greenwhite.smartup5_trade.m_tracking.bean;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;

public class TROutlet {

    public static final String VISITED = "visited";
    public static final String NOT_VISITED = "not_visited";
    public static final String EXTRAORDINARY = "extraordinary";

    public final String outletId;
    public final String name;
    public final String address;
    public final String latLon;
    public final String state;
    public final MyArray<DealInfo> deals;

    public TROutlet(String outletId,
                    String name,
                    String address,
                    String latLon,
                    String state,
                    MyArray<DealInfo> deals) {
        this.outletId = outletId;
        this.name = name;
        this.address = Util.nvl(address);
        this.latLon = Util.nvl(latLon);
        this.state = state;
        this.deals = deals;
    }

    //----------------------------------------------------------------------------------------------

    public String getState() {
        switch (state) {
            case VISITED:
                return DS.getString(R.string.tracking_visited_info);
            case NOT_VISITED:
                return DS.getString(R.string.tracking_not_visited_info);
            case EXTRAORDINARY:
                return DS.getString(R.string.tracking_extraordinary_info);
            default:
                throw AppError.Unsupported();
        }
    }

    public int getStateIcon() {
        switch (state) {
            case VISITED:
                return R.drawable.flag2;
            case NOT_VISITED:
                return R.drawable.flag1;
            case EXTRAORDINARY:
                return R.drawable.flag3;
            default:
                throw AppError.Unsupported();
        }
    }

    public int getMarkerIcon() {
        MyArray<DealInfo> fdi = deals.filter(new MyPredicate<DealInfo>() {
            @Override
            public boolean apply(DealInfo dealInfo) {
                return !TextUtils.isEmpty(dealInfo.location);
            }
        });

        if (fdi.nonEmpty() && VISITED.equals(state)) {
            return R.drawable.marker5;

        } else if (!TextUtils.isEmpty(latLon) && VISITED.equals(state)) {
            return R.drawable.marker6;

        } else if (fdi.nonEmpty() && EXTRAORDINARY.equals(state)) {
            return R.drawable.marker1;

        } else if (!TextUtils.isEmpty(latLon) && EXTRAORDINARY.equals(state)) {
            return R.drawable.marker3;

        } else {
            return R.drawable.marker2;
        }
    }

    public LatLng getLatLng() {
        return LocationUtil.convertStringToLatLng(latLon);
    }

    public LatLng getTrackingLatLng() {
        MyArray<DealInfo> fdi = deals.filter(new MyPredicate<DealInfo>() {
            @Override
            public boolean apply(DealInfo dealInfo) {
                return !TextUtils.isEmpty(dealInfo.location);
            }
        });

        if (fdi.nonEmpty()) {
            return LocationUtil.convertStringToLatLng(fdi.get(0).location);
        }
        return getLatLng();
    }

    public boolean isTrackingLocationEmpty() {
        MyArray<DealInfo> fdi = deals.filter(new MyPredicate<DealInfo>() {
            @Override
            public boolean apply(DealInfo dealInfo) {
                return !TextUtils.isEmpty(dealInfo.location);
            }
        });

        return fdi.isEmpty() && TextUtils.isEmpty(this.latLon);
    }

    //----------------------------------------------------------------------------------------------

    public static final MyMapper<TROutlet, String> KEY_ADAPTER = new MyMapper<TROutlet, String>() {
        @Override
        public String apply(TROutlet TROutlet) {
            return TROutlet.outletId;
        }
    };

    public static final UzumAdapter<TROutlet> UZUM_ADAPTER = new UzumAdapter<TROutlet>() {

        @Override
        public TROutlet read(UzumReader in) {
            return new TROutlet(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readArray(DealInfo.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, TROutlet val) {
            out.write(val.outletId);
            out.write(val.name);
            out.write(val.address);
            out.write(val.latLon);
            out.write(val.state);
            out.write(val.deals, DealInfo.UZUM_ADAPTER);
        }
    };
}
