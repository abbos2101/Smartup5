package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonPriceType {

    public final String personId;
    public final MyArray<String> priceTypeIds;

    public PersonPriceType(String personId, MyArray<String> priceTypeIds) {
        this.personId = personId;
        this.priceTypeIds = priceTypeIds;
    }

    public static final MyMapper<PersonPriceType, String> KEY_ADAPTER = new MyMapper<PersonPriceType, String>() {
        @Override
        public String apply(PersonPriceType personPriceType) {
            return personPriceType.personId;
        }
    };

    public static final UzumAdapter<PersonPriceType> UZUM_ADAPTER = new UzumAdapter<PersonPriceType>() {
        @Override
        public PersonPriceType read(UzumReader in) {
            return new PersonPriceType(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, PersonPriceType val) {
            out.write(val.personId);
            out.write(val.priceTypeIds, STRING_ARRAY);
        }
    };
}
