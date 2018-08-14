package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductLastInputPrice {

    public final String currencyId;
    public final MyArray<ProductLastPrice> prices;

    public ProductLastInputPrice(String currencyId, MyArray<ProductLastPrice> prices) {
        this.currencyId = currencyId;
        this.prices = prices;
    }

    public static final UzumAdapter<ProductLastInputPrice> UZUM_ADAPTER = new UzumAdapter<ProductLastInputPrice>() {
        @Override
        public ProductLastInputPrice read(UzumReader in) {
            return new ProductLastInputPrice(in.readString(),
                    in.readArray(ProductLastPrice.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, ProductLastInputPrice val) {
            out.write(val.currencyId);
            out.write(val.prices, ProductLastPrice.UZUM_ADAPTER);
        }
    };
}
