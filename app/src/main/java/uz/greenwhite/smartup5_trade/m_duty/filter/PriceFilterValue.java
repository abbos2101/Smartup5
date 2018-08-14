package uz.greenwhite.smartup5_trade.m_duty.filter;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class PriceFilterValue {

    public final ProductFilterValue product;

    public PriceFilterValue(ProductFilterValue product) {
        this.product = product;
    }

    public static PriceFilterValue makeDefault() {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new PriceFilterValue(p);
    }

    public static final UzumAdapter<PriceFilterValue> UZUM_ADAPTER = new UzumAdapter<PriceFilterValue>() {
        @Override
        public PriceFilterValue read(UzumReader in) {
            return new PriceFilterValue(in.readValue(ProductFilterValue.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PriceFilterValue val) {
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
        }
    };
}
