package uz.greenwhite.smartup5_trade.m_session.bean.role;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RoleSetting {

    public static final String KE_OUTLET_NAME = "1";
    public static final String KE_OUTLET_REGION = "2";
    public static final String KE_OUTLET_ADDRESS = "3";
    public static final String KE_OUTLET_ADDRESS_GUIDE = "4";
    public static final String KE_OUTLET_LOCATION = "5";
    public static final String KE_OUTLET_CODE = "6";
    public static final String KE_OUTLET_NOTE = "7";
    public static final String KE_OUTLET_PHONE = "8";
    public static final String KE_OUTLET_BARCODE = "9";
    public static final String KE_OUTLET_PLAN = "10";
    public static final String KE_OUTLET_GROUP = "11";
    public static final String KE_HOSPITAL = "12";
    public static final String KE_SPECIALTY = "13";
    public static final String KE_CABINET = "14";
    public static final String KE_PERSON_SHORT_NAME = "15";
    public static final String KE_PERSON_EMAIL = "16";
    public static final String KE_PERSON_PARENT_ID = "17";
    public static final String KE_ZIP_CODE = "18";

    public final String roleId;
    public final MyArray<String> outletModuleVisibles;
    public final MyArray<String> outletModuleEditings;
    public final MyArray<String> outletModuleRequireds;

    public RoleSetting(String roleId,
                       MyArray<String> outletModuleVisibles,
                       MyArray<String> outletModuleEditings,
                       MyArray<String> outletModuleRequireds) {
        this.roleId = roleId;
        this.outletModuleVisibles = outletModuleVisibles;
        this.outletModuleEditings = outletModuleEditings;
        this.outletModuleRequireds = MyArray.nvl(outletModuleRequireds);
    }

    public static final MyMapper<RoleSetting, String> KEY_ADAPTER = new MyMapper<RoleSetting, String>() {
        @Override
        public String apply(RoleSetting roleSetting) {
            return roleSetting.roleId;
        }
    };

    public static final UzumAdapter<RoleSetting> UZUM_ADAPTER = new UzumAdapter<RoleSetting>() {
        @Override
        public RoleSetting read(UzumReader in) {
            return new RoleSetting(in.readString(),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, RoleSetting val) {
            out.write(val.roleId);
            out.write(val.outletModuleVisibles, STRING_ARRAY);
            out.write(val.outletModuleEditings, STRING_ARRAY);
            out.write(val.outletModuleRequireds, STRING_ARRAY);
        }
    };
}
