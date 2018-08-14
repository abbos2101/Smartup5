package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;

public class Deal {

    public static final String DEAL_EXTRAORDINARY = "E";
    public static final String DEAL_ORDER = "O";
    public static final String DEAL_RETURN = "R";
    public static final String DEAL_PHARMCY = "P";
    public static final String DEAL_DOCTOR = "D";
    public static final String DEAL_SUPERVISOR = "S";

    public static final String STATE_NEW = "Y";
    public static final String STATE_DRAFT = "N";

    public static final String DEAL_STATE_NONE = "";
    public static final String DEAL_STATE_DRAFT = "D";
    public static final String DEAL_STATE_NEW = "N";
    public static final String DEAL_STATE_EXECUTING = "E";
    public static final String DEAL_STATE_WAITING = "W";
    public static final String DEAL_STATE_SHIPPED = "S";
    public static final String DEAL_STATE_COMPLETED = "C";
    public static final String DEAL_STATE_ARCHIVED = "A";

    public final String filialId;
    public final String roomId;
    public final String outletId;
    public final String dealLocalId;
    public final String dealType;
    public final DealHeader header;
    public final MyArray<DealModule> modules;
    public final String dealNew;
    public final String finalDealId;
    public final String dealState;

    public Deal(String filialId,
                String roomId,
                String outletId,
                String dealLocalId,
                String dealType,
                DealHeader header,
                MyArray<DealModule> modules,
                String dealNew,
                String finalDealId,
                String dealState) {
        AppError.checkNull(header);

        this.filialId = filialId;
        this.roomId = roomId;
        this.outletId = outletId;
        this.dealLocalId = dealLocalId;
        this.dealType = dealType;
        this.header = header;
        this.modules = modules;
        this.dealNew = Util.nvl(dealNew, "Y");
        this.finalDealId = Util.nvl(finalDealId);
        this.dealState = Util.nvl(dealState);
    }

    public boolean is(String dealType) {
        return this.dealType.equals(dealType);
    }

    public String getEntryName() {
        switch (this.dealType) {
            case DEAL_EXTRAORDINARY:
                return RT.DEAL_EXTRAORDINARY;
            case DEAL_RETURN:
                return RT.DEAL_RETURN;
            case DEAL_SUPERVISOR:
            case DEAL_ORDER:
            case DEAL_DOCTOR:
            case DEAL_PHARMCY:
                return RT.DEAL_ORDER;
            default:
                throw AppError.Unsupported();
        }
    }

    public String getDealName() {
        switch (this.dealType) {
            case DEAL_SUPERVISOR:
                return DS.getString(R.string.sv_visit);
            case DEAL_EXTRAORDINARY:
                return DS.getString(R.string.outlet_extraordinary);
            case DEAL_RETURN:
                return DS.getString(R.string.outlet_return);
            case DEAL_ORDER:
            case DEAL_DOCTOR:
            case DEAL_PHARMCY:
                return DS.getString(R.string.outlet_visit);
            default:
                throw AppError.Unsupported();
        }
    }

    public static final Deal EMPTY = new Deal("", "", "", "", "",
            DealHeader.makeEmpty(RoundModel.make("+2.0R"), ""), MyArray.<DealModule>emptyArray(), "", "", DEAL_STATE_NONE);

    public static final MyMapper<Deal, String> KEY_ADAPTER = new MyMapper<Deal, String>() {
        @Override
        public String apply(Deal val) {
            return val.dealLocalId;
        }
    };

    public static final UzumAdapter<Deal> UZUM_ADAPTER = new UzumAdapter<Deal>() {

        @Override
        public Deal read(UzumReader in) {
            return new Deal(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readValue(DealHeader.UZUM_ADAPTER),
                    in.readArray(DealModule.UZUM_ADAPTER),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, Deal val) {
            out.write(val.filialId);
            out.write(val.roomId);
            out.write(val.outletId);
            out.write(val.dealLocalId);
            out.write(val.dealType);
            out.write(val.header, DealHeader.UZUM_ADAPTER);
            out.write(val.modules, DealModule.UZUM_ADAPTER);
            out.write(val.dealNew);
            out.write(val.finalDealId);
            out.write(val.dealState);
        }
    };
}
