package uz.greenwhite.smartup5_trade.m_session.bean.role;// 13.09.2016

import android.util.SparseArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class RoleMenu {

    public static final String SESSION = "session";
    public static final String PERSON = "person";
    public static final String REPORT = "report";
    public static final String DUTY = "duty";
    public static final String WAREHOUSE = "warehouse";

    public final String code;
    public final MyArray<String> menuIds;

    public RoleMenu(String code, MyArray<String> menuIds) {
        this.code = code;
        this.menuIds = menuIds;
    }

    public static final MyMapper<RoleMenu, String> KEY_ADAPTER = new MyMapper<RoleMenu, String>() {
        @Override
        public String apply(RoleMenu val) {
            return val.code;
        }
    };

    public static final UzumAdapter<RoleMenu> UZUM_ADAPTER = new UzumAdapter<RoleMenu>() {
        @Override
        public RoleMenu read(UzumReader in) {
            return new RoleMenu(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, RoleMenu val) {
            out.write(val.code);
            out.write(val.menuIds, STRING_ARRAY);
        }
    };

    public static <T> MyArray<T> sortForms(ArgSession arg,
                                           String code,
                                           MyArray<T> forms,
                                           MyMapper<T, Integer> idAdapter) {
        return sortForms(arg.getScope(), code, forms, idAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    public static <T> MyArray<T> sortForms(final Scope scope,
                                           String code,
                                           MyArray<T> forms,
                                           MyMapper<T, Integer> idAdapter) {

        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        MyArray<String> menuIds = MyArray.emptyArray();

        try {
            for (Role role : roles) {
                for (RoleMenu menu : role.menus) {
                    if (menu.code.equals(code)) {
                        for (String menuId : menu.menuIds) {
                            if (!menuIds.contains(menuId, MyMapper.<String>identity())) {
                                menuIds = menuIds.append(menuId);
                            }
                        }
                    }
                }
            }

            if (menuIds.isEmpty()) {
                return MyArray.emptyArray();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            } else {
                ErrorUtil.saveThrowable(e);
            }
        }

        final SparseArray<T> fs = new SparseArray<T>();
        for (T f : forms) {
            fs.put(idAdapter.apply(f), f);
        }

        return menuIds.map(new MyMapper<String, T>() {
            @Override
            public T apply(String id) {
                return fs.get(Integer.parseInt(id));
            }
        }).filterNotNull();
    }
}
