package uz.greenwhite.smartup5_trade.m_session.bean.action;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ConditionBonus {

    public final String bonusId;
    public final MyArray<BonusProduct> products;

    public ConditionBonus(String bonusId, MyArray<BonusProduct> products) {
        this.bonusId = bonusId;
        this.products = products;
    }

    public static final UzumAdapter<ConditionBonus> UZUM_ADAPTER = new UzumAdapter<ConditionBonus>() {
        @Override
        public ConditionBonus read(UzumReader in) {
            return new ConditionBonus(in.readString(),
                    in.readArray(BonusProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, ConditionBonus val) {
            out.write(val.bonusId);
            out.write(val.products, BonusProduct.UZUM_ADAPTER);
        }
    };
}
