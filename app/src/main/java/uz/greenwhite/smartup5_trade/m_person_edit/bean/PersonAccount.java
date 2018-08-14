package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class PersonAccount {

    public final String bankAccountId;
    public final Bank bank;
    public final Currency currency;
    public final String personAccount;

    public PersonAccount(String bankAccountId, Bank bank, Currency currency, String personAccount) {
        this.bankAccountId = Util.nvl(bankAccountId);
        this.bank = Util.nvl(bank, Bank.DEFAULT);
        this.currency = Util.nvl(currency, Currency.DEFAULT);
        this.personAccount = personAccount;
    }

    public static final PersonAccount DEFAULT = new PersonAccount(null, Bank.DEFAULT, Currency.EMPTY, "");

    public static final UzumAdapter<PersonAccount> UZUM_ADAPTER = new UzumAdapter<PersonAccount>() {
        @Override
        public PersonAccount read(UzumReader in) {
            return new PersonAccount(in.readString(),
                    in.readValue(Bank.UZUM_ADAPTER),
                    in.readValue(Currency.UZUM_ADAPTER), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonAccount val) {
            out.write(val.bankAccountId);
            out.write(val.bank, Bank.UZUM_ADAPTER);
            out.write(val.currency, Currency.UZUM_ADAPTER);
            out.write(val.personAccount);
        }
    };
}
