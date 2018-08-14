package uz.greenwhite.smartup5_trade.m_person_edit.bean;// 20.12.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class LegalPerson {

    public final String personId;
    public final String name;
    public final String shortName;
    public final String code;
    public final String address;
    public final String addressGuide;
    public final String phone;
    public final String email;
    public final String location;
    public final String barcode;
    public final Region region;
    public final Hospital hospital;
    public final String zipCode;

    public LegalPerson(String personId,
                       String name,
                       String code,
                       String address,
                       String addressGuide,
                       String phone,
                       String email,
                       String location,
                       String barcode,
                       Region region,
                       Hospital hospital,
                       String shortName,
                       String zipCode) {
        this.personId = Util.nvl(personId);
        this.name = Util.nvl(name);
        this.shortName = Util.nvl(shortName);
        this.address = Util.nvl(address);
        this.addressGuide = Util.nvl(addressGuide);
        this.code = Util.nvl(code);
        this.phone = Util.nvl(phone);
        this.email = Util.nvl(email);
        this.location = Util.nvl(location);
        this.barcode = Util.nvl(barcode);
        this.region = Util.nvl(region, Region.DEFAULT);
        this.hospital = Util.nvl(hospital, Hospital.DEFAULT);
        this.zipCode = Util.nvl(zipCode);
    }

    public static final LegalPerson DEFAULT = new LegalPerson(
            null, null, null, null, null,
            null, null, null, null,
            null, null, null, null);

    public static final UzumAdapter<LegalPerson> UZUM_ADAPTER = new UzumAdapter<LegalPerson>() {
        @Override
        public LegalPerson read(UzumReader in) {
            return new LegalPerson(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readValue(Region.UZUM_ADAPTER),
                    in.readValue(Hospital.UZUM_ADAPTER),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, LegalPerson val) {
            out.write(val.personId);
            out.write(val.name);
            out.write(val.address);
            out.write(val.addressGuide);
            out.write(val.code);
            out.write(val.phone);
            out.write(val.email);
            out.write(val.location);
            out.write(val.barcode);
            out.write(val.region, Region.UZUM_ADAPTER);
            out.write(val.hospital, Hospital.UZUM_ADAPTER);
            out.write(val.shortName);
            out.write(val.zipCode);
        }
    };
}
