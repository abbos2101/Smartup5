package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Room {

    public final String id;
    public final String name;
    public final String psOrder;
    public final String psGift;
    public final String psStock;
    public final MyArray<String> productIds;
    public final MyArray<String> paymentTypeIds;
    public final MyArray<String> warehouseIds;
    public final MyArray<String> marginIds;
    public final MyArray<String> priceTypeIds;

    public Room(String id,
                String name,
                String psOrder,
                String psGift,
                String psStock,
                MyArray<String> paymentTypeIds,
                MyArray<String> warehouseIds,
                MyArray<String> marginIds,
                MyArray<String> priceTypeIds,
                MyArray<String> productIds) {
        this.id = id;
        this.name = name;
        this.psOrder = psOrder;
        this.psGift = psGift;
        this.psStock = psStock;
        this.productIds = MyArray.nvl(productIds);
        this.paymentTypeIds = MyArray.nvl(paymentTypeIds);
        this.warehouseIds = MyArray.nvl(warehouseIds);
        this.marginIds = MyArray.nvl(marginIds);
        this.priceTypeIds = MyArray.nvl(priceTypeIds);
    }

    public static final Room DEFAULT = new Room(
            "", "", "", "", "",
            MyArray.<String>emptyArray(), MyArray.<String>emptyArray(),
            MyArray.<String>emptyArray(), MyArray.<String>emptyArray(),
            MyArray.<String>emptyArray());

    public static final MyMapper<Room, String> KEY_ADAPTER = new MyMapper<Room, String>() {
        @Override
        public String apply(Room room) {
            return room.id;
        }
    };

    public static final UzumAdapter<Room> UZUM_ADAPTER = new UzumAdapter<Room>() {
        @Override
        public Room read(UzumReader in) {
            return new Room(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, Room val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.psOrder);
            out.write(val.psGift);
            out.write(val.psStock);
            out.write(val.paymentTypeIds, STRING_ARRAY);
            out.write(val.warehouseIds, STRING_ARRAY);
            out.write(val.marginIds, STRING_ARRAY);
            out.write(val.priceTypeIds, STRING_ARRAY);
            out.write(val.productIds, STRING_ARRAY);
        }
    };
}
