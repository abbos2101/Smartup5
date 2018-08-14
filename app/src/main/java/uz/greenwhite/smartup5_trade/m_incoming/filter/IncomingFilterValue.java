package uz.greenwhite.smartup5_trade.m_incoming.filter;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class IncomingFilterValue {

    public final ProductFilterValue product;

    public IncomingFilterValue(ProductFilterValue product) {
        this.product = product;
    }


    public static IncomingFilterValue makeDefault() {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new IncomingFilterValue(p);
    }

    public static final UzumAdapter<IncomingFilterValue> UZUM_ADAPTER = new UzumAdapter<IncomingFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public IncomingFilterValue read(UzumReader in) {
            return new IncomingFilterValue(in.readValue(ProductFilterValue.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, IncomingFilterValue val) {
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
        }
    };
}
