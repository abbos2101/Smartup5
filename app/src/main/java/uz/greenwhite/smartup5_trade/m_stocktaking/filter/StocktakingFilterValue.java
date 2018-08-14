package uz.greenwhite.smartup5_trade.m_stocktaking.filter;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class StocktakingFilterValue {

    public final ProductFilterValue product;
    public final String cardCode;
    public final boolean hasValue;

    public StocktakingFilterValue(ProductFilterValue product, String cardCode, boolean hasValue) {
        this.product = product;
        this.cardCode = cardCode;
        this.hasValue = hasValue;
    }


    public static StocktakingFilterValue makeDefault() {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new StocktakingFilterValue(p, "", false);
    }

    public static final UzumAdapter<StocktakingFilterValue> UZUM_ADAPTER = new UzumAdapter<StocktakingFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public StocktakingFilterValue read(UzumReader in) {
            return new StocktakingFilterValue(in.readValue(ProductFilterValue.UZUM_ADAPTER),
                    in.readString(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, StocktakingFilterValue val) {
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.cardCode);
            out.write(val.hasValue);
        }
    };
}
