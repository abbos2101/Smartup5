package uz.greenwhite.smartup5_trade.m_outlet.bean;// 27.09.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SAttach {

    public final boolean contract;
    public final boolean invoice;
    public final boolean powerOfAttorney;

    public SAttach(Boolean contract, Boolean invoice, Boolean powerOfAttorney) {
        this.contract = Util.nvl(contract, false);
        this.invoice = Util.nvl(invoice, false);
        this.powerOfAttorney = Util.nvl(powerOfAttorney, false);
    }

    public static final SAttach DEFAULT = new SAttach(false, false, false);

    public static final UzumAdapter<SAttach> UZUM_ADAPTER = new UzumAdapter<SAttach>() {
        @Override
        public SAttach read(UzumReader in) {
            return new SAttach(in.readBoolean(), in.readBoolean(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SAttach val) {
            out.write(val.contract);
            out.write(val.invoice);
            out.write(val.powerOfAttorney);
        }
    };
}
