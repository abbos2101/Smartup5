package uz.greenwhite.smartup5_trade.m_order_info.bean;


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderCurPhoto {

    public final String sha;
    public final String photoTypeName;
    public final String date;
    public final String note;

    public OrderCurPhoto(String sha, String photoTypeName, String date, String note) {
        this.sha = sha;
        this.photoTypeName = photoTypeName;
        this.date = date;
        this.note = note;
    }

    public static final MyMapper<OrderCurPhoto, String> KEY_ADAPTER = new MyMapper<OrderCurPhoto, String>() {
        @Override
        public String apply(OrderCurPhoto val) {
            return val.sha;
        }
    };

    public static final UzumAdapter<OrderCurPhoto> UZUM_ADAPTER = new UzumAdapter<OrderCurPhoto>() {
        @Override
        public OrderCurPhoto read(UzumReader in) {
            return new OrderCurPhoto(
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderCurPhoto val) {
            out.write(val.sha);
            out.write(val.photoTypeName);
            out.write(val.date);
            out.write(val.note);
        }
    };
}
