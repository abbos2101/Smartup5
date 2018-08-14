package uz.greenwhite.smartup5_trade.m_session.arg;// 27.06.2016

import android.support.annotation.Nullable;

import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.bean.user.User;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class ArgSession {

    public final String accountId;
    public final String filialId;

    public ArgSession(String accountId, String filialId) {
        this.accountId = accountId;
        this.filialId = filialId;
    }

    public ArgSession(UzumReader in) {
        this(in.readString(), in.readString());
    }

    public Scope getScope() {
        SysUtil.checkMainLooper("ArgSession.getScope is not main loop");
        return DS.getScope(accountId, filialId);
    }

    @Nullable
    public User getUser() {
        return getScope().ref.getUser();
    }

    public Filial getFilial() {
        return getScope().ref.getFilial(filialId);
    }

    public Setting getSetting() {
        return getScope().ref.getSettingWithDefault();
    }

    public void write(UzumWriter w) {
        w.write(this.accountId);
        w.write(this.filialId);
    }

    public Account getAccount() {
        return AdminApi.getAccount(this.accountId);
    }

    public static final UzumAdapter<ArgSession> UZUM_ADAPTER = new UzumAdapter<ArgSession>() {
        @Override
        public ArgSession read(UzumReader in) {
            return new ArgSession(in);
        }

        @Override
        public void write(UzumWriter out, ArgSession val) {
            val.write(out);
        }
    };
}
