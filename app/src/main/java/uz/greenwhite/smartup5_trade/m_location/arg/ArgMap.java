package uz.greenwhite.smartup5_trade.m_location.arg;// 14.10.2016

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgMap extends ArgSession {

    public final String location;

    public ArgMap(ArgSession arg, String location) {
        super(arg.accountId, arg.filialId);
        this.location = location;
    }

    public ArgMap(UzumReader in) {
        super(in);
        this.location = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.location);
    }

    public LatLng getLocation() {
        if (TextUtils.isEmpty(this.location)) return null;

        try {
            String[] latLng = this.location.split(",");
            return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            e.printStackTrace();
            return null;
        }
    }

    public static final UzumAdapter<ArgMap> UZUM_ADAPTER = new UzumAdapter<ArgMap>() {
        @Override
        public ArgMap read(UzumReader in) {
            return new ArgMap(in);
        }

        @Override
        public void write(UzumWriter out, ArgMap val) {
            val.write(out);
        }
    };
}
