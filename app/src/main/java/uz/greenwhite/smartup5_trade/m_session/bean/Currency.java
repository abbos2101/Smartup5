package uz.greenwhite.smartup5_trade.m_session.bean;// 14.07.2016

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class Currency {

    public final String currencyId;
    private final String name;
    public final BigDecimal price;

    public Currency(String currencyId, String name, BigDecimal price) {
        this.currencyId = currencyId;
        this.name = name;
        this.price = Util.nvl(price, BigDecimal.ONE);
    }

    public String getName() {
        if ("0".equals(currencyId) && (TextUtils.isEmpty(name) || "\u00A0".equals(name))) {
            return DS.getString(R.string.basic);
        }
        return name;
    }

    public String getNameBaseEmpty() {
        if ("0".equals(currencyId)) {
            return "";
        }
        return name;
    }

    public static final Currency EMPTY = new Currency("", "", null);
    public static final Currency DEFAULT = new Currency("0", DS.getString(R.string.basic), null);

    public static final MyMapper<Currency, String> KEY_ADAPTER = new MyMapper<Currency, String>() {
        @Override
        public String apply(Currency currency) {
            return currency.currencyId;
        }
    };

    public static final UzumAdapter<Currency> UZUM_ADAPTER = new UzumAdapter<Currency>() {
        @Override
        public Currency read(UzumReader in) {
            return new Currency(in.readString(),
                    in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, Currency val) {
            out.write(val.currencyId);
            out.write(val.name);
            out.write(val.price);
        }
    };

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency c = (Currency) o;

        return currencyId.equals(c.currencyId);

    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.currencyId);
    }
}
