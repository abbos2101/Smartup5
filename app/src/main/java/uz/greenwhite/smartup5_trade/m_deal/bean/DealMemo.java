package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealMemo {

    public final String memo;

    public DealMemo(String memo) {
        this.memo = memo;
    }

    public static final UzumAdapter<DealMemo> UZUM_ADAPTER = new UzumAdapter<DealMemo>() {
        @Override
        public DealMemo read(UzumReader in) {
            return new DealMemo(in.readString());
        }

        @Override
        public void write(UzumWriter out, DealMemo val) {
            out.write(val.memo);
        }
    };

}
