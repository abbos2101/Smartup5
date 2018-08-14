package uz.greenwhite.smartup5_trade.m_tracking.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Tracking {

    public final MyArray<TROutlet> trackingOutlet;
    public final MyArray<TRGps> agentTracking;

    public Tracking(MyArray<TROutlet> trackingOutlet, MyArray<TRGps> agentTracking) {
        this.trackingOutlet = trackingOutlet;
        this.agentTracking = agentTracking;
    }

    public static final UzumAdapter<Tracking> UZUM_ADAPTER = new UzumAdapter<Tracking>() {
        @Override
        public Tracking read(UzumReader in) {
            return new Tracking(in.readArray(TROutlet.UZUM_ADAPTER),
                    in.readArray(TRGps.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Tracking val) {
            out.write(val.trackingOutlet, TROutlet.UZUM_ADAPTER);
            out.write(val.agentTracking, TRGps.UZUM_ADAPTER);

        }

    };
}

