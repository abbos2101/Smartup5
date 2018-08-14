package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import java.util.Date;

import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class TodayFilterValue {

    public final boolean visitDateEnable;
    public final String visitDate;

    public final GroupFilterValue groupFilter;
    public final boolean hasDeal;
    public final String regionId;
    public final String personKind;
    public final String specialityId;

    public TodayFilterValue(boolean visitDateEnable,
                            String visitDate,
                            GroupFilterValue groupFilter,
                            boolean hasDeal,
                            String regionId,
                            String personKind,
                            String specialityId) {
        this.visitDateEnable = visitDateEnable;
        this.visitDate = Util.nvl(visitDate);
        this.groupFilter = Util.nvl(groupFilter, GroupFilterValue.DEFAULT);
        this.hasDeal = hasDeal;
        this.regionId = Util.nvl(regionId);
        this.personKind = Util.nvl(personKind);
        this.specialityId = Util.nvl(specialityId);
    }

    public static TodayFilterValue makeDefault() {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new TodayFilterValue(true, today, GroupFilterValue.DEFAULT, false, "", "-1", "");
    }

    public static final UzumAdapter<TodayFilterValue> UZUM_ADAPTER = new UzumAdapter<TodayFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public TodayFilterValue read(UzumReader in) {
            return new TodayFilterValue(
                    in.readBoolean(), in.readString(),
                    in.readValue(GroupFilterValue.UZUM_ADAPTER),
                    in.readBoolean(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, TodayFilterValue val) {
            out.write(val.visitDateEnable);
            out.write(val.visitDate);
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
            out.write(val.hasDeal);
            out.write(val.regionId);
            out.write(val.personKind);
            out.write(val.specialityId);
        }
    };
}
