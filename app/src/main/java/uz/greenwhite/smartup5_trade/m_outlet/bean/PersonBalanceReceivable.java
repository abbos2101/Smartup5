package uz.greenwhite.smartup5_trade.m_outlet.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonBalanceReceivable {

    public final String personId;
    public final BigDecimal amount;

    public PersonBalanceReceivable(String personId, BigDecimal amount) {
        this.personId = personId;
        this.amount = amount;
    }

    public static final MyMapper<PersonBalanceReceivable, String> KEY_ADAPTER = new MyMapper<PersonBalanceReceivable, String>() {
        @Override
        public String apply(PersonBalanceReceivable val) {
            return val.personId;
        }
    };

    public static final UzumAdapter<PersonBalanceReceivable> UZUM_ADAPTER = new UzumAdapter<PersonBalanceReceivable>() {
        @Override
        public PersonBalanceReceivable read(UzumReader in) {
            return new PersonBalanceReceivable(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, PersonBalanceReceivable val) {
            out.write(val.personId);
            out.write(val.amount);
        }
    };
}
