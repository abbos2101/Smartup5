package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonProps {

    public final String typeorg, department, typeown, orgform, laddress,
            inn, kopf, kfc, okonx, okpo, okud, coato, coogu, oked, svift, parentId;

    public PersonProps(String typeorg,
                       String department,
                       String typeown,
                       String orgform,
                       String laddress,
                       String inn,
                       String kopf,
                       String kfc,
                       String okonx,
                       String okpo,
                       String okud,
                       String coato,
                       String coogu,
                       String oked,
                       String svift,
                       String parentId) {
        this.typeorg = Util.nvl(typeorg);
        this.department = Util.nvl(department);
        this.typeown = Util.nvl(typeown);
        this.orgform = Util.nvl(orgform);
        this.laddress = Util.nvl(laddress);
        this.inn = Util.nvl(inn);
        this.kopf = Util.nvl(kopf);
        this.kfc = Util.nvl(kfc);
        this.okonx = Util.nvl(okonx);
        this.okpo = Util.nvl(okpo);
        this.okud = Util.nvl(okud);
        this.coato = Util.nvl(coato);
        this.coogu = Util.nvl(coogu);
        this.oked = Util.nvl(oked);
        this.svift = Util.nvl(svift);
        this.parentId = Util.nvl(parentId);
    }

    public static final PersonProps DEFAULT = new PersonProps(null, null, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, null, null);

    public static final UzumAdapter<PersonProps> UZUM_ADAPTER = new UzumAdapter<PersonProps>() {
        @Override
        public PersonProps read(UzumReader in) {
            return new PersonProps(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonProps val) {
            out.write(val.typeorg);
            out.write(val.department);
            out.write(val.typeown);
            out.write(val.orgform);
            out.write(val.laddress);
            out.write(val.inn);
            out.write(val.kopf);
            out.write(val.kfc);
            out.write(val.okonx);
            out.write(val.okpo);
            out.write(val.okud);
            out.write(val.coato);
            out.write(val.coogu);
            out.write(val.oked);
            out.write(val.svift);
            out.write(val.parentId);
        }
    };
}
