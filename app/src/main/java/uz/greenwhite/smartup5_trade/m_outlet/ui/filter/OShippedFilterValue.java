package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OShippedFilterValue {

    public final boolean deliveryDateEnable;
    public final String deliveryDate;


    public OShippedFilterValue(boolean deliveryDateEnable,
                               String deliveryDate) {
        this.deliveryDateEnable = deliveryDateEnable;
        this.deliveryDate = deliveryDate;
    }

    public static OShippedFilterValue makeDefault() {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new OShippedFilterValue(true, today);
    }

    public static final UzumAdapter<OShippedFilterValue> UZUM_ADAPTER = new UzumAdapter<OShippedFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public OShippedFilterValue read(UzumReader in) {
            return new OShippedFilterValue(in.readBoolean(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OShippedFilterValue val) {
            out.write(val.deliveryDateEnable);
            out.write(val.deliveryDate);
        }
    };
}
