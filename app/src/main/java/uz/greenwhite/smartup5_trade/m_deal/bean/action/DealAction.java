package uz.greenwhite.smartup5_trade.m_deal.bean.action;// 07.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;

public class DealAction {

    public final String actionId;
    public final String warehouseId;
    public final String bonusId;
    public final String productId;
    public final BigDecimal quantity;
    public final MyArray<CardQuantity> orderQuantities;
    public final String productUnitId;

    public DealAction(String actionId,
                      String warehouseId,
                      String productId,
                      BigDecimal quantity,
                      MyArray<CardQuantity> orderQuantities,
                      String bonusId,
                      String productUnitId) {
        this.actionId = actionId;
        this.warehouseId = warehouseId;
        this.bonusId = bonusId;
        this.productId = productId;
        this.quantity = quantity;
        this.orderQuantities = orderQuantities;
        this.productUnitId = Util.nvl(productUnitId);
    }

    public Tuple3 getKey() {
        return getKey(this.actionId, this.bonusId, this.productId);
    }

    public static Tuple3 getKey(String actionId, String bonusId, String productId) {
        return new Tuple3(actionId, bonusId, productId);
    }

    public static final MyMapper<DealAction, Tuple3> KEY_ADAPTER = new MyMapper<DealAction, Tuple3>() {
        @Override
        public Tuple3 apply(DealAction dealAction) {
            return dealAction.getKey();
        }
    };

    public static final UzumAdapter<DealAction> UZUM_ADAPTER = new UzumAdapter<DealAction>() {
        @Override
        public DealAction read(UzumReader in) {
            return new DealAction(in.readString(),
                    in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readArray(CardQuantity.UZUM_ADAPTER),
                    in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, DealAction val) {
            out.write(val.actionId);
            out.write(val.warehouseId);
            out.write(val.productId);
            out.write(val.quantity);
            out.write(val.orderQuantities, CardQuantity.UZUM_ADAPTER);
            out.write(val.bonusId);
            out.write(val.productUnitId);
        }
    };
}
