package uz.greenwhite.smartup5_trade.m_session.bean.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonGroupType;

public class Overload {


    public static final String K_QUANT = "Q";
    public static final String K_AMOUNT = "A";
    public static final String K_WEIGHT = "W";

    public final String overloadId;
    public final String name;
    public final String beginDate;
    public final String endDate;
    public final String overloadKind;
    public final String cyclic;
    public final String productId;
    public final MyArray<PersonGroupType> groupTypes;
    public final MyArray<OverloadRule> rules;

    public Overload(String overloadId,
                    String name,
                    String beginDate,
                    String endDate,
                    String overloadKind,
                    String cyclic,
                    String productId,
                    MyArray<PersonGroupType> groupTypes,
                    MyArray<OverloadRule> rules) {
        this.overloadId = overloadId;
        this.name = name;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.overloadKind = overloadKind;
        this.cyclic = cyclic;
        this.productId = productId;
        this.groupTypes = groupTypes;
        this.rules = rules;
    }

    public static final MyMapper<Overload, String> KEY_ADAPTER = new MyMapper<Overload, String>() {
        @Override
        public String apply(Overload overload) {
            return overload.overloadId;
        }
    };

    public static final UzumAdapter<Overload> UZUM_ADAPTER = new UzumAdapter<Overload>() {
        @Override
        public Overload read(UzumReader in) {
            return new Overload(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readArray(PersonGroupType.UZUM_ADAPTER),
                    in.readArray(OverloadRule.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Overload val) {
            out.write(val.overloadId);
            out.write(val.name);
            out.write(val.beginDate);
            out.write(val.endDate);
            out.write(val.overloadKind);
            out.write(val.cyclic);
            out.write(val.productId);
            out.write(val.groupTypes, PersonGroupType.UZUM_ADAPTER);
            out.write(val.rules, OverloadRule.UZUM_ADAPTER);
        }
    };
}
