package uz.greenwhite.smartup5_trade.m_session.bean.violation;

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;

public class Violation {

    public static final String K_CONSIGNMENT_DATE = "C";
    public static final String K_PAYMENT_TYPE = "P";
    public static final String K_NONE = "N";

    public final String violationId;
    public final String name;
    public final MyArray<String> roomIds;
    public final String groupId;
    public final MyArray<String> groupTypeIds;
    public final String kind;
    public final BigDecimal value;
    public final MyArray<String> sourceIds;
    public final MyArray<Ban> bans;

    public Violation(String violationId,
                     String name,
                     MyArray<String> roomIds,
                     String groupId,
                     MyArray<String> groupTypeIds,
                     String kind,
                     BigDecimal value,
                     MyArray<String> sourceIds,
                     MyArray<Ban> bans) {
        this.violationId = violationId;
        this.name = name;
        this.roomIds = roomIds;
        this.groupId = groupId;
        this.groupTypeIds = groupTypeIds;
        this.kind = kind;
        this.value = Util.nvl(value, BigDecimal.ZERO);
        this.sourceIds = MyArray.nvl(sourceIds);
        this.bans = MyArray.nvl(bans);
    }

    public CharSequence getBanDetails(final DealRef dealRef) {

        ShortHtml html = UI.html();
        boolean firstRun = false;
        for (Ban val : bans) {
            if (firstRun) {
                html.br();
            }
            String name = val.getName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }

            html.b().v(name).b();

            if (val.kindValue != null &&
                    val.kindValue.compareTo(BigDecimal.ZERO) == 0) {
                html.v(" ").v(DS.getString(R.string.deal_violation_values, NumberUtil.formatMoney(val.kindValue)));
            }

            if (Ban.K_PRICE_TYPE.equals(val.kind) && val.kindSourceIds.nonEmpty()) {
                String values = val.kindSourceIds.map(new MyMapper<String, String>() {
                    @Override
                    public String apply(String sourceId) {
                        PriceType priceType = dealRef.getPriceType(sourceId);
                        if (priceType != null) {
                            return priceType.name;
                        }
                        return null;
                    }
                }).filterNotNull().mkString(", ");
                html.v(" ").i().v(DS.getString(R.string.deal_violation_bans, values)).i();

            } else if (Ban.K_PAYMENT_TYPE.equals(val.kind) && val.kindSourceIds.nonEmpty()) {
                String values = val.kindSourceIds.map(new MyMapper<String, String>() {
                    @Override
                    public String apply(String sourceId) {
                        PaymentType paymentType = dealRef.getPaymentType(sourceId);
                        if (paymentType != null) {
                            return paymentType.name;
                        }
                        return null;
                    }
                }).filterNotNull().mkString(", ");
                html.v(" ").i().v(DS.getString(R.string.deal_violation_bans, values)).i();
            }


            firstRun = true;
        }
        return html.html();
    }

    public static final MyMapper<Violation, String> KEY_ADAPTER = new MyMapper<Violation, String>() {
        @Override
        public String apply(Violation violation) {
            return violation.violationId;
        }
    };

    public static final UzumAdapter<Violation> UZUM_ADAPTER = new UzumAdapter<Violation>() {
        @Override
        public Violation read(UzumReader in) {
            return new Violation(in.readString(),
                    in.readString(), in.readValue(STRING_ARRAY),
                    in.readString(), in.readValue(STRING_ARRAY),
                    in.readString(),
                    in.readBigDecimal(), in.readValue(STRING_ARRAY),
                    in.readArray(Ban.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Violation val) {
            out.write(val.violationId);
            out.write(val.name);
            out.write(val.roomIds, STRING_ARRAY);
            out.write(val.groupId);
            out.write(val.groupTypeIds, STRING_ARRAY);
            out.write(val.kind);
            out.write(val.value);
            out.write(val.sourceIds, STRING_ARRAY);
            out.write(val.bans, Ban.UZUM_ADAPTER);
        }
    };
}
