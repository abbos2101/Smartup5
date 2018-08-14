package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealPhoto {

    public final String sha;
    public final String date;
    public final String typeId;
    public final String latLng;
    public final String note;

    public DealPhoto(String sha,
                     String date,
                     String typeId,
                     String latLng,
                     String note) {
        this.sha = sha;
        this.date = date;
        this.typeId = typeId;
        this.latLng = latLng;
        this.note = note;
    }

    public static final MyMapper<DealPhoto, String> KEY_ADAPTER = new MyMapper<DealPhoto, String>() {
        @Override
        public String apply(DealPhoto val) {
            return val.sha;
        }
    };

    public static final UzumAdapter<DealPhoto> UZUM_ADAPTER = new UzumAdapter<DealPhoto>() {
        @Override
        public DealPhoto read(UzumReader in) {
            return new DealPhoto(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealPhoto val) {
            out.write(val.sha);
            out.write(val.date);
            out.write(val.typeId);
            out.write(val.latLng);
            out.write(val.note);
        }
    };
}
