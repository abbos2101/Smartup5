package uz.greenwhite.smartup5_trade.m_deal.bean.overload;// 07.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;

public class DealOverload {

    public final String overloadId;
    public final String warehouseId;
    public final String priceTypeId;
    public final String cardCode;
    public final String loadId;
    public final String productId;
    public final BigDecimal quantity;
    public final BigDecimal price;
    public final MyArray<CardQuantity> orderQuantities;
    public final String currencyId;
    public final String productUnitId;

    public DealOverload(String overloadId,
                        String warehouseId,
                        String priceTypeId,
                        String cardCode,
                        String loadId,
                        String productId,
                        BigDecimal quantity,
                        BigDecimal price,
                        MyArray<CardQuantity> orderQuantities,
                        String currencyId,
                        String productUnitId) {
        this.overloadId = overloadId;
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
        this.cardCode = cardCode;
        this.loadId = loadId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.orderQuantities = orderQuantities;
        this.currencyId = currencyId;
        this.productUnitId = Util.nvl(productUnitId);
    }

    public Tuple3 getKey() {
        return getKey(this.overloadId, this.loadId, this.productId);
    }

    public static Tuple3 getKey(String overloadId, String loadId, String productId) {
        return new Tuple3(overloadId, loadId, productId);
    }

    public static final MyMapper<DealOverload, Tuple3> KEY_ADAPTER = new MyMapper<DealOverload, Tuple3>() {
        @Override
        public Tuple3 apply(DealOverload dealOverload) {
            return dealOverload.getKey();
        }
    };

    public static final UzumAdapter<DealOverload> UZUM_ADAPTER = new UzumAdapter<DealOverload>() {
        @Override
        public DealOverload read(UzumReader in) {
            return new DealOverload(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(),
                    in.readArray(CardQuantity.UZUM_ADAPTER),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealOverload val) {
            out.write(val.overloadId);
            out.write(val.warehouseId);
            out.write(val.priceTypeId);
            out.write(val.cardCode);
            out.write(val.loadId);
            out.write(val.productId);
            out.write(val.quantity);
            out.write(val.price);
            out.write(val.orderQuantities, CardQuantity.UZUM_ADAPTER);
            out.write(val.currencyId);
            out.write(val.productUnitId);
        }
    };
}
