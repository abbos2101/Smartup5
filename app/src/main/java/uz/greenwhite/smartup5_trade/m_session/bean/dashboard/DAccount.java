package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;// 28.12.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DAccount {

    public static final String TYPE_EXTRAORDINARY = "E";
    public static final String TYPE_ORDER = "O";

    public final String priceTypeId;
    public final String priceTypeName;
    public final String productCount;
    public final String totalPrice;
    public final String visitType;

    public DAccount(String priceTypeId,
                    String priceTypeName,
                    String productCount,
                    String totalPrice,
                    String visitType) {
        this.priceTypeId = Util.nvl(priceTypeId);
        this.priceTypeName = Util.nvl(priceTypeName);
        this.productCount = Util.nvl(productCount);
        this.totalPrice = Util.nvl(totalPrice);
        this.visitType = Util.nvl(visitType);
    }

    public static final UzumAdapter<DAccount> UZUM_ADAPTER = new UzumAdapter<DAccount>() {
        @Override
        public DAccount read(UzumReader in) {
            return new DAccount(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DAccount val) {
            out.write(val.priceTypeId);
            out.write(val.priceTypeName);
            out.write(val.productCount);
            out.write(val.totalPrice);
            out.write(val.visitType);
        }
    };
}
