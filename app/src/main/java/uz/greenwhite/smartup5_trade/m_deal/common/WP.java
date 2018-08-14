package uz.greenwhite.smartup5_trade.m_deal.common;// 30.06.2016


import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;

public class WP {

    public final String warehouseId;
    public final String priceTypeId;

    public WP(String warehouseId, String priceTypeId) {
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
    }

    public Tuple2 getKey() {
        return getKey(warehouseId, priceTypeId);
    }

    public static Tuple2 getKey(String warehouseId, String priceTypeId) {
        return new Tuple2(warehouseId, priceTypeId);
    }

    public static final MyMapper<WP, Tuple2> KEY_ADAPTER = new MyMapper<WP, Tuple2>() {
        @Override
        public Tuple2 apply(WP wp) {
            return getKey(wp.warehouseId, wp.priceTypeId);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WP wp = (WP) o;

        return priceTypeId.equals(wp.priceTypeId) && warehouseId.equals(wp.warehouseId);

    }

    @Override
    public int hashCode() {
        int result = Integer.parseInt(warehouseId);
        result = 31 * result + Integer.parseInt(priceTypeId);
        return result;
    }
}

