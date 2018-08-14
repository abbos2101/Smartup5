package uz.greenwhite.smartup5_trade.m_session.bean.action;// 07.12.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletAction {

    public static final String QUANT = "Q";
    public static final String WEIGHT = "W";
    public static final String AMOUNT = "A";

    public final String actionId;
    public final String name;
    public final String actionType;
    public final String warehouseId;
    public final String startDate;
    public final String endDate;
    public final MyArray<ActionLevel> actionLevels;
    public final ActionParam param;

    public OutletAction(String actionId,
                        String name,
                        String actionType,
                        String warehouseId,
                        String startDate,
                        String endDate,
                        MyArray<ActionLevel> actionLevels,
                        ActionParam param) {
        this.actionId = actionId;
        this.name = name;
        this.actionType = actionType;
        this.warehouseId = warehouseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actionLevels = actionLevels;
        this.param = Util.nvl(param, ActionParam.DEFAULT);
    }

    public boolean isQuant() {
        return MyPredicate.equals(this.actionType, QUANT);
    }

    public boolean isWeight() {
        return MyPredicate.equals(this.actionType, WEIGHT);
    }

    public boolean isAmount() {
        return MyPredicate.equals(this.actionType, AMOUNT);
    }

    public static final MyMapper<OutletAction, String> KEY_ADAPTER = new MyMapper<OutletAction, String>() {
        @Override
        public String apply(OutletAction outletAction) {
            return outletAction.actionId;
        }
    };

    public static final UzumAdapter<OutletAction> UZUM_ADAPTER = new UzumAdapter<OutletAction>() {
        @Override
        public OutletAction read(UzumReader in) {
            return new OutletAction(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readArray(ActionLevel.UZUM_ADAPTER),
                    in.readValue(ActionParam.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OutletAction val) {
            out.write(val.actionId);
            out.write(val.name);
            out.write(val.actionType);
            out.write(val.warehouseId);
            out.write(val.startDate);
            out.write(val.endDate);
            out.write(val.actionLevels, ActionLevel.UZUM_ADAPTER);
            out.write(val.param, ActionParam.UZUM_ADAPTER);
        }
    };
}
