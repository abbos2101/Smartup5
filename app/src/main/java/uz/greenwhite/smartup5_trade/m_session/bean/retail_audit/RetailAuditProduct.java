package uz.greenwhite.smartup5_trade.m_session.bean.retail_audit;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RetailAuditProduct {

    public final String productId;
    public final Boolean ourProduct;

    public RetailAuditProduct(String productId, Boolean ourProduct) {
        this.productId = productId;
        this.ourProduct = ourProduct;
    }

    public static final MyMapper<RetailAuditProduct, String> KEY_ADAPTER = new MyMapper<RetailAuditProduct, String>() {
        @Override
        public String apply(RetailAuditProduct retailAuditProduct) {
            return retailAuditProduct.productId;
        }
    };

    public static final UzumAdapter<RetailAuditProduct> UZUM_ADAPTER = new UzumAdapter<RetailAuditProduct>() {
        @Override
        public RetailAuditProduct read(UzumReader in) {
            return new RetailAuditProduct(in.readString(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, RetailAuditProduct val) {
            out.write(val.productId);
            out.write(val.ourProduct);
        }
    };
}
