package uz.greenwhite.smartup5_trade.m_deal.bean;

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealStock {

    public final String productId;
    public final String stock;
    public final String market;
    public final String expireDate;

    public DealStock(String productId, String stock, String market, String expireDate) {
        this.productId = productId;
        this.stock = stock;
        this.market = market;
        this.expireDate = expireDate;
    }

    private BigDecimal parsToBigDecimal(String number) {
        if (!TextUtils.isEmpty(number)) {
            return new BigDecimal(number);
        }
        return null;
    }

    public BigDecimal getStock() {
        return parsToBigDecimal(stock);
    }

    public static final UzumAdapter<DealStock> UZUM_ADAPTER = new UzumAdapter<DealStock>() {
        @Override
        public DealStock read(UzumReader in) {
            return new DealStock(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealStock val) {
            out.write(val.productId);
            out.write(val.stock);
            out.write(val.market);
            out.write(val.expireDate);
        }
    };
}
