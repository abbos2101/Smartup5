package uz.greenwhite.smartup5_trade.m_person_edit.bean;


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonEditGroupType {

    public final String typeId;
    public final String name;

    public PersonEditGroupType(String typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    public static final MyMapper<PersonEditGroupType, String> KEY_ADAPTER = new MyMapper<PersonEditGroupType, String>() {
        @Override
        public String apply(PersonEditGroupType personEditGroupType) {
            return personEditGroupType.typeId;
        }
    };

    public static final UzumAdapter<PersonEditGroupType> UZUM_ADAPTER = new UzumAdapter<PersonEditGroupType>() {
        @Override
        public PersonEditGroupType read(UzumReader in) {
            return new PersonEditGroupType(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonEditGroupType val) {
            out.write(val.typeId);
            out.write(val.name);
        }
    };
}
