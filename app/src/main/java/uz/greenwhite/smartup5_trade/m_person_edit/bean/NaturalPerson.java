package uz.greenwhite.smartup5_trade.m_person_edit.bean;// 20.12.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class NaturalPerson {

    public static final String K_G_MALE = "M";
    public static final String K_G_FEMALE = "F";

    public final String personId;
    public final String name;
    public final String surname;
    public final String patronymic;
    public final String gender;
    public final String phone;
    public final String birthday;
    public final String email;
    public final String address;
    public final String code;

    public NaturalPerson(String personId,
                         String name,
                         String surname,
                         String patronymic,
                         String gender,
                         String phone,
                         String birthday,
                         String email,
                         String address,
                         String code) {
        this.personId = Util.nvl(personId);
        this.name = Util.nvl(name);
        this.surname = Util.nvl(surname);
        this.patronymic = Util.nvl(patronymic);
        this.gender = Util.nvl(gender);
        this.phone = Util.nvl(phone);
        this.birthday = Util.nvl(birthday);
        this.email = Util.nvl(email);
        this.address = Util.nvl(address);
        this.code = Util.nvl(code);
    }

    public static final NaturalPerson DEFAULT = new NaturalPerson(null, null, null, null,
            null, null, null, null, null, null);

    public static final UzumAdapter<NaturalPerson> UZUM_ADAPTER = new UzumAdapter<NaturalPerson>() {
        @Override
        public NaturalPerson read(UzumReader in) {
            return new NaturalPerson(
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, NaturalPerson val) {
            out.write(val.personId);
            out.write(val.name);
            out.write(val.surname);
            out.write(val.patronymic);
            out.write(val.gender);
            out.write(val.phone);
            out.write(val.birthday);
            out.write(val.email);
            out.write(val.address);
            out.write(val.code);
        }
    };
}
