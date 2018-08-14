package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import java.util.Date;

import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DebtorFilterValue {
    public final boolean deliveryDateEnable;
    public final String deliveryDate;

    public final GroupFilterValue groupFilter;

    public DebtorFilterValue(boolean deliveryDateEnable,
                             String deliveryDate,
                             GroupFilterValue groupFilter) {
        this.deliveryDateEnable = deliveryDateEnable;
        this.deliveryDate = deliveryDate;
        this.groupFilter = groupFilter;
    }

    public static DebtorFilterValue makeDefault() {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new DebtorFilterValue(true, today, GroupFilterValue.DEFAULT);
    }

    public static final UzumAdapter<DebtorFilterValue> UZUM_ADAPTER = new UzumAdapter<DebtorFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public DebtorFilterValue read(UzumReader in) {
            return new DebtorFilterValue(
                    in.readBoolean(), in.readString(),
                    in.readValue(GroupFilterValue.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DebtorFilterValue val) {
            out.write(val.deliveryDateEnable);
            out.write(val.deliveryDate);
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
        }
    };
}
