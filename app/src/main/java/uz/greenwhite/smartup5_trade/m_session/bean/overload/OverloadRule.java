package uz.greenwhite.smartup5_trade.m_session.bean.overload;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OverloadRule {

    public final String ruleId;
    public final BigDecimal fromValue, toValue;
    public final MyArray<OverloadLoad> loads;

    public OverloadRule(String ruleId, BigDecimal fromValue, BigDecimal toValue, MyArray<OverloadLoad> loads) {
        this.ruleId = ruleId;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.loads = loads;
    }

    public static final UzumAdapter<OverloadRule> UZUM_ADAPTER = new UzumAdapter<OverloadRule>() {
        @Override
        public OverloadRule read(UzumReader in) {
            return new OverloadRule(in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readArray(OverloadLoad.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OverloadRule val) {
            out.write(val.ruleId);
            out.write(val.fromValue);
            out.write(val.toValue);
            out.write(val.loads, OverloadLoad.UZUM_ADAPTER);
        }
    };
}
