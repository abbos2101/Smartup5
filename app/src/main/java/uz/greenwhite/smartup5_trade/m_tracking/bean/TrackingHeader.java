package uz.greenwhite.smartup5_trade.m_tracking.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class TrackingHeader {

    public final String outletPlan;
    public final String visited;
    public final String extraordinary;
    public final MyArray<TROutlet> outlets;
    public final MyArray<TRGps> locations;

    public TrackingHeader(String outletPlan,
                          String visited,
                          String extraordinary,
                          MyArray<TROutlet> outlets,
                          MyArray<TRGps> locations) {
        this.outletPlan = outletPlan;
        this.visited = visited;
        this.extraordinary = extraordinary;
        this.outlets = outlets;
        this.locations = locations;
    }

    public MyArray<TRGps> getTrackingLocations() {
        return this.locations.filter(new MyPredicate<TRGps>() {
            @Override
            public boolean apply(TRGps trGps) {
                return TRGps.TRACKING.equals(trGps.state);
            }
        });
    }

    public static final UzumAdapter<TrackingHeader> UZUM_ADAPTER = new UzumAdapter<TrackingHeader>() {
        @Override
        public TrackingHeader read(UzumReader in) {
            return new TrackingHeader(in.readString(), in.readString(),
                    in.readString(), in.readArray(TROutlet.UZUM_ADAPTER),
                    in.readArray(TRGps.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, TrackingHeader val) {
            out.write(val.outletPlan);
            out.write(val.visited);
            out.write(val.extraordinary);
            out.write(val.outlets, TROutlet.UZUM_ADAPTER);
            out.write(val.locations, TRGps.UZUM_ADAPTER);
        }
    };
}
