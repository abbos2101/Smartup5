package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonInfo {

    public static final String K_SHOP = "S";
    public static final String K_DOCTOR = "D";
    public static final String K_HOSPITAL = "H";
    public static final String K_PHARMACY = "P";

    public final String filialId;
    public final String roomId;
    public final LegalPerson legalPerson;
    public final PersonProps props;
    public final MyArray<GroupType> personCharacts;
    public final MyArray<PersonAddress> personAddresses;
    public final MyArray<PersonAccount> personAccounts;
    public final String personKind;

    public PersonInfo(String filialId,
                      String roomId,
                      LegalPerson legalPerson,
                      PersonProps props,
                      MyArray<GroupType> personCharacts,
                      MyArray<PersonAddress> personAddresses,
                      MyArray<PersonAccount> personAccounts,
                      String personKind) {

        this.filialId = filialId;
        this.roomId = roomId;
        this.legalPerson = legalPerson;
        this.props = props;
        this.personCharacts = personCharacts;
        this.personAddresses = personAddresses;
        this.personAccounts = personAccounts;
        this.personKind = Util.nvl(personKind, K_SHOP);

    }

    public static PersonInfo makeDefault(String filialId, String roomId, String personKind) {
        return new PersonInfo(
                filialId,
                roomId,
                LegalPerson.DEFAULT,
                PersonProps.DEFAULT,
                MyArray.<GroupType>emptyArray(),
                MyArray.<PersonAddress>emptyArray(),
                MyArray.<PersonAccount>emptyArray(),
                personKind);
    }

    public static final UzumAdapter<PersonInfo> UZUM_ADAPTER = new UzumAdapter<PersonInfo>() {
        @Override
        public PersonInfo read(UzumReader in) {
            return new PersonInfo(
                    in.readString(),
                    in.readString(),
                    in.readValue(LegalPerson.UZUM_ADAPTER),
                    in.readValue(PersonProps.UZUM_ADAPTER),
                    in.readArray(GroupType.UZUM_ADAPTER),
                    in.readArray(PersonAddress.UZUM_ADAPTER),
                    in.readArray(PersonAccount.UZUM_ADAPTER),
                    in.readString()
            );
        }

        @Override
        public void write(UzumWriter out, PersonInfo val) {
            out.write(val.filialId);
            out.write(val.roomId);
            out.write(val.legalPerson, LegalPerson.UZUM_ADAPTER);
            out.write(val.props, PersonProps.UZUM_ADAPTER);
            out.write(val.personCharacts, GroupType.UZUM_ADAPTER);
            out.write(val.personAddresses, PersonAddress.UZUM_ADAPTER);
            out.write(val.personAccounts, PersonAccount.UZUM_ADAPTER);
            out.write(val.personKind);

        }
    };
}
