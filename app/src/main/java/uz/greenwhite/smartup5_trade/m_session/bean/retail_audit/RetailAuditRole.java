package uz.greenwhite.smartup5_trade.m_session.bean.retail_audit;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RetailAuditRole {

    public final String roleId;
    public final MyArray<String> retailAuditSetIds;

    public RetailAuditRole(String roleId, MyArray<String> retailAuditSetIds) {
        this.roleId = roleId;
        this.retailAuditSetIds = retailAuditSetIds;
    }

    public static final UzumAdapter<RetailAuditRole> UZUM_ADAPTER = new UzumAdapter<RetailAuditRole>() {
        @Override
        public RetailAuditRole read(UzumReader in) {
            return new RetailAuditRole(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, RetailAuditRole val) {
            out.write(val.roleId);
            out.write(val.retailAuditSetIds, STRING_ARRAY);
        }
    };
}
