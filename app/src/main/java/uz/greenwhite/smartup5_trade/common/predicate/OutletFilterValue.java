package uz.greenwhite.smartup5_trade.common.predicate;// 05.09.2016

import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.filter.Range;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletFilterValue {

    public final GroupFilterValue groupFilter;
    public final boolean hasDeal;
    public final String regionId;
    public final Range lastVisitDate;
    public final String specialityId;
    public final String legalPersonId;

    public OutletFilterValue(GroupFilterValue groupFilter,
                             Boolean hasDeal,
                             String regionId,
                             Range lastVisitDate,
                             String specialityId,
                             String legalPersonId) {
        this.groupFilter = Util.nvl(groupFilter, GroupFilterValue.DEFAULT);
        this.hasDeal = Util.nvl(hasDeal, false);
        this.regionId = Util.nvl(regionId);
        this.lastVisitDate = Util.nvl(lastVisitDate, Range.EMPTY);
        this.specialityId = Util.nvl(specialityId);
        this.legalPersonId = Util.nvl(legalPersonId);
    }

    public static final OutletFilterValue DEFAULT = new OutletFilterValue(null, null, null, null, null, null);

    public static final UzumAdapter<OutletFilterValue> UZUM_ADAPTER = new UzumAdapter<OutletFilterValue>() {
        @Override
        public OutletFilterValue read(UzumReader in) {
            return new OutletFilterValue(in.readValue(GroupFilterValue.UZUM_ADAPTER),
                    in.readBoolean(), in.readString(),
                    in.readValue(Range.UZUM_ADAPTER),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletFilterValue val) {
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
            out.write(val.hasDeal);
            out.write(val.regionId);
            out.write(val.lastVisitDate, Range.UZUM_ADAPTER);
            out.write(val.specialityId);
            out.write(val.legalPersonId);
        }
    };
}
