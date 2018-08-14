package uz.greenwhite.smartup5_trade.m_debtor.bean;


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class DebtorHolder {

    public final String entryId;
    public final Debtor debtor;
    public final EntryState entryState;

    public DebtorHolder(String entryId, Debtor debtor, EntryState entryState) {
        this.entryId = entryId;
        this.debtor = debtor;
        this.entryState = entryState;
    }

    public static final MyMapper<DebtorHolder, String> KEY_ADAPTER = new MyMapper<DebtorHolder, String>() {
        @Override
        public String apply(DebtorHolder val) {
            return val.debtor.localId;
        }
    };

    public static final UzumAdapter<DebtorHolder> UZUM_ADAPTER = new UzumAdapter<DebtorHolder>() {
        @Override
        public DebtorHolder read(UzumReader in) {
            return new DebtorHolder(in.readString(),
                    in.readValue(Debtor.UZUM_ADAPTER), in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DebtorHolder val) {
            out.write(val.entryId);
            out.write(val.debtor, Debtor.UZUM_ADAPTER);
            out.write(val.entryState, EntryState.UZUM_ADAPTER);
        }
    };
}
