package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonGroupType {

    public final String groupId;
    public final String typeId;


    public PersonGroupType(String groupId, String typeId) {
        this.groupId = groupId;
        this.typeId = typeId;
    }

    public static final PersonGroupType DEFAULT = new PersonGroupType("", "");

    public static final MyMapper<PersonGroupType, String> KEY_ADAPTER = new MyMapper<PersonGroupType, String>() {
        @Override
        public String apply(PersonGroupType outletCharact) {
            return outletCharact.groupId;
        }
    };

    public static final UzumAdapter<PersonGroupType> UZUM_ADAPTER = new UzumAdapter<PersonGroupType>() {
        @Override
        public PersonGroupType read(UzumReader in) {
            return new PersonGroupType(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonGroupType val) {
            out.write(val.groupId);
            out.write(val.typeId);
        }
    };

}
