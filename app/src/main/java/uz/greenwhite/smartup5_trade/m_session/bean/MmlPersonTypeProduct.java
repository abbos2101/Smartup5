package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class MmlPersonTypeProduct {

    public final String personTypeId;
    public final String productId;

    public MmlPersonTypeProduct(String personTypeId, String productId) {
        this.personTypeId = personTypeId;
        this.productId = productId;
    }

    public static final UzumAdapter<MmlPersonTypeProduct> UZUM_ADAPTER = new UzumAdapter<MmlPersonTypeProduct>() {
        @Override
        public MmlPersonTypeProduct read(UzumReader in) {
            return new MmlPersonTypeProduct(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, MmlPersonTypeProduct val) {
            out.write(val.personTypeId);
            out.write(val.productId);
        }
    };
}
