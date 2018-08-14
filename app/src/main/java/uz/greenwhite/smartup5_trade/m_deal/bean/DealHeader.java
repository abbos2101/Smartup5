package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;

public class DealHeader {

    public final String begunOn;
    public final String endedOn;
    public final int spendTime;
    public final String locLatLng;
    public final String deliveryDate;
    public final RoundModel roundModel;
    public final String contractNumberId;
    public final String expeditorId;
    public final String agentId;

    public DealHeader(String begunOn,
                      String endedOn,
                      int spendTime,
                      String locLatLng,
                      String deliveryDate,
                      RoundModel roundModel,
                      String contractNumberId,
                      String expeditorId,
                      String agentId) {
        this.begunOn = begunOn;
        this.endedOn = endedOn;
        this.spendTime = spendTime;
        this.locLatLng = Util.nvl(locLatLng);
        this.deliveryDate = Util.nvl(deliveryDate);
        this.roundModel = roundModel;
        this.contractNumberId = Util.nvl(contractNumberId);
        this.expeditorId = Util.nvl(expeditorId);
        this.agentId = Util.nvl(agentId);
    }

    public static DealHeader makeEmpty(RoundModel roundModel, String locLatLng) {
        return new DealHeader(null, null, 0,
                locLatLng, null, roundModel,
                null, null, null);
    }

    public static final UzumAdapter<DealHeader> UZUM_ADAPTER = new UzumAdapter<DealHeader>() {
        @Override
        public DealHeader read(UzumReader in) {
            return new DealHeader(in.readString(),
                    in.readString(), in.readInt(),
                    in.readString(), in.readString(),
                    RoundModel.make(in.readString()),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, DealHeader val) {
            out.write(val.begunOn);
            out.write(val.endedOn);
            out.write(val.spendTime);
            out.write(val.locLatLng);
            out.write(val.deliveryDate);
            out.write(val.roundModel.model);
            out.write(val.contractNumberId);
            out.write(val.expeditorId);
            out.write(val.agentId);
        }
    };
}
