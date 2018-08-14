package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import java.util.Date;

import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ShippedFilterValue {
    public final boolean deliveryDateEnable;
    public final String deliveryDate;

    public final GroupFilterValue groupFilter;

    public final String roomId;
    public final String regionId;

    public ShippedFilterValue(boolean deliveryDateEnable,
                              String deliveryDate,
                              GroupFilterValue groupFilter,
                              String roomId,
                              String regionId) {
        this.deliveryDateEnable = deliveryDateEnable;
        this.deliveryDate = deliveryDate;
        this.groupFilter = groupFilter;
        this.roomId = roomId;
        this.regionId = regionId;
    }

    public static ShippedFilterValue makeDefault() {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new ShippedFilterValue(true, today, GroupFilterValue.DEFAULT, "", "");
    }

    public static final UzumAdapter<ShippedFilterValue> UZUM_ADAPTER = new UzumAdapter<ShippedFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public ShippedFilterValue read(UzumReader in) {
            return new ShippedFilterValue(
                    in.readBoolean(), in.readString(),
                    in.readValue(GroupFilterValue.UZUM_ADAPTER),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ShippedFilterValue val) {
            out.write(val.deliveryDateEnable);
            out.write(val.deliveryDate);
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
            out.write(val.roomId);
            out.write(val.regionId);
        }
    };
}
