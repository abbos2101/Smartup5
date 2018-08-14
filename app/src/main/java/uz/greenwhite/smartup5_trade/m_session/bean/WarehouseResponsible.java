package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class WarehouseResponsible {

    public final String personId;
    public final String name;

    public WarehouseResponsible(String personId, String name) {
        this.personId = personId;
        this.name = name;
    }

    public static final UzumAdapter<WarehouseResponsible> UZUM_ADAPTER = new UzumAdapter<WarehouseResponsible>() {
        @Override
        public WarehouseResponsible read(UzumReader in) {
            return new WarehouseResponsible(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, WarehouseResponsible val) {
            out.write(val.personId);
            out.write(val.name);
        }
    };
}
