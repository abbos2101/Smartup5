package uz.greenwhite.smartup5_trade.m_session.bean.action;// 28.12.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ActionParam {

    public final MyArray<String> productIds;

    public ActionParam(MyArray<String> productIds) {
        this.productIds = MyArray.nvl(productIds);
    }

    public static final ActionParam DEFAULT = new ActionParam(null);

    public static final UzumAdapter<ActionParam> UZUM_ADAPTER = new UzumAdapter<ActionParam>() {
        @Override
        public ActionParam read(UzumReader in) {
            return new ActionParam(in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, ActionParam val) {
            out.write(val.productIds, STRING_ARRAY);
        }
    };
}
