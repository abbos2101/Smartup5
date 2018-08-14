package uz.greenwhite.smartup5_trade.m_deal.filter;


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.common.predicate.ProductFilterValue;

public class RetailAuditFilterValue {
    public final String formCode;
    public final ProductFilterValue product;
    public final boolean hasValue;

    public RetailAuditFilterValue(String formCode,
                                  ProductFilterValue product,
                                  boolean hasValue) {
        this.formCode = formCode;
        this.product = product;
        this.hasValue = hasValue;
    }

    public static RetailAuditFilterValue makeDefault(String formCode, boolean mhl) {
        ProductFilterValue p = new ProductFilterValue(null, false, false, mhl, false);
        return new RetailAuditFilterValue(formCode, p, false);
    }

    public static final RetailAuditFilterValue DEFAULT = new RetailAuditFilterValue(null, null, false);

    public static final MyMapper<RetailAuditFilterValue, String> KEY_ADAPTER = new MyMapper<RetailAuditFilterValue, String>() {
        @Override
        public String apply(RetailAuditFilterValue filterValue) {
            return filterValue.formCode;
        }
    };

    public static final UzumAdapter<RetailAuditFilterValue> UZUM_ADAPTER = new UzumAdapter<RetailAuditFilterValue>() {
        @Override
        public RetailAuditFilterValue read(UzumReader in) {
            return new RetailAuditFilterValue(in.readString(), in.readValue(ProductFilterValue.UZUM_ADAPTER), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, RetailAuditFilterValue val) {
            out.write(val.formCode);
            out.write(val.product, ProductFilterValue.UZUM_ADAPTER);
            out.write(val.hasValue);
        }
    };
}