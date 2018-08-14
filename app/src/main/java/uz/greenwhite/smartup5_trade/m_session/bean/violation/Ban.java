package uz.greenwhite.smartup5_trade.m_session.bean.violation;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class Ban {

    public static final String K_DEAL = "DEAL";
    public static final String K_CONSIGNMENT = "CONSIGNMENT";
    public static final String K_PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final String K_PRICE_TYPE = "PRICE_TYPE";
    public static final String K_PREPAYMENT = "PREPAYMENT";
    public static final String K_DISCOUNT = "DISCOUNT";
    public static final String K_GIFT = "GIFT";
    public static final String K_ACTION = "ACTION";
    public static final String K_PRODUCT = "PRODUCT";

    public final String banId;
    public final String kind;
    public final BigDecimal kindValue;
    public final MyArray<String> kindSourceIds;

    public Ban(String banId, String kind, BigDecimal kindValue, MyArray<String> kindSourceIds) {
        this.banId = banId;
        this.kind = kind;
        this.kindValue = kindValue;
        this.kindSourceIds = kindSourceIds;
    }

    public String getName() {
        switch (kind) {
            case K_DEAL:
                return DS.getString(R.string.order);
            case K_CONSIGNMENT:
                return DS.getString(R.string.consignment);
            case K_PAYMENT_TYPE:
                return DS.getString(R.string.deal_payment_type);
            case K_PRICE_TYPE:
                return DS.getString(R.string.deal_form_price_type);
            case K_PREPAYMENT:
                return DS.getString(R.string.prepayment);
            case K_DISCOUNT:
                return DS.getString(R.string.deal_discount);
            case K_GIFT:
                return DS.getString(R.string.gift);
            case K_ACTION:
                return DS.getString(R.string.action);
            case K_PRODUCT:
                return DS.getString(R.string.order_products);
            default:
                return "";
        }
    }

    public static final UzumAdapter<Ban> UZUM_ADAPTER = new UzumAdapter<Ban>() {
        @Override
        public Ban read(UzumReader in) {
            return new Ban(in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, Ban val) {
            out.write(val.banId);
            out.write(val.kind);
            out.write(val.kindValue);
            out.write(val.kindSourceIds, STRING_ARRAY);
        }
    };
}
