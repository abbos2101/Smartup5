package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class Outlet {

    public static final String K_PHARMACY = "P";
    public static final String K_HOSPITAL = "H";

    public final String id;
    public final String name;
    public final String address;
    public final String addressGuide;
    public final String latLng;
    public final String phone;
    public final String barcode;
    public final MyArray<OutletGroupValue> groupValues;
    public final String photoSha;
    public final String regionId;
    public final String personKind;
    public final String categorizationTypeId;
    public final String parentId;
    public final String code;
    public final String inn;
    public final MyArray<BankAccount> bankAccounts;
    public final String ownerName;

    protected Outlet(String id,
                     String name,
                     String address,
                     String addressGuide,
                     String latLng,
                     String phone,
                     String barcode,
                     String photoSha,
                     String regionId,
                     MyArray<OutletGroupValue> groupValues,
                     String personKind,
                     String categorizationTypeId,
                     String parentId,
                     String code,
                     String inn,
                     MyArray<BankAccount> bankAccounts,
                     String ownerName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressGuide = addressGuide;
        this.latLng = latLng;
        this.phone = phone;
        this.barcode = barcode;
        this.groupValues = MyArray.nvl(groupValues);
        this.photoSha = Util.nvl(photoSha);
        this.regionId = Util.nvl(regionId);
        this.personKind = Util.nvl(personKind);
        this.categorizationTypeId = Util.nvl(categorizationTypeId);
        this.parentId = Util.nvl(parentId);
        this.code = Util.nvl(code);
        this.inn = Util.nvl(inn);
        this.bankAccounts = MyArray.nvl(bankAccounts);
        this.ownerName = Util.nvl(ownerName);
    }

    private Outlet(String id,
                   String name,
                   String address,
                   String addressGuide,
                   String latLng,
                   String phone,
                   String barcode,
                   String photoSha,
                   String regionId,
                   MyArray<OutletGroupValue> groupValues,
                   String categorizationTypeId,
                   String parentId,
                   String code,
                   String inn,
                   MyArray<BankAccount> bankAccounts,
                   String ownerName) {
        this(id, name, address, addressGuide, latLng, phone, barcode,
                photoSha, regionId, groupValues, null, categorizationTypeId, parentId,
                code, inn, bankAccounts, ownerName);
    }

    public Outlet(String id, String name, String address) {
        this(id, name, address, "",
                "", "", "", "", "",
                null, null, null, "",
                "", MyArray.<BankAccount>emptyArray(), "");
    }

    public static String getPersonKindName(String personKind) {
        if (TextUtils.isEmpty(personKind)) {
            return DS.getString(R.string.person_kind_person_name);
        }

        switch (personKind) {
            case K_HOSPITAL:
                return DS.getString(R.string.person_kind_doctor_name);
            case K_PHARMACY:
                return DS.getString(R.string.person_kind_pharm_name);
            default:
                return "";
        }
    }

    public boolean isOutlet() {
        return TextUtils.isEmpty(personKind);
    }

    public boolean isDoctor() {
        return K_HOSPITAL.equals(personKind);
    }

    public boolean isPharm() {
        return K_PHARMACY.equals(personKind);
    }

    public String getAddress() {
        return TextUtils.isEmpty(this.address) ? DS.getString(R.string.address_unknown) : this.address;
    }

    public boolean hasLocation() {
        return !TextUtils.isEmpty(latLng);
    }

    public int getIconBackground() {
        String lastNumber = id.substring(id.length() - 1, id.length());
        switch (lastNumber) {
            case "1":
                return R.drawable.bg_1;
            case "2":
                return R.drawable.bg_2;
            case "3":
                return R.drawable.bg_3;
            case "4":
                return R.drawable.bg_4;
            case "5":
                return R.drawable.bg_5;
            case "6":
                return R.drawable.bg_6;
            case "7":
                return R.drawable.bg_7;
            case "8":
                return R.drawable.bg_2;
            case "9":
                return R.drawable.bg_6;
            case "0":
                return R.drawable.bg_7;
            default:
                return R.drawable.bg_3;
        }
    }

    public static final MyMapper<Outlet, String> KEY_ADAPTER = new MyMapper<Outlet, String>() {
        @Override
        public String apply(Outlet outlet) {
            return outlet.id;
        }
    };

    public static final UzumAdapter<Outlet> UZUM_ADAPTER = new UzumAdapter<Outlet>() {
        @Override
        public Outlet read(UzumReader in) {
            return new Outlet(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(), in.readString(),
                    in.readArray(OutletGroupValue.UZUM_ADAPTER),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readArray(BankAccount.UZUM_ADAPTER), in.readString());
        }

        @Override
        public void write(UzumWriter out, Outlet val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.address);
            out.write(val.addressGuide);
            out.write(val.latLng);
            out.write(val.phone);
            out.write(val.barcode);
            out.write(val.photoSha);
            out.write(val.regionId);
            out.write(val.groupValues, OutletGroupValue.UZUM_ADAPTER);
            out.write(val.categorizationTypeId);
            out.write(val.parentId);
            out.write(val.code);
            out.write(val.inn);
            out.write(val.bankAccounts, BankAccount.UZUM_ADAPTER);
            out.write(val.ownerName);
        }
    };
}
