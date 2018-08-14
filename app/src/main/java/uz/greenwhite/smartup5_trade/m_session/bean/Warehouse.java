package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Warehouse {

    public final String id;
    public final String name;
    public final WarehouseResponsible responsible;

    public Warehouse(String id, String name, WarehouseResponsible responsible) {
        this.id = id;
        this.name = name;
        this.responsible = responsible;
    }

    public static final MyMapper<Warehouse, String> KEY_ADAPTER = new MyMapper<Warehouse, String>() {
        @Override
        public String apply(Warehouse warehouse) {
            return warehouse.id;
        }
    };

    public static final UzumAdapter<Warehouse> UZUM_ADAPTER = new UzumAdapter<Warehouse>() {
        @Override
        public Warehouse read(UzumReader in) {
            return new Warehouse(in.readString(), in.readString(),
                    in.readValue(WarehouseResponsible.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Warehouse val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.responsible, WarehouseResponsible.UZUM_ADAPTER);
        }
    };
}
