package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class GroupType {

    public final String groupId;
    public final String name;
    public final String typeId;
    public final String typeName;


    public GroupType(String groupId, String name, String typeId, String typeName) {
        this.groupId = groupId;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public static final GroupType DEFAULT = new GroupType("", "", "", "");

    public static final MyMapper<GroupType, String> KEY_ADAPTER = new MyMapper<GroupType, String>() {
        @Override
        public String apply(GroupType outletCharact) {
            return outletCharact.groupId;
        }
    };

    public static final UzumAdapter<GroupType> UZUM_ADAPTER = new UzumAdapter<GroupType>() {
        @Override
        public GroupType read(UzumReader in) {
            return new GroupType(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, GroupType val) {
            out.write(val.groupId);
            out.write(val.name);
            out.write(val.typeId);
            out.write(val.typeName);
        }
    };

    public PersonEditGroup toPersonEditGroup() {
        return new PersonEditGroup(this.groupId, this.name,
                MyArray.from(new PersonEditGroupType(this.typeId, this.typeName)));
    }

}
