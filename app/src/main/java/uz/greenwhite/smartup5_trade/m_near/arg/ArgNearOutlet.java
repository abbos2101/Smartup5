package uz.greenwhite.smartup5_trade.m_near.arg;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;


public class ArgNearOutlet extends ArgSession {

    public final String latitude;
    public final String longitude;
    public final MyArray<String> outletIds;

    public ArgNearOutlet(ArgSession arg, LatLng latLng, MyArray<String> outletIds) {
        super(arg.accountId, arg.filialId);
        this.latitude = String.valueOf(latLng.latitude);
        this.longitude = String.valueOf(latLng.longitude);
        this.outletIds = MyArray.nvl(outletIds);
    }

    public static ArgNearOutlet create(ArgSession arg, Location location, MyArray<String> outletIds) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return new ArgNearOutlet(arg, latLng, outletIds);
    }

    public ArgNearOutlet(UzumReader in) {
        super(in);
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.outletIds = in.readValue(UzumAdapter.STRING_ARRAY);
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(latitude),
                Double.parseDouble(longitude));
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.latitude);
        w.write(this.longitude);
        w.write(this.outletIds, UzumAdapter.STRING_ARRAY);
    }

    public static final UzumAdapter<ArgNearOutlet> UZUM_ADAPTER = new UzumAdapter<ArgNearOutlet>() {
        @Override
        public ArgNearOutlet read(UzumReader in) {
            return new ArgNearOutlet(in);
        }

        @Override
        public void write(UzumWriter out, ArgNearOutlet val) {
            val.write(out);
        }
    };
}
