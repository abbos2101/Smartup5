package uz.greenwhite.smartup5_trade.m_session.bean.retail_audit;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RetailAuditSet {

    public final String retailAuditSetId;
    public final String name;
    public final String code;
    public final MyArray<String> productIds;

    public RetailAuditSet(String retailAuditSetId, String name, String code, MyArray<String> productIds) {
        this.retailAuditSetId = retailAuditSetId;
        this.name = name;
        this.code = code;
        this.productIds = productIds;
    }

    public static final MyMapper<RetailAuditSet, String> KEY_ADAPTER = new MyMapper<RetailAuditSet, String>() {
        @Override
        public String apply(RetailAuditSet retailAuditSet) {
            return retailAuditSet.retailAuditSetId;
        }
    };

    public static final UzumAdapter<RetailAuditSet> UZUM_ADAPTER = new UzumAdapter<RetailAuditSet>() {
        @Override
        public RetailAuditSet read(UzumReader in) {
            return new RetailAuditSet(in.readString(), in.readString(),
                    in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, RetailAuditSet val) {
            out.write(val.retailAuditSetId);
            out.write(val.name);
            out.write(val.code);
            out.write(val.productIds, STRING_ARRAY);
        }
    };
}
