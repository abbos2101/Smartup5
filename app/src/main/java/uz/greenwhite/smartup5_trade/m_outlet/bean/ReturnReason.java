package uz.greenwhite.smartup5_trade.m_outlet.bean;// 18.10.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class ReturnReason {

    public final String id;
    public final String name;

    public ReturnReason(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final ReturnReason NOT_SELECT = new ReturnReason("-1", DS.getString(R.string.not_selected));

    public static final MyMapper<ReturnReason, String> KEY_ADAPTER = new MyMapper<ReturnReason, String>() {
        @Override
        public String apply(ReturnReason returnReason) {
            return returnReason.id;
        }
    };

    public static final UzumAdapter<ReturnReason> UZUM_ADAPTER = new UzumAdapter<ReturnReason>() {
        @Override
        public ReturnReason read(UzumReader in) {
            return new ReturnReason(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ReturnReason val) {
            out.write(val.id);
            out.write(val.name);
        }
    };
}
