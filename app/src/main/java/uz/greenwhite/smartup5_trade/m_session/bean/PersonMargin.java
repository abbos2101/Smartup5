package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonMargin {

    public final String personId;
    public final MyArray<String> marginIds;

    public PersonMargin(String personId, MyArray<String> marginIds) {
        this.personId = personId;
        this.marginIds = marginIds;
    }

    public static final MyMapper<PersonMargin,String> KEY_ADAPTER = new MyMapper<PersonMargin, String>() {
        @Override
        public String apply(PersonMargin personMargin) {
            return personMargin.personId;
        }
    };

    public static final UzumAdapter<PersonMargin> UZUM_ADAPTER = new UzumAdapter<PersonMargin>() {
        @Override
        public PersonMargin read(UzumReader in) {
            return new PersonMargin(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, PersonMargin val) {
            out.write(val.personId);
            out.write(val.marginIds, STRING_ARRAY);
        }
    };
}
