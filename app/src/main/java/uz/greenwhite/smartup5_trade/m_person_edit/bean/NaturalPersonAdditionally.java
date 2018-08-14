package uz.greenwhite.smartup5_trade.m_person_edit.bean;// 20.12.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Specialty;

public class NaturalPersonAdditionally {

    public final Specialty specialty;
    public final Hospital hospital;
    public final String cabinet;
    public final String latLng;

    public NaturalPersonAdditionally(Specialty specialty, Hospital hospital, String cabinet, String latLng) {
        this.specialty = Util.nvl(specialty, Specialty.DEFAULT);
        this.hospital = Util.nvl(hospital, Hospital.DEFAULT);
        this.cabinet = Util.nvl(cabinet);
        this.latLng = Util.nvl(latLng);
    }

    public static final NaturalPersonAdditionally DEFAULT = new NaturalPersonAdditionally(null, null, null, null);

    public static final UzumAdapter<NaturalPersonAdditionally> UZUM_ADAPTER = new UzumAdapter<NaturalPersonAdditionally>() {
        @Override
        public NaturalPersonAdditionally read(UzumReader in) {
            return new NaturalPersonAdditionally(
                    in.readValue(Specialty.UZUM_ADAPTER),
                    in.readValue(Hospital.UZUM_ADAPTER),
                    in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, NaturalPersonAdditionally val) {
            out.write(val.specialty, Specialty.UZUM_ADAPTER);
            out.write(val.hospital, Hospital.UZUM_ADAPTER);
            out.write(val.cabinet);
            out.write(val.latLng);
        }
    };
}
