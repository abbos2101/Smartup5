package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class AgreeFilterValue {

    public final String formCode;
    public final ProductFilterValue product;
    public final boolean hasValue;

    public AgreeFilterValue(String formCode,
                            ProductFilterValue product,
                            boolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public static AgreeFilterValue makeDefault(String formCode, boolean mhl) {
        ProductFilterValue p = new ProductFilterValue(null, false, false, mhl, false);
        return new AgreeFilterValue(formCode, p, false);
    }

    public static final AgreeFilterValue DEFAULT = new AgreeFilterValue(null, null, false);

    public static final MyMapper<AgreeFilterValue, String> KEY_ADAPTER = new MyMapper<AgreeFilterValue, String>() {
        @Override
        public String apply(AgreeFilterValue filterValue) {
            return filterValue.formCode;
        }
    };


    public static final UzumAdapter<AgreeFilterValue> UZUM_ADAPTER = new UzumAdapter<AgreeFilterValue>() {
        @Override
        public AgreeFilterValue read(UzumReader in) {
            return new AgreeFilterValue(in.readString(), in.readValue(ProductFilterValue.UZUM_ADAPTER), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, AgreeFilterValue val) {
            out.write(val.formCode);
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.hasValue);
        }
    };
}
