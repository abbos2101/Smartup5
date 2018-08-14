package uz.greenwhite.smartup5_trade.m_outlet.categorization;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResult;

public class CatHolder {

    public final String entryId;
    public final CatResult outletCatQuiz;
    public final EntryState entryState;

    public CatHolder(String entryId,  CatResult outletCatQuiz, EntryState entryState) {
        this.entryId = entryId;
        this.outletCatQuiz = outletCatQuiz;
        this.entryState = entryState;
    }

    public static final UzumAdapter<CatHolder> UZUM_ADAPTER = new UzumAdapter<CatHolder>() {
        @Override
        public CatHolder read(UzumReader in) {
            return new CatHolder(in.readString(), in.readValue(CatResult.UZUM_ADAPTER), in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, CatHolder val) {
            out.write(val.entryId);
            out.write(val.outletCatQuiz, CatResult.UZUM_ADAPTER);
            out.write(val.entryState, EntryState.UZUM_ADAPTER);
        }
    };
}
