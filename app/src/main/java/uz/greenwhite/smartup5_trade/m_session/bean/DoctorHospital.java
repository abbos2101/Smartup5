package uz.greenwhite.smartup5_trade.m_session.bean;


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DoctorHospital {

    public final String id;
    public final String name;
    public final String shortName;

    public DoctorHospital(String id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public static final UzumAdapter<DoctorHospital> UZUM_ADAPTER = new UzumAdapter<DoctorHospital>() {
        @Override
        public DoctorHospital read(UzumReader in) {
            return new DoctorHospital(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DoctorHospital val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.shortName);
        }
    };

    public static final MyMapper<DoctorHospital, String> KEY_ADAPTER = new MyMapper<DoctorHospital, String>() {
        @Override
        public String apply(DoctorHospital hospital) {
            return hospital.id;
        }
    };
}
