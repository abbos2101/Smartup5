package uz.greenwhite.smartup5_trade.m_session.bean;// 05.09.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletType {

    public final String typeId;
    public final String name;
    public final String groupId;
    public final String orderNo;

    public OutletType(String typeId,
                      String name,
                      String groupId,
                      String orderNo) {
        this.typeId = typeId;
        this.name = name;
        this.groupId = groupId;
        this.orderNo = orderNo;
    }

    public static final MyMapper<OutletType, String> KEY_ADAPTER = new MyMapper<OutletType, String>() {
        @Override
        public String apply(OutletType val) {
            return val.typeId;
        }
    };

    public static final UzumAdapter<OutletType> UZUM_ADAPTER = new UzumAdapter<OutletType>() {
        @Override
        public OutletType read(UzumReader in) {
            return new OutletType(in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletType val) {
            out.write(val.typeId);
            out.write(val.name);
            out.write(val.groupId);
            out.write(val.orderNo);
        }
    };
}
