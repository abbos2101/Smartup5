package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DebtorFilterValue {

    public final boolean debtorDateEnable;
    public final String debtorDate;


    public DebtorFilterValue(boolean debtorDateEnable,
                             String debtorDate) {
        this.debtorDateEnable = debtorDateEnable;
        this.debtorDate = debtorDate;
    }

    public static DebtorFilterValue makeDefault() {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new DebtorFilterValue(true, today);
    }

    public static final UzumAdapter<DebtorFilterValue> UZUM_ADAPTER = new UzumAdapter<DebtorFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public DebtorFilterValue read(UzumReader in) {
            return new DebtorFilterValue(in.readBoolean(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DebtorFilterValue val) {
            out.write(val.debtorDateEnable);
            out.write(val.debtorDate);
        }
    };
}
