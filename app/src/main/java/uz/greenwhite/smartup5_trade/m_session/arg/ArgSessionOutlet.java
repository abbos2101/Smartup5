package uz.greenwhite.smartup5_trade.m_session.arg;// 27.06.2016

import android.text.TextUtils;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class ArgSessionOutlet extends ArgSession {

    public final String outletKind;

    public ArgSessionOutlet(ArgSession arg, String outletKind) {
        super(arg.accountId, arg.filialId);
        this.outletKind = outletKind;
    }

    public ArgSessionOutlet(UzumReader in) {
        super(in);
        this.outletKind = in.readString();
    }

    public void write(UzumWriter w) {
        super.write(w);
        w.write(outletKind);
    }

    public CharSequence getPersonTitle() {
        if (isDoctor()) return DS.getString(R.string.session_outlet_doctors);
        if (isPharm()) return DS.getString(R.string.session_outlet_pharms);
        if (isOutlet()) return DS.getString(R.string.session_outlets);
        return DS.getString(R.string.session_outlets);
    }

    public boolean isDoctor() {
        return Outlet.K_HOSPITAL.equals(outletKind);
    }

    public boolean isPharm() {
        return Outlet.K_PHARMACY.equals(outletKind);
    }

    public boolean isOutlet() {
        return TextUtils.isEmpty(outletKind);
    }

    public static final UzumAdapter<ArgSessionOutlet> UZUM_ADAPTER = new UzumAdapter<ArgSessionOutlet>() {
        @Override
        public ArgSessionOutlet read(UzumReader in) {
            return new ArgSessionOutlet(in);
        }

        @Override
        public void write(UzumWriter out, ArgSessionOutlet val) {
            val.write(out);
        }
    };
}
