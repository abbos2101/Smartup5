package uz.greenwhite.smartup5_trade.m_session.bean;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class BankAccount {

    public final String bankAccId;
    public final String bankAccCode;

    public BankAccount(String bankAccId, String bankAccCode) {
        this.bankAccId = bankAccId;
        this.bankAccCode = bankAccCode;
    }

    public static final UzumAdapter<BankAccount> UZUM_ADAPTER = new UzumAdapter<BankAccount>() {
        @Override
        public BankAccount read(UzumReader in) {
            return new BankAccount(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, BankAccount val) {
            out.write(val.bankAccId);
            out.write(val.bankAccCode);
        }
    };
}
