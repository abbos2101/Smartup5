package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Filial {

    public static final String FILIAL_HEAD = "0";

    public final String id;
    public final RoundModel roundModel;
    public final MyArray<String> roleIds;
    public final MyArray<String> roomIds;
    public final MyArray<String> photoTypeIds;
    public final MyArray<String> productIds;
    public final MyArray<String> moduleIds;
    public final MyArray<String> requiredIds;
    public final MyArray<String> actionIds;
    public final String serverTime;
    public final MyArray<String> pharmacyIds;
    public final MyArray<String> doctorIds;
    public final MyArray<String> requiredPharmacyIds;
    public final MyArray<String> requiredDoctorIds;
    public final MyArray<String> unreadMessageIds;
    public final MyArray<String> overloadIds;
    public final MyArray<String> warehouseIds;
    public final MyArray<String> currencyIds;
    public final MyArray<String> providerPersonIds; // Поставщик
    public final MyArray<String> unviewedTaskIds;

    public Filial(String id,
                  String roundModel,
                  MyArray<String> roleIds,
                  MyArray<String> roomIds,
                  MyArray<String> photoTypeIds,
                  MyArray<String> productIds,
                  MyArray<String> moduleIds,
                  MyArray<String> requiredIds,
                  MyArray<String> actionIds,
                  String serverTime,
                  MyArray<String> pharmacyIds,
                  MyArray<String> doctorIds,
                  MyArray<String> requiredPharmacyIds,
                  MyArray<String> requiredDoctorIds,
                  MyArray<String> unreadMessageIds,
                  MyArray<String> overloadIds,
                  MyArray<String> warehouseIds,
                  MyArray<String> currencyIds,
                  MyArray<String> providerPersonIds,
                  MyArray<String> unviewedTaskIds) {
        this.id = id;
        this.roundModel = RoundModel.make(roundModel);
        this.roleIds = MyArray.nvl(roleIds);
        this.roomIds = MyArray.nvl(roomIds);
        this.photoTypeIds = MyArray.nvl(photoTypeIds);
        this.productIds = MyArray.nvl(productIds);
        this.moduleIds = MyArray.nvl(moduleIds);
        this.requiredIds = MyArray.nvl(requiredIds);
        this.actionIds = MyArray.nvl(actionIds);
        this.serverTime = Util.nvl(serverTime);
        this.pharmacyIds = MyArray.nvl(pharmacyIds);
        this.doctorIds = MyArray.nvl(doctorIds);
        this.requiredPharmacyIds = MyArray.nvl(requiredPharmacyIds);
        this.requiredDoctorIds = MyArray.nvl(requiredDoctorIds);
        this.unreadMessageIds = unreadMessageIds;
        this.overloadIds = MyArray.nvl(overloadIds);
        this.warehouseIds = MyArray.nvl(warehouseIds);
        this.currencyIds = MyArray.nvl(currencyIds);
        this.providerPersonIds = MyArray.nvl(providerPersonIds);
        this.unviewedTaskIds = MyArray.nvl(unviewedTaskIds);
    }

    public MyArray<VisitModule> getVisitModules(String personKind) {
        MyArray<String> mModuleIds = this.moduleIds;
        MyArray<String> mRequiredIds = this.requiredIds;

        switch (personKind) {
            case Outlet.K_HOSPITAL:
                mModuleIds = moduleIds.union(doctorIds, MyMapper.<String>identity());
                mRequiredIds = requiredIds.union(requiredDoctorIds, MyMapper.<String>identity());
                break;

            case Outlet.K_PHARMACY:
                mModuleIds = moduleIds.union(pharmacyIds, MyMapper.<String>identity());
                mRequiredIds = requiredIds.union(requiredPharmacyIds, MyMapper.<String>identity());
                break;
        }

        final MyArray<String> finalMRequiredIds = mRequiredIds;
        return mModuleIds.filter(new MyPredicate<String>() {
            @Override
            public boolean apply(String s) {
                return !TextUtils.isEmpty(s);
            }
        }).map(new MyMapper<String, VisitModule>() {
            @Override
            public VisitModule apply(String id) {
                boolean required = finalMRequiredIds.contains(id, MyMapper.<String>identity());
                return new VisitModule(Integer.parseInt(id), required);
            }
        });
    }

    public static final MyMapper<Filial, String> KEY_ADAPTER = new MyMapper<Filial, String>() {
        @Override
        public String apply(Filial filial) {
            return filial.id;
        }
    };

    public static final UzumAdapter<Filial> UZUM_ADAPTER = new UzumAdapter<Filial>() {
        @Override
        public Filial read(UzumReader in) {
            return new Filial(
                    in.readString(), in.readString(),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readString(),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, Filial val) {
            out.write(val.id);
            out.write(val.roundModel.model);
            out.write(val.roleIds, STRING_ARRAY);
            out.write(val.roomIds, STRING_ARRAY);
            out.write(val.photoTypeIds, STRING_ARRAY);
            out.write(val.productIds, STRING_ARRAY);
            out.write(val.moduleIds, STRING_ARRAY);
            out.write(val.requiredIds, STRING_ARRAY);
            out.write(val.actionIds, STRING_ARRAY);
            out.write(val.serverTime);
            out.write(val.pharmacyIds, STRING_ARRAY);
            out.write(val.doctorIds, STRING_ARRAY);
            out.write(val.requiredPharmacyIds, STRING_ARRAY);
            out.write(val.requiredDoctorIds, STRING_ARRAY);
            out.write(val.unreadMessageIds, STRING_ARRAY);
            out.write(val.overloadIds, STRING_ARRAY);
            out.write(val.warehouseIds, STRING_ARRAY);
            out.write(val.currencyIds, STRING_ARRAY);
            out.write(val.providerPersonIds, STRING_ARRAY);
            out.write(val.unviewedTaskIds, STRING_ARRAY);
        }
    };
}