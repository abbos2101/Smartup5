package uz.greenwhite.smartup5_trade.m_shipped.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SOrderFilterValue {

    public final String formCode;
    public final boolean hasValue;

    public SOrderFilterValue(String formCode, boolean hasValue) {
        this.formCode = formCode;
        this.hasValue = hasValue;
    }

    public static SOrderFilterValue makeDefault(String formCode) {
        return new SOrderFilterValue(formCode, false);
    }

    public static final MyMapper<SOrderFilterValue, String> KEY_ADAPTER = new MyMapper<SOrderFilterValue, String>() {
        @Override
        public String apply(SOrderFilterValue val) {
            return val.formCode;
        }
    };

    public static final UzumAdapter<SOrderFilterValue> UZUM_ADAPTER = new UzumAdapter<SOrderFilterValue>() {
        @Override
        public SOrderFilterValue read(UzumReader in) {
            return new SOrderFilterValue(in.readString(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SOrderFilterValue val) {
            out.write(val.formCode);
            out.write(val.hasValue);
        }
    };
}
