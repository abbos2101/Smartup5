package uz.greenwhite.smartup5_trade.m_person_edit.bean;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonEditGroup {

    public final String groupId;
    public final String name;
    public final MyArray<PersonEditGroupType> types;


    public PersonEditGroup(String groupId, String name, MyArray<PersonEditGroupType> types) {
        this.groupId = groupId;
        this.name = name;
        this.types = types;
    }

    public static final MyMapper<PersonEditGroup, String> KEY_ADAPTER = new MyMapper<PersonEditGroup, String>() {
        @Override
        public String apply(PersonEditGroup val) {
            return val.groupId;
        }
    };

    public static final UzumAdapter<PersonEditGroup> UZUM_ADAPTER = new UzumAdapter<PersonEditGroup>() {
        @Override
        public PersonEditGroup read(UzumReader in) {
            return new PersonEditGroup(in.readString(), in.readString(),
                    in.readArray(PersonEditGroupType.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonEditGroup val) {
            out.write(val.groupId);
            out.write(val.name);
            out.write(val.types, PersonEditGroupType.UZUM_ADAPTER);
        }
    };
}
