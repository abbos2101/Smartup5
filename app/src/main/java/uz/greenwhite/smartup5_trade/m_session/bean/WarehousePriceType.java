package uz.greenwhite.smartup5_trade.m_session.bean;// 31.10.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class WarehousePriceType {

    public final String warehouseId;
    public final MyArray<String> priceTypeIds;

    public WarehousePriceType(String warehouseId, MyArray<String> priceTypeIds) {
        this.warehouseId = warehouseId;
        this.priceTypeIds = MyArray.nvl(priceTypeIds);
    }

    public static final MyMapper<WarehousePriceType, String> KEY_ADAPTER = new MyMapper<WarehousePriceType, String>() {
        @Override
        public String apply(WarehousePriceType val) {
            return val.warehouseId;
        }
    };

    public static final UzumAdapter<WarehousePriceType> UZUM_ADAPTER = new UzumAdapter<WarehousePriceType>() {
        @Override
        public WarehousePriceType read(UzumReader in) {
            return new WarehousePriceType(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, WarehousePriceType val) {
            out.write(val.warehouseId);
            out.write(val.priceTypeIds, STRING_ARRAY);
        }
    };
}
