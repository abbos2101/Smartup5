package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 31.08.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SettingLocation {

    public static final int ACCURACY = 100;

    public final Boolean takeRequired;

    public SettingLocation(Boolean takeRequired) {
        this.takeRequired = takeRequired;
    }

    public boolean nonEmpty() {
        return takeRequired != null;
    }

    public static SettingLocation withParent(SettingLocation setting, SettingLocation parent) {
        if (setting != null) {
            return setting.withParent(parent);
        }
        return parent;
    }

    public SettingLocation withParent(SettingLocation parent) {
        if (nonEmpty()) {
            return this;
        }
        return new SettingLocation(
                Util.nvl(this.takeRequired, parent.takeRequired)
        );
    }

    public static final UzumAdapter<SettingLocation> UZUM_ADAPTER = new UzumAdapter<SettingLocation>() {
        @Override
        public SettingLocation read(UzumReader in) {
            return new SettingLocation(in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SettingLocation val) {
            throw AppError.Unsupported();
        }
    };
}
