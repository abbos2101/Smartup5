package uz.greenwhite.smartup5_trade.m_deal.bean.service;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealService {

    public final String productId, priceTypeId, currencyId;
    public final BigDecimal price, quantity, margin;
    public final String productUnitId;

    public DealService(String productId,
                       String priceTypeId,
                       BigDecimal price,
                       BigDecimal quantity,
                       BigDecimal margin,
                       String currencyId,
                       String productUnitId) {
        this.productId = productId;
        this.priceTypeId = priceTypeId;
        this.currencyId = currencyId;
        this.price = price;
        this.quantity = quantity;
        this.margin = margin;
        this.productUnitId = Util.nvl(productUnitId);
    }

    public static Tuple2 getKey(String productId, String priceTypeId) {
        return new Tuple2(productId, priceTypeId);
    }

    public static final MyMapper<DealService, Tuple2> KEY_ADAPTER = new MyMapper<DealService, Tuple2>() {
        @Override
        public Tuple2 apply(DealService val) {
            return getKey(val.productId, val.priceTypeId);
        }
    };

    public static final UzumAdapter<DealService> UZUM_ADAPTER = new UzumAdapter<DealService>() {
        @Override
        public DealService read(UzumReader in) {
            return new DealService(in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealService val) {
            out.write(val.productId);       // 1
            out.write(val.priceTypeId);     // 2
            out.write(val.price);           // 3
            out.write(val.quantity);        // 4
            out.write(val.margin);          // 5
            out.write(val.currencyId);      // 6
            out.write(val.productUnitId);   // 7
        }
    };
}
