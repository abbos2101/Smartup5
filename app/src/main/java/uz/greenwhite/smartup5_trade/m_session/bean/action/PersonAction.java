package uz.greenwhite.smartup5_trade.m_session.bean.action;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonGroupType;

public class PersonAction {

    public static final String K_QUANT = "Q";
    public static final String K_AMOUNT = "A";
    public static final String K_WEIGHT = "W";

    public final String actionId;
    public final String warehouseId;
    public final String name;
    public final String actionKind;
    public final String startDate;
    public final String endDate;
    public final MyArray<PersonGroupType> groupTypes;
    public final MyArray<Condition> conditions;

    public PersonAction(String actionId,
                        String warehouseId,
                        String name,
                        String actionKind,
                        String startDate,
                        String endDate,
                        MyArray<PersonGroupType> groupTypes,
                        MyArray<Condition> conditions) {
        this.actionId = actionId;
        this.warehouseId = warehouseId;
        this.name = name;
        this.actionKind = actionKind;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupTypes = groupTypes;
        this.conditions = conditions;
    }

    public static final MyMapper<PersonAction, String> KEY_ADAPTER = new MyMapper<PersonAction, String>() {
        @Override
        public String apply(PersonAction action) {
            return action.actionId;
        }
    };

    public static final UzumAdapter<PersonAction> UZUM_ADAPTER = new UzumAdapter<PersonAction>() {
        @Override
        public PersonAction read(UzumReader in) {
            return new PersonAction(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(),
                    in.readArray(PersonGroupType.UZUM_ADAPTER),
                    in.readArray(Condition.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonAction val) {
            out.write(val.actionId);
            out.write(val.warehouseId);
            out.write(val.name);
            out.write(val.actionKind);
            out.write(val.startDate);
            out.write(val.endDate);
            out.write(val.groupTypes, PersonGroupType.UZUM_ADAPTER);
            out.write(val.conditions, Condition.UZUM_ADAPTER);
        }
    };
}
