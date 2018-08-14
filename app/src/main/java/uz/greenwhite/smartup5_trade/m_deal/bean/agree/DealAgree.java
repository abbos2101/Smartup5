package uz.greenwhite.smartup5_trade.m_deal.bean.agree;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealAgree {

    public final String productId;
    public final String oldCurValue;
    public final String oldNewValue;
    public final String curValue;
    public final String newValue;
    public final String period;

    public DealAgree(String productId, String oldCurValue, String oldNewValue,
                     String curValue, String newValue, String period) {
        this.productId = productId;
        this.oldCurValue = Util.nvl(oldCurValue, "");
        this.oldNewValue = Util.nvl(oldNewValue, "");
        this.curValue = Util.nvl(curValue, "");
        this.newValue = Util.nvl(newValue, "");
        this.period = Util.nvl(period, "");
    }

    public static final MyMapper<DealAgree, String> KEY_ADAPTER = new MyMapper<DealAgree, String>() {
        @Override
        public String apply(DealAgree val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<DealAgree> UZUM_ADAPTER = new UzumAdapter<DealAgree>() {
        @Override
        public DealAgree read(UzumReader in) {
            return new DealAgree(in.readString(), in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealAgree val) {
            out.write(val.productId);
            out.write(val.oldCurValue);
            out.write(val.oldNewValue);
            out.write(val.curValue);
            out.write(val.newValue);
            out.write(val.period);
        }
    };
}
