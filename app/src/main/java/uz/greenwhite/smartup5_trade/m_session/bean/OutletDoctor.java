package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletDoctor extends Outlet {

    public final String specialityId;
    public final String legalPersonId;

    public OutletDoctor(String id,
                        String name,
                        String address,
                        String addressGuide,
                        String latLng,
                        String phone,
                        String barcode,
                        String photoSha,
                        String regionId,
                        MyArray<OutletGroupValue> groupValues,
                        String specialityId,
                        String categorizationTypeId,
                        String legalPersonId,
                        String code,
                        String inn,
                        MyArray<BankAccount> bankAccounts,
                        String ownerName) {
        super(id, name, address, addressGuide, latLng, phone, barcode,
                photoSha, regionId, groupValues, K_HOSPITAL, categorizationTypeId,
                null, code, inn, bankAccounts, ownerName);
        this.specialityId = specialityId;
        this.legalPersonId = Util.nvl(legalPersonId);
    }

    public static final MyMapper<OutletDoctor, String> KEY_ADAPTER = new MyMapper<OutletDoctor, String>() {
        @Override
        public String apply(OutletDoctor outlet) {
            return outlet.id;
        }
    };

    public static final UzumAdapter<OutletDoctor> UZUM_ADAPTER = new UzumAdapter<OutletDoctor>() {
        @Override
        public OutletDoctor read(UzumReader in) {
            return new OutletDoctor(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readArray(OutletGroupValue.UZUM_ADAPTER),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readArray(BankAccount.UZUM_ADAPTER), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletDoctor val) {
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
            out.write(val.specialityId);
            out.write(val.categorizationTypeId);
            out.write(val.legalPersonId);
            out.write(val.code);
            out.write(val.inn);
            out.write(val.bankAccounts, BankAccount.UZUM_ADAPTER);
            out.write(val.ownerName);
        }
    };
}
