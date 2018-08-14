package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DoctorLastAgree {

    public final String productId;
    public final String curQuant;
    public final String newQuant;
    public final String period;

    public DoctorLastAgree(String productId, String curQuant, String newQuant, String period) {
        this.productId = productId;
        this.curQuant = curQuant;
        this.newQuant = newQuant;
        this.period = period;
    }

    public static final MyMapper<DoctorLastAgree, String> KEY_ADAPTER = new MyMapper<DoctorLastAgree, String>() {
        @Override
        public String apply(DoctorLastAgree doctorLastAgree) {
            return doctorLastAgree.productId;
        }
    };

    public static final UzumAdapter<DoctorLastAgree> UZUM_ADAPTER = new UzumAdapter<DoctorLastAgree>() {
        @Override
        public DoctorLastAgree read(UzumReader in) {
            return new DoctorLastAgree(in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DoctorLastAgree val) {
            out.write(val.productId);
            out.write(val.curQuant);
            out.write(val.newQuant);
            out.write(val.period);
        }
    };

}
