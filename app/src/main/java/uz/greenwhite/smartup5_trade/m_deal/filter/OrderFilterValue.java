package uz.greenwhite.smartup5_trade.m_deal.filter;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class OrderFilterValue {

    public final String formCode;
    public final ProductFilterValue product;
    public final String cardCode;
    public final boolean hasValue;
    public final boolean hasDiscount;
    public final boolean warehouseAvail;
    public final boolean sortFirstMll;

    public OrderFilterValue(String formCode,
                            ProductFilterValue product,
                            String cardCode,
                            boolean hasValue,
                            boolean hasDiscount,
                            boolean warehouseAvail,
                            boolean sortFirstMll) {
        this.formCode = formCode;
        this.product = product;
        this.cardCode = cardCode;
        this.hasValue = hasValue;
        this.hasDiscount = hasDiscount;
        this.warehouseAvail = warehouseAvail;
        this.sortFirstMll = sortFirstMll;
    }

    public static OrderFilterValue makeDefault(String formCode, boolean warehouseAvail) {
        ProductFilterValue p = new ProductFilterValue(null, false, false, false, false);
        return new OrderFilterValue(formCode, p, "", false, false, warehouseAvail, true);
    }

    public static final MyMapper<OrderFilterValue, String> KEY_ADAPTER = new MyMapper<OrderFilterValue, String>() {
        @Override
        public String apply(OrderFilterValue orderFilterValue) {
            return orderFilterValue.formCode;
        }
    };

    public static final UzumAdapter<OrderFilterValue> UZUM_ADAPTER = new UzumAdapter<OrderFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public OrderFilterValue read(UzumReader in) {
            return new OrderFilterValue(in.readString(),
                    in.readValue(ProductFilterValue.UZUM_ADAPTER),
                    in.readString(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, OrderFilterValue val) {
            out.write(val.formCode);
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.cardCode);
            out.write(val.hasValue);
            out.write(val.hasDiscount);
            out.write(val.warehouseAvail);
            out.write(val.sortFirstMll);
        }
    };
}
