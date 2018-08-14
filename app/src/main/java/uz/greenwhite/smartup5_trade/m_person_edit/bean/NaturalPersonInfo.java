package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class NaturalPersonInfo {

    public final String filialId;
    public final String roomId;
    public final NaturalPerson naturalPerson;
    public final NaturalPersonAdditionally naturalPersonAdditionally;
    public final MyArray<GroupType> characts;
    public final MyArray<PersonAddress> addresses;

    public NaturalPersonInfo(String filialId,
                             String roomId,
                             NaturalPerson naturalPerson,
                             NaturalPersonAdditionally naturalPersonAdditionally,
                             MyArray<GroupType> characts,
                             MyArray<PersonAddress> addresses) {
        this.filialId = filialId;
        this.roomId = roomId;
        this.naturalPerson = naturalPerson;
        this.naturalPersonAdditionally = naturalPersonAdditionally;
        this.characts = characts;
        this.addresses = addresses;
    }

    public NaturalPersonInfo(String filialId,
                             String roomId,
                             NaturalPerson naturalPerson,
                             NaturalPersonAdditionally naturalPersonAdditionally,
                             MyArray<GroupType> characts) {
        this(filialId, roomId, naturalPerson, naturalPersonAdditionally, characts, MyArray.<PersonAddress>emptyArray());
    }

    public static NaturalPersonInfo makeDefault(String filialId, String roomId) {
        return new NaturalPersonInfo(
                filialId,
                roomId,
                NaturalPerson.DEFAULT,
                NaturalPersonAdditionally.DEFAULT,
                MyArray.<GroupType>emptyArray(),
                MyArray.<PersonAddress>emptyArray());
    }

    public static final UzumAdapter<NaturalPersonInfo> UZUM_ADAPTER = new UzumAdapter<NaturalPersonInfo>() {
        @Override
        public NaturalPersonInfo read(UzumReader in) {
            return new NaturalPersonInfo(
                    in.readString(),
                    in.readString(),
                    in.readValue(NaturalPerson.UZUM_ADAPTER),
                    in.readValue(NaturalPersonAdditionally.UZUM_ADAPTER),
                    in.readArray(GroupType.UZUM_ADAPTER),
                    in.readArray(PersonAddress.UZUM_ADAPTER)
            );
        }

        @Override
        public void write(UzumWriter out, NaturalPersonInfo val) {
            out.write(val.filialId);
            out.write(val.roomId);
            out.write(val.naturalPerson, NaturalPerson.UZUM_ADAPTER);
            out.write(val.naturalPersonAdditionally, NaturalPersonAdditionally.UZUM_ADAPTER);
            out.write(val.characts, GroupType.UZUM_ADAPTER);
            out.write(val.addresses, PersonAddress.UZUM_ADAPTER); // refactoring qilinganda ochirish kerak ozgargan mexanizm
        }
    };
}
