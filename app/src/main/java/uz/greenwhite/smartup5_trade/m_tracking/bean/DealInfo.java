package uz.greenwhite.smartup5_trade.m_tracking.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealInfo {

    public final String dealId;
    public final String visitDate;
    public final String location;
    public final String state;

    public DealInfo(String dealId, String visitDate, String location, String state) {
        this.dealId = dealId;
        this.visitDate = visitDate;
        this.location = location;
        this.state = state;
    }

    public static final MyMapper<DealInfo, String> KEY_ADAPTER = new MyMapper<DealInfo, String>() {
        @Override
        public String apply(DealInfo dealInfo) {
            return dealInfo.dealId;
        }
    };

    public static final UzumAdapter<DealInfo> UZUM_ADAPTER = new UzumAdapter<DealInfo>() {
        @Override
        public DealInfo read(UzumReader in) {
            return new DealInfo(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealInfo val) {
            out.write(val.dealId);
            out.write(val.visitDate);
            out.write(val.location);
            out.write(val.state);
        }
    };
}
