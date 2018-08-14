package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class StockFilterValue {

    public final String formCode;
    public final ProductFilterValue product;
    public final boolean hasValue;

    public StockFilterValue(String formCode,
                            ProductFilterValue product,
                            boolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public static StockFilterValue makeDefault(String formCode) {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new StockFilterValue(formCode, p, false);
    }

    public static final MyMapper<StockFilterValue, String> KEY_ADAPTER = new MyMapper<StockFilterValue, String>() {
        @Override
        public String apply(StockFilterValue orderFilterValue) {
            return orderFilterValue.formCode;
        }
    };

    public static final UzumAdapter<StockFilterValue> UZUM_ADAPTER = new UzumAdapter<StockFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public StockFilterValue read(UzumReader in) {
            return new StockFilterValue(
                    in.readString(),
                    in.readValue(ProductFilterValue.UZUM_ADAPTER),
                    in.readBoolean()
            );
        }

        @Override
        public void write(UzumWriter out, StockFilterValue val) {
            out.write(val.formCode);
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.hasValue);
        }
    };
}
