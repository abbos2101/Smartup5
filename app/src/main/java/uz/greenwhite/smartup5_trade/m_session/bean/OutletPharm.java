package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletPharm extends Outlet {

    public OutletPharm(String id,
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
        super(id, name, address, addressGuide, latLng, phone, barcode,
                photoSha, regionId, groupValues, K_PHARMACY, categorizationTypeId,
                parentId, code, inn, bankAccounts, ownerName);
    }

    public static final MyMapper<OutletPharm, String> KEY_ADAPTER = new MyMapper<OutletPharm, String>() {
        @Override
        public String apply(OutletPharm outlet) {
            return outlet.id;
        }
    };

    public static final UzumAdapter<OutletPharm> UZUM_ADAPTER = new UzumAdapter<OutletPharm>() {
        @Override
        public OutletPharm read(UzumReader in) {
            return new OutletPharm(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readArray(OutletGroupValue.UZUM_ADAPTER),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readArray(BankAccount.UZUM_ADAPTER), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletPharm val) {
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
