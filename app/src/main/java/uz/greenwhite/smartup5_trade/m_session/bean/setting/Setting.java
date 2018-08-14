package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 31.08.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Setting {

    public final SettingLocation location;
    public final SettingDeal deal;
    public final SettingCommon common;
    public final SettingPerson person;

    public Setting(SettingLocation location,
                   SettingDeal deal,
                   SettingCommon common,
                   SettingPerson person) {
        this.location = location;
        this.deal = deal;
        this.common = common;
        this.person = person;
    }

    public boolean nonEmpty() {
        return location.nonEmpty() &&
                deal.nonEmpty() &&
                common.nonEmpty() &&
                (person != null && person.nonEmpty());
    }

    @SuppressWarnings("WeakerAccess")
    public Setting withParent(Setting parent) {
        if (nonEmpty()) {
            return this;
        }
        return new Setting(
                SettingLocation.withParent(location, parent.location),
                SettingDeal.withParent(deal, parent.deal),
                SettingCommon.withParent(common, parent.common),
                SettingPerson.withParent(person, parent.person)
        );
    }

    public static Setting withParent(Setting setting, Setting parent) {
        if (setting != null) {
            return setting.withParent(parent);
        }
        return parent;
    }

    public static final Setting DEFAULT = new Setting(
            new SettingLocation(false),

            new SettingDeal(true, false, false, false,
                    0, "0", true, true, false,
                    false, false, false, false,
                    true, false, false, false),

            new SettingCommon(900, 1800, false, false,
                    0, PhotoConfig.DEFAULT, false, false,
                    true),

            new SettingPerson(false, true, true)
    );

    public static final UzumAdapter<Setting> UZUM_ADAPTER = new UzumAdapter<Setting>() {
        @Override
        public Setting read(UzumReader in) {
            return new Setting(
                    in.readValue(SettingLocation.UZUM_ADAPTER),
                    in.readValue(SettingDeal.UZUM_ADAPTER),
                    in.readValue(SettingCommon.UZUM_ADAPTER),
                    in.readValue(SettingPerson.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Setting val) {
            throw AppError.Unsupported();
        }
    };
}
