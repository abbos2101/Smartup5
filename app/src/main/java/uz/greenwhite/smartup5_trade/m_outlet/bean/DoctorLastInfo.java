package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DoctorLastInfo {

    public final String personId;
    public final MyArray<DoctorLastAgree> doctorLastAgrees;


    public DoctorLastInfo(String personId, MyArray<DoctorLastAgree> doctorLastAgrees) {
        this.personId = personId;
        this.doctorLastAgrees = doctorLastAgrees;
    }

    public static final MyMapper<DoctorLastInfo, String> KEY_ADAPTER = new MyMapper<DoctorLastInfo, String>() {
        @Override
        public String apply(DoctorLastInfo doctorLastInfo) {
            return doctorLastInfo.personId;
        }
    };

    public static final UzumAdapter<DoctorLastInfo> UZUM_ADAPTER = new UzumAdapter<DoctorLastInfo>() {
        @Override
        public DoctorLastInfo read(UzumReader in) {
            return new DoctorLastInfo(in.readString(), in.readArray(DoctorLastAgree.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DoctorLastInfo val) {
            out.write(val.personId);
            out.write(val.doctorLastAgrees, DoctorLastAgree.UZUM_ADAPTER);
        }
    };
}
