package uz.greenwhite.smartup5_trade.m_session.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Producer {

    public final String producerId;
    public final String name;
    public final String regionId;
    public final String orderNo;

    public Producer(String producerId, String name, String regionId, String orderNo) {
        this.producerId = producerId;
        this.name = name;
        this.regionId = regionId;
        this.orderNo = TextUtils.isEmpty(orderNo) ? "9999" : orderNo;

    }

    public static final MyMapper<Producer, String> KEY_ADAPTER = new MyMapper<Producer, String>() {
        @Override
        public String apply(Producer producer) {
            return producer.producerId;
        }
    };

    public static final UzumAdapter<Producer> UZUM_ADAPTER = new UzumAdapter<Producer>() {
        @Override
        public Producer read(UzumReader in) {
            return new Producer(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Producer val) {
            out.write(val.producerId);
            out.write(val.name);
            out.write(val.regionId);
            out.write(val.orderNo);
        }
    };
}

