package uz.greenwhite.smartup5_trade.m_deal.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealGift {

    public final String productId;
    public final String warehouseId;
    public final BigDecimal quantity;
    public final MyArray<CardQuantity> orderQuantities;
    public final String productUnitId;

    public DealGift(String productId,
                    String warehouseId,
                    BigDecimal quantity,
                    MyArray<CardQuantity> orderQuantities,
                    String productUnitId) {
        AppError.checkNull(productId, warehouseId, quantity);

        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.orderQuantities = Util.nvl(orderQuantities, MyArray.<CardQuantity>emptyArray());
        this.productUnitId = Util.nvl(productUnitId);

        if (orderQuantities != null) {
            orderQuantities.checkUniqueness(CardQuantity.KEY_ADAPTER);
        }
    }

    public static Tuple2 getKey(String productId, String warehouseId) {
        return new Tuple2(productId, warehouseId);
    }

    public static final MyMapper<DealGift, Tuple2> KEY_ADAPTER = new MyMapper<DealGift, Tuple2>() {
        @Override
        public Tuple2 apply(DealGift val) {
            return getKey(val.productId, val.warehouseId);
        }
    };

    public static final UzumAdapter<DealGift> UZUM_ADAPTER = new UzumAdapter<DealGift>() {
        @Override
        public DealGift read(UzumReader in) {
            return new DealGift(in.readString(), in.readString(),
                    in.readBigDecimal(), in.readArray(CardQuantity.UZUM_ADAPTER),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, DealGift val) {
            out.write(val.productId);
            out.write(val.warehouseId);
            out.write(val.quantity);
            out.write(val.orderQuantities, CardQuantity.UZUM_ADAPTER);
            out.write(val.productUnitId);
        }
    };
}
