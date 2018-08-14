package uz.greenwhite.smartup5_trade.m_session.bean;// 10.08.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PhotoType {

    public final String typeId;
    public final String name;
    public final int orderNo;

    public PhotoType(String typeId, String name, Integer orderNo) {
        this.typeId = typeId;
        this.name = name;
        this.orderNo = Util.nvl(orderNo, 999999);
    }

    public static final MyMapper<PhotoType, String> KEY_ADAPTER = new MyMapper<PhotoType, String>() {
        @Override
        public String apply(PhotoType photoType) {
            return photoType.typeId;
        }
    };

    public static final UzumAdapter<PhotoType> UZUM_ADAPTER = new UzumAdapter<PhotoType>() {
        @Override
        public PhotoType read(UzumReader in) {
            return new PhotoType(in.readString(), in.readString(), in.readInteger());
        }

        @Override
        public void write(UzumWriter out, PhotoType val) {
            out.write(val.typeId);
            out.write(val.name);
            out.write(val.orderNo);
        }
    };

}
