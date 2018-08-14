package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Memo {

    public final String memo;
    public final String date;

    public Memo(String memo, String date) {
        this.memo = memo;
        this.date = date;
    }

    public static final UzumAdapter<Memo> UZUM_ADAPTER = new UzumAdapter<Memo>() {
        @Override
        public Memo read(UzumReader in) {
            return new Memo(in.readString(),in.readString());
        }

        @Override
        public void write(UzumWriter out, Memo val) {
            out.write(val.memo);
            out.write(val.date);
        }
    };
}
