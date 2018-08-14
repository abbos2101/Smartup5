package uz.greenwhite.smartup5_trade.m_session.bean.role;// 13.09.2016

import android.util.SparseArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Role {

    public static final String DOCTOR = "pharm:r1";
    public static final String PHARMACY = "pharm:r2";
    public static final String PHARMCY_PLUS_DOCTOR = "pharm:r3";

    public final String roleId;
    public final String name;
    public final MyArray<RoleMenu> menus;
    public final String pCode;

    public Role(String roleId, String name, MyArray<RoleMenu> menus, String pCode) {
        AppError.checkNull(menus);

        this.roleId = roleId;
        this.name = name;
        this.menus = menus;
        this.pCode = pCode;
    }

    public <T> MyArray<T> sortForms(String code, MyArray<T> forms, MyMapper<T, Integer> idAdapter) {
        RoleMenu roleMenu = menus.find(code, RoleMenu.KEY_ADAPTER);
        if (roleMenu == null) {
            return forms;
        }
        final SparseArray<T> fs = new SparseArray<T>();
        for (T f : forms) {
            fs.put(idAdapter.apply(f), f);
        }
        return roleMenu.menuIds.map(new MyMapper<String, T>() {
            @Override
            public T apply(String id) {
                return fs.get(Integer.parseInt(id));
            }
        }).filterNotNull();
    }

    public static final MyMapper<Role, String> KEY_ADAPTER = new MyMapper<Role, String>() {
        @Override
        public String apply(Role val) {
            return val.roleId;
        }
    };

    public static final UzumAdapter<Role> UZUM_ADAPTER = new UzumAdapter<Role>() {
        @Override
        public Role read(UzumReader in) {
            return new Role(in.readString(), in.readString(),
                    in.readArray(RoleMenu.UZUM_ADAPTER), in.readString());
        }

        @Override
        public void write(UzumWriter out, Role val) {
            out.write(val.roleId);
            out.write(val.name);
            out.write(val.menus, RoleMenu.UZUM_ADAPTER);
            out.write(val.pCode);
        }
    };
}
