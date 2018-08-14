package uz.greenwhite.smartup5_trade.m_deal.arg;// 30.06.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHeader;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;

public class ArgDeal extends ArgOutlet {

    public final String roomId;
    public final String dealId;
    public final String location;
    public final String accuracy;
    public final String type;

    private final Deal historyDeal;

    public ArgDeal(ArgOutlet arg,
                   Deal historyDeal) {
        super(arg, arg.outletId);
        this.roomId = historyDeal.roomId;
        this.dealId = "";
        this.location = "";
        this.accuracy = "";
        this.type = Deal.DEAL_EXTRAORDINARY;
        this.historyDeal = historyDeal;
    }

    public ArgDeal(ArgOutlet arg,
                   String roomId,
                   String dealId,
                   String location,
                   String accuracy,
                   String type) {
        super(arg, arg.outletId);
        this.roomId = roomId;
        this.dealId = dealId;
        this.location = location;
        this.accuracy = accuracy;
        this.type = type;
        this.historyDeal = Deal.EMPTY;
    }

    public ArgDeal(UzumReader in) {
        super(in);
        this.roomId = in.readString();
        this.dealId = in.readString();
        this.location = in.readString();
        this.accuracy = in.readString();
        this.type = in.readString();
        this.historyDeal = in.readValue(Deal.UZUM_ADAPTER);
    }

    public boolean isNew() {
        return TextUtils.isEmpty(this.dealId);
    }

    public DealHolder getDealHolder(Scope scope) {
        if (historyDeal != null && !TextUtils.isEmpty(historyDeal.finalDealId)) {
            return new DealHolder(historyDeal, EntryState.NOT_SAVED_ENTRY);
        }
        if (isNew()) {
            Filial filial = scope.ref.getFilial(filialId);
            String id = String.valueOf(AdminApi.nextSequence());
            Deal d = new Deal(filialId, roomId, outletId, id, type,
                    DealHeader.makeEmpty(filial.roundModel, location), MyArray.<DealModule>emptyArray(), Deal.STATE_NEW, null, Deal.DEAL_STATE_NONE);
            return new DealHolder(d, EntryState.NOT_SAVED_ENTRY);
        } else {
            return scope.entry.getDeal(dealId);
        }
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.roomId);
        w.write(this.dealId);
        w.write(this.location);
        w.write(this.accuracy);
        w.write(this.type);
        w.write(this.historyDeal, Deal.UZUM_ADAPTER);
    }

    public static final UzumAdapter<ArgDeal> UZUM_ADAPTER = new UzumAdapter<ArgDeal>() {
        @Override
        public ArgDeal read(UzumReader in) {
            return new ArgDeal(in);
        }

        @Override
        public void write(UzumWriter out, ArgDeal val) {
            val.write(out);
        }
    };
}
