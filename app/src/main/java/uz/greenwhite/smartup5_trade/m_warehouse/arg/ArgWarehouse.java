package uz.greenwhite.smartup5_trade.m_warehouse.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class ArgWarehouse extends ArgSession {

    public final String warehouseId;

    public ArgWarehouse(ArgSession arg, String warehouseId) {
        super(arg.accountId, arg.filialId);
        this.warehouseId = warehouseId;
    }

    protected ArgWarehouse(UzumReader in) {
        super(in);
        this.warehouseId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.warehouseId);
    }

    public Warehouse getWarehouse() {
        return getScope().ref.getWarehouse(warehouseId);
    }

    public static final UzumAdapter<ArgWarehouse> UZUM_ADAPTER = new UzumAdapter<ArgWarehouse>() {
        @Override
        public ArgWarehouse read(UzumReader in) {
            return new ArgWarehouse(in);
        }

        @Override
        public void write(UzumWriter out, ArgWarehouse val) {
            val.write(out);
        }
    };
}
