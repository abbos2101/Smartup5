package uz.greenwhite.smartup5_trade.m_session.bean;// 12.08.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductType {

    public final String typeId;
    public final String name;
    public final String groupId;
    public final int orderNo;

    public ProductType(String typeId,
                       String name,
                       String groupId,
                       Integer orderNo) {
        this.typeId = typeId;
        this.name = name;
        this.groupId = groupId;
        this.orderNo = Util.nvl(orderNo, 999999);
    }

    public static final MyMapper<ProductType, String> KEY_ADAPTER = new MyMapper<ProductType, String>() {
        @Override
        public String apply(ProductType val) {
            return val.typeId;
        }
    };

    public static final UzumAdapter<ProductType> UZUM_ADAPTER = new UzumAdapter<ProductType>() {
        @Override
        public ProductType read(UzumReader in) {
            return new ProductType(in.readString(), in.readString(),
                    in.readString(), in.readInteger());
        }

        @Override
        public void write(UzumWriter out, ProductType val) {
            out.write(val.typeId);
            out.write(val.name);
            out.write(val.groupId);
            out.write(val.orderNo);
        }
    };
}
