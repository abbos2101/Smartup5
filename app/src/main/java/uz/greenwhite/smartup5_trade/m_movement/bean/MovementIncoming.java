package uz.greenwhite.smartup5_trade.m_movement.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class MovementIncoming {

    public static final String K_DRAFT = "D";
    public static final String K_READY = "R";
    public static final String K_POSTED = "P";
    public static final String K_DELETED = "L";

    public final String movementId;
    public final String fromFilial;
    public final String fromWarehouse;
    public final String currencyName;
    public final String date;
    public final String warehouseId;
    public final String state;
    public final MyArray<MovementProduct> products;

    public MovementIncoming(String movementId,
                            String fromFilial,
                            String fromWarehouse,
                            String currencyName,
                            String date,
                            String warehouseId,
                            String state,
                            MyArray<MovementProduct> products) {
        this.movementId = movementId;
        this.fromFilial = fromFilial;
        this.fromWarehouse = fromWarehouse;
        this.currencyName = currencyName;
        this.date = date;
        this.warehouseId = warehouseId;
        this.state = state;
        this.products = products;
    }

    public static final MyMapper<MovementIncoming, String> KEY_ADAPTER = new MyMapper<MovementIncoming, String>() {
        @Override
        public String apply(MovementIncoming val) {
            return val.movementId;
        }
    };

    public CharSequence getStateName() {
        switch (state) {
            case K_DRAFT:
                return DS.getString(R.string.warehouse_movement_draft);
            case K_READY:
                return DS.getString(R.string.warehouse_movement_ready);
            case K_POSTED:
                return DS.getString(R.string.warehouse_movement_posted);
            case K_DELETED:
                return DS.getString(R.string.warehouse_movement_deleted);
            default:
                return DS.getString(R.string.unknown);
        }
    }

    public static final UzumAdapter<MovementIncoming> UZUM_ADAPTER = new UzumAdapter<MovementIncoming>() {
        @Override
        public MovementIncoming read(UzumReader in) {
            return new MovementIncoming(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readArray(MovementProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, MovementIncoming val) {
            out.write(val.movementId);
            out.write(val.fromFilial);
            out.write(val.fromWarehouse);
            out.write(val.currencyName);
            out.write(val.date);
            out.write(val.warehouseId);
            out.write(val.state);
            out.write(val.products, MovementProduct.UZUM_ADAPTER);
        }
    };
}
