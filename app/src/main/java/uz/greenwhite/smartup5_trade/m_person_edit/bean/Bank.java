package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class Bank {

    public final String bankId;
    public final String mfo;
    public final String name;

    public Bank(String bankId,
                String name,
                String mfo) {
        this.bankId = bankId;
        this.name = name;
        this.mfo = mfo;
    }

    public static final Bank DEFAULT = new Bank("", "", "");

    @Override
    public String toString() {
        return DS.getString(R.string.person_bank_mfo_info, mfo, name);
    }

    public static final MyMapper<Bank, String> KEY_ADAPTER = new MyMapper<Bank, String>() {
        @Override
        public String apply(Bank bank) {
            return bank.bankId;
        }
    };

    public static final UzumAdapter<Bank> UZUM_ADAPTER = new UzumAdapter<Bank>() {
        @Override
        public Bank read(UzumReader in) {
            return new Bank(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Bank val) {
            out.write(val.bankId);
            out.write(val.name);
            out.write(val.mfo);
        }
    };
}

