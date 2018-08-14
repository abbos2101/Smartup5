package uz.greenwhite.smartup5_trade.m_stocktaking.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class StocktakingHolder {

    public final Stocktaking stocktaking;
    public final EntryState state;

    public StocktakingHolder(Stocktaking stocktaking, EntryState state) {
        this.stocktaking = stocktaking;
        this.state = state;
    }

    public static final UzumAdapter<StocktakingHolder> UZUM_ADAPTER = new UzumAdapter<StocktakingHolder>() {
        @Override
        public StocktakingHolder read(UzumReader in) {
            return new StocktakingHolder(in.readValue(Stocktaking.UZUM_ADAPTER),
                    in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, StocktakingHolder val) {
            out.write(val.stocktaking, Stocktaking.UZUM_ADAPTER);
            out.write(val.state, EntryState.UZUM_ADAPTER);
        }
    };
}
