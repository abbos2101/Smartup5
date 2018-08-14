package uz.greenwhite.smartup5_trade.m_person_edit.arg;// 20.12.2016

import android.text.TextUtils;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;

public class ArgPerson extends ArgSession {

    public static final UzumAdapter<ArgPerson> UZUM_ADAPTER = new UzumAdapter<ArgPerson>() {
        @Override
        public ArgPerson read(UzumReader in) {
            return new ArgPerson(in);
        }

        @Override
        public void write(UzumWriter out, ArgPerson val) {
            val.write(out);
        }
    };

    public final String personId;
    public final String roomId;
    public final String personKind;

    public ArgPerson(ArgSession arg, String personId, String roomId, String personKind) {
        super(arg.accountId, arg.filialId);
        this.personId = personId;
        this.roomId = roomId;
        this.personKind = personKind;
    }

    public ArgPerson(ArgSession arg, String personId, String roomId) {
        super(arg.accountId, arg.filialId);
        this.personId = personId;
        this.roomId = roomId;
        this.personKind = "";
    }

    public ArgPerson(UzumReader in) {
        super(in);
        this.personId = in.readString();
        this.roomId = in.readString();
        this.personKind = in.readString();
    }

    public boolean isPharmOrDoctor() {
        switch (Util.nvl(personKind)) {
            case PersonInfo.K_PHARMACY:
            case PersonInfo.K_DOCTOR:
            case PersonInfo.K_HOSPITAL:
                return true;
            default:
                return false;
        }
    }

    public boolean editPerson() {
        return !TextUtils.isEmpty(personId);
    }

    public PersonInfo getPersonInfo() {
        return PersonInfo.makeDefault(this.filialId, this.roomId, this.personKind);
    }

    public NaturalPersonInfo getNaturalPersonInfo() {
        return NaturalPersonInfo.makeDefault(this.filialId, this.roomId);
    }

    public Room getRoom() {
        return getScope().ref.getRoom(roomId);
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.personId);
        w.write(this.roomId);
        w.write(this.personKind);
    }
}
