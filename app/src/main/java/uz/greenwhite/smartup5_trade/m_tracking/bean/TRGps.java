package uz.greenwhite.smartup5_trade.m_tracking.bean;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;

public class TRGps {

    public static final String TRACKING = "T";
    public static final String CHECKIN = "C";

    public final String id;
    public final String time;
    public final String latLng;
    public final String accuracy;
    public final String speed;
    public final String state;

    public TRGps(String id, String time, String latLng, String accuracy, String speed, String state) {
        this.id = id;
        this.time = time;
        this.latLng = latLng;
        this.accuracy = accuracy;
        this.speed = speed;
        this.state = state;
    }

    public LatLng getLatLng() {
        return LocationUtil.convertStringToLatLng(latLng);
    }

    public String getTime() {
        return time.split(" ")[1];
    }

    public static final MyMapper<TRGps, String> KEY_ADAPTER = new MyMapper<TRGps, String>() {
        @Override
        public String apply(TRGps val) {
            return val.id;
        }
    };

    public static final UzumAdapter<TRGps> UZUM_ADAPTER = new UzumAdapter<TRGps>() {

        @Override
        public TRGps read(UzumReader in) {
            return new TRGps(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, TRGps val) {
            out.write(val.id);
            out.write(val.time);
            out.write(val.latLng);
            out.write(val.accuracy);
            out.write(val.speed);
            out.write(val.state);
        }
    };
}
