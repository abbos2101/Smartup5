package uz.greenwhite.smartup5_trade.m_session.bean.action;


import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ConditionRule {


    public final String ruleId;
    public final String cyclic;
    public final BigDecimal fromValue;
    public final BigDecimal toValue;
    public final MyArray<String> productIds;

    public ConditionRule(String ruleId,
                         String cyclic,
                         BigDecimal fromValue,
                         BigDecimal toValue,
                         MyArray<String> productIds) {
        this.ruleId = ruleId;
        this.cyclic = cyclic;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.productIds = productIds;
    }

    public static final UzumAdapter<ConditionRule> UZUM_ADAPTER = new UzumAdapter<ConditionRule>() {
        @Override
        public ConditionRule read(UzumReader in) {
            return new ConditionRule(in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readValue(STRING_ARRAY)
            );
        }

        @Override
        public void write(UzumWriter out, ConditionRule val) {
            out.write(val.ruleId);
            out.write(val.cyclic);
            out.write(val.fromValue);
            out.write(val.toValue);
            out.write(val.productIds, STRING_ARRAY);
        }
    };
}
