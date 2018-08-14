package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonProductSet {
    public final String personId;
    public final String psOrder;
    public final String psGift;
    public final String psStock;

    public PersonProductSet(String personId, String psOrder, String psGift, String psStock) {
        this.personId = personId;
        this.psOrder = psOrder;
        this.psGift = psGift;
        this.psStock = psStock;
    }

    public static final MyMapper<PersonProductSet, String> KEY_ADAPTER = new MyMapper<PersonProductSet, String>() {
        @Override
        public String apply(PersonProductSet personProductSet) {
            return personProductSet.personId;
        }
    };

    public static final UzumAdapter<PersonProductSet> UZUM_ADAPTER = new UzumAdapter<PersonProductSet>() {
        @Override
        public PersonProductSet read(UzumReader in) {
            return new PersonProductSet(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonProductSet val) {
            out.write(val.personId);
            out.write(val.psOrder);
            out.write(val.psGift);
            out.write(val.psStock);
        }
    };
}
