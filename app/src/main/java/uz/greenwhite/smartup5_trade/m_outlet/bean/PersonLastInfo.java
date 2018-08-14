package uz.greenwhite.smartup5_trade.m_outlet.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonLastInfo {

    public final String personId;
    public final String lastMemo;
    public final String lastShipment;
    public final String lastVisit;

    public PersonLastInfo(String personId, String lastMemo, String lastShipment, String lastVisit) {
        this.personId = personId;
        this.lastMemo = lastMemo;
        this.lastShipment = lastShipment;
        this.lastVisit = lastVisit;
    }

    public boolean hasLastDate() {
        return !TextUtils.isEmpty(lastVisit);
    }

    public static final MyMapper<PersonLastInfo,String> KEY_ADAPTER = new MyMapper<PersonLastInfo, String>() {
        @Override
        public String apply(PersonLastInfo personLastInfo) {
            return personLastInfo.personId;
        }
    };

    public static final UzumAdapter<PersonLastInfo> UZUM_ADAPTER = new UzumAdapter<PersonLastInfo>() {
        @Override
        public PersonLastInfo read(UzumReader in) {
            return new PersonLastInfo(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonLastInfo val) {
            out.write(val.personId);
            out.write(val.lastMemo);
            out.write(val.lastShipment);
            out.write(val.lastVisit);
        }
    };
}
