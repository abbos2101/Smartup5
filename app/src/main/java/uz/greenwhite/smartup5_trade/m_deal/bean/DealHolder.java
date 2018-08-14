package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class DealHolder {

    public final Deal deal;
    public final EntryState entryState;

    public DealHolder(Deal deal, EntryState entryState) {
        this.deal = deal;
        this.entryState = entryState;
    }

    public static final MyMapper<DealHolder, String> KEY_ADAPTER = new MyMapper<DealHolder, String>() {
        @Override
        public String apply(DealHolder val) {
            return val.deal.dealLocalId;
        }
    };

    public static final UzumAdapter<DealHolder> UZUM_ADAPTER = new UzumAdapter<DealHolder>() {
        @Override
        public DealHolder read(UzumReader in) {
            return new DealHolder(in.readValue(Deal.UZUM_ADAPTER), in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealHolder val) {
            out.write(val.deal, Deal.UZUM_ADAPTER);
            out.write(val.entryState, EntryState.UZUM_ADAPTER);
        }
    };

    public static final MyMapper<DealHolder, EntryState> TO_ENTRY_STATE = new MyMapper<DealHolder, EntryState>() {
        @Override
        public EntryState apply(DealHolder dealHolder) {
            return dealHolder.entryState;
        }
    };
}
