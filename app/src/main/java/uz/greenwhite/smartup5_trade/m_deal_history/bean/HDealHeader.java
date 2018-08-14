package uz.greenwhite.smartup5_trade.m_deal_history.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class HDealHeader {

    public final String begunOn;
    public final String endedOn;
    public final String speedTime;
    public final String location;
    public final String deliveryDate;
    public final String roundModel;
    public final String contractId;
    public final String expeditorId;

    public HDealHeader(String begunOn,
                       String endedOn,
                       String speedTime,
                       String location,
                       String deliveryDate,
                       String roundModel,
                       String contractId,
                       String expeditorId) {
        this.begunOn = begunOn;
        this.endedOn = endedOn;
        this.speedTime = speedTime;
        this.location = location;
        this.deliveryDate = deliveryDate;
        this.roundModel = roundModel;
        this.contractId = contractId;
        this.expeditorId = expeditorId;
    }

    public static final UzumAdapter<HDealHeader> UZUM_ADAPTER = new UzumAdapter<HDealHeader>() {
        @Override
        public HDealHeader read(UzumReader in) {
            return new HDealHeader(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter uzumWriter, HDealHeader hDealHeader) {
            uzumWriter.write(hDealHeader.begunOn);
            uzumWriter.write(hDealHeader.endedOn);
            uzumWriter.write(hDealHeader.speedTime);
            uzumWriter.write(hDealHeader.location);
            uzumWriter.write(hDealHeader.deliveryDate);
            uzumWriter.write(hDealHeader.roundModel);
            uzumWriter.write(hDealHeader.contractId);
            uzumWriter.write(hDealHeader.expeditorId);
        }
    };
}
