package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class GiftFilterValue {

    public final String formCode;
    public final ProductFilterValue product;
    public final boolean hasValue;

    public GiftFilterValue(String formCode,
                           ProductFilterValue product,
                           boolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public static GiftFilterValue makeDefault(String formCode) {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new GiftFilterValue(formCode, p, false);
    }

    public static final MyMapper<GiftFilterValue, String> KEY_ADAPTER = new MyMapper<GiftFilterValue, String>() {
        @Override
        public String apply(GiftFilterValue orderFilterValue) {
            return orderFilterValue.formCode;
        }
    };

    public static final UzumAdapter<GiftFilterValue> UZUM_ADAPTER = new UzumAdapter<GiftFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public GiftFilterValue read(UzumReader in) {
            return new GiftFilterValue(
                    in.readString(),
                    in.readValue(ProductFilterValue.UZUM_ADAPTER),
                    in.readBoolean()
            );
        }

        @Override
        public void write(UzumWriter out, GiftFilterValue val) {
            out.write(val.formCode);
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.hasValue);
        }
    };
}
