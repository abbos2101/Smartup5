package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class PersonAddress {

    public static final String ADDRESS_TYPE_WORK = "W";
    public static final String ADDRESS_TYPE_DELIVERY = "D";
    public static final String ADDRESS_TYPE_OTHERS = "O";
    public static final String ADDRESS_TYPE_HOME = "H";
    public static final String ADDRESS_TYPE_JURIDIC = "J";
    public static final String ADDRESS_TYPE_FACTUAL = "F";

    public final String typeAddress;
    public final String address;
    public final String postCode;
    public final Region region;

    public PersonAddress(String typeAddress, String address, String postCode, Region region) {
        this.typeAddress = Util.nvl(typeAddress);
        this.address = Util.nvl(address);
        this.postCode = Util.nvl(postCode);
        this.region = Util.nvl(region, Region.DEFAULT);
    }

    public static final PersonAddress DEFAULT = new PersonAddress(null, null, null, null);

    public static final UzumAdapter<PersonAddress> UZUM_ADAPTER = new UzumAdapter<PersonAddress>() {
        @Override
        public PersonAddress read(UzumReader in) {
            return new PersonAddress(in.readString(),
                    in.readString(), in.readString(),
                    in.readValue(Region.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonAddress val) {
            out.write(val.typeAddress);
            out.write(val.address);
            out.write(val.postCode);
            out.write(val.region, Region.UZUM_ADAPTER);
        }
    };
}
