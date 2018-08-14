package uz.greenwhite.smartup5_trade.m_shipped.bean;// 08.09.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;

public class SDealHolder {

    public final String entryId;
    public final SDeal deal;
    public final EntryState entryState;

    public SDealHolder(String entryId, SDeal deal, EntryState entryState) {
        this.entryId = entryId;
        this.deal = deal;
        this.entryState = entryState;
    }

    public static final MyMapper<SDealHolder, String> KEY_ADAPTER = new MyMapper<SDealHolder, String>() {
        @Override
        public String apply(SDealHolder sDealHolder) {
            return sDealHolder.deal.dealId;
        }
    };

    public static final UzumAdapter<SDealHolder> UZUM_ADAPTER = new UzumAdapter<SDealHolder>() {
        @Override
        public SDealHolder read(UzumReader in) {
            return new SDealHolder(in.readString(),
                    in.readValue(SDeal.UZUM_ADAPTER), in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, SDealHolder val) {
            out.write(val.entryId);
            out.write(val.deal, SDeal.UZUM_ADAPTER);
            out.write(val.entryState, EntryState.UZUM_ADAPTER);
        }
    };

}
