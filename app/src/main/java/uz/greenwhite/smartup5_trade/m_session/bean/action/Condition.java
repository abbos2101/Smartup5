package uz.greenwhite.smartup5_trade.m_session.bean.action;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Condition {

    public final String conditionId;
    public final MyArray<ConditionRule> rules;
    public final MyArray<ConditionBonus> bonuses;

    public Condition(String conditionId, MyArray<ConditionRule> rules, MyArray<ConditionBonus> bonuses) {
        this.conditionId = conditionId;
        this.rules = rules;
        this.bonuses = bonuses;
    }

    public static final UzumAdapter<Condition> UZUM_ADAPTER = new UzumAdapter<Condition>() {
        @Override
        public Condition read(UzumReader in) {
            return new Condition(in.readString(),
                    in.readArray(ConditionRule.UZUM_ADAPTER),
                    in.readArray(ConditionBonus.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Condition val) {
            out.write(val.conditionId);
            out.write(val.rules, ConditionRule.UZUM_ADAPTER);
            out.write(val.bonuses, ConditionBonus.UZUM_ADAPTER);
        }
    };
}
