package uz.greenwhite.smartup5_trade.m_deal.bean.recom;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealRecom {

    public final String productId;
    public final BigDecimal recomQuant;

    public DealRecom(String productId, BigDecimal recomQuant) {
        this.productId = productId;
        this.recomQuant = recomQuant;
    }

    public static final MyMapper<DealRecom, String> KEY_ADAPTER = new MyMapper<DealRecom, String>() {
        @Override
        public String apply(DealRecom dealRecom) {
            return dealRecom.productId;
        }
    };

    public static final UzumAdapter<DealRecom> UZUM_ADAPTER = new UzumAdapter<DealRecom>() {
        @Override
        public DealRecom read(UzumReader in) {
            return new DealRecom(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, DealRecom val) {
            out.write(val.productId);
            out.write(val.recomQuant);
        }
    };
}
