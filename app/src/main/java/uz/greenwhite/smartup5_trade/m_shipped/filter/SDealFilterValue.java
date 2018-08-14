package uz.greenwhite.smartup5_trade.m_shipped.filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SDealFilterValue {

    public final MyArray<SOrderFilterValue> orders;

    public SDealFilterValue(MyArray<SOrderFilterValue> orders) {
        this.orders = MyArray.nvl(orders);
    }

    public static final SDealFilterValue DEFAULT = new SDealFilterValue(null);

    public static final UzumAdapter<SDealFilterValue> UZUM_ADAPTER = new UzumAdapter<SDealFilterValue>() {
        @Override
        public SDealFilterValue read(UzumReader in) {
            return new SDealFilterValue(in.readArray(SOrderFilterValue.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, SDealFilterValue val) {
            out.write(val.orders, SOrderFilterValue.UZUM_ADAPTER);
        }
    };
}
