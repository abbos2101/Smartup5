package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SpecialityProduct {

    public final String specialityId;
    public final MyArray<String> productIds;

    public SpecialityProduct(String specialityId,
                             MyArray<String> productIds) {
        this.specialityId = specialityId;
        this.productIds = productIds;
    }

    public static final MyMapper<SpecialityProduct, String> KEY_ADAPTER = new MyMapper<SpecialityProduct, String>() {
        @Override
        public String apply(SpecialityProduct specialityProduct) {
            return specialityProduct.specialityId;
        }
    };

    public static final UzumAdapter<SpecialityProduct> UZUM_ADAPTER = new UzumAdapter<SpecialityProduct>() {
        @Override
        public SpecialityProduct read(UzumReader in) {
            return new SpecialityProduct(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, SpecialityProduct val) {
            out.write(val.specialityId);
            out.write(val.productIds, STRING_ARRAY);
        }
    };
}
