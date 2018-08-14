package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;


import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.filter.Range;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonFilterValue {

    public final GroupFilterValue groupFilter;
    public final boolean hasDeal;
    public final String regionId;
    public final Range lastVisitDate;
    public final String specialityId;

    public PersonFilterValue(GroupFilterValue groupFilter,
                             Boolean hasDeal,
                             String regionId,
                             Range lastVisitDate,
                             String specialityId) {
        this.groupFilter = Util.nvl(groupFilter, GroupFilterValue.DEFAULT);
        this.hasDeal = Util.nvl(hasDeal, false);
        this.regionId = Util.nvl(regionId);
        this.lastVisitDate = Util.nvl(lastVisitDate, Range.EMPTY);
        this.specialityId = Util.nvl(specialityId);
    }

    public static final PersonFilterValue DEFAULT = new PersonFilterValue(null, null, null, null, null);

    public static final UzumAdapter<PersonFilterValue> UZUM_ADAPTER = new UzumAdapter<PersonFilterValue>() {
        @Override
        public PersonFilterValue read(UzumReader in) {
            return new PersonFilterValue(in.readValue(GroupFilterValue.UZUM_ADAPTER),
                    in.readBoolean(), in.readString(),
                    in.readValue(Range.UZUM_ADAPTER),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonFilterValue val) {
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
            out.write(val.hasDeal);
            out.write(val.regionId);
            out.write(val.lastVisitDate, Range.UZUM_ADAPTER);
            out.write(val.specialityId);
        }
    };
}
