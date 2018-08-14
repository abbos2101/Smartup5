package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 26.07.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumException;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SettingPerson {

    public final Boolean createLegalPerson;
    public final Boolean showPersonDebtExists; // Kotragentni qarzdorligini (korsatish yoki korsatmaslik)
    public final Boolean showPersonDebtAmount; // Kotragentni qarzdorligini detailni Sumovoy qip korsatish (-/+ 900,000 (BC))


    public SettingPerson(Boolean createLegalPerson,
                         Boolean showPersonDebtExists,
                         Boolean showPersonDebtAmount) {
        this.createLegalPerson = createLegalPerson;
        this.showPersonDebtExists = showPersonDebtExists;
        this.showPersonDebtAmount = showPersonDebtAmount;
    }

    public boolean nonEmpty() {
        return createLegalPerson != null &&
                showPersonDebtExists != null &&
                showPersonDebtAmount != null;
    }

    public SettingPerson withParent(SettingPerson parent) {
        if (nonEmpty()) {
            return this;
        }
        return new SettingPerson(
                Util.nvl(this.createLegalPerson, parent.createLegalPerson),
                Util.nvl(this.showPersonDebtExists, parent.showPersonDebtExists),
                Util.nvl(this.showPersonDebtAmount, parent.showPersonDebtAmount)
        );
    }

    public static SettingPerson withParent(SettingPerson setting, SettingPerson parent) {
        if (setting != null) {
            return setting.withParent(parent);
        }
        return parent;
    }

    public static final UzumAdapter<SettingPerson> UZUM_ADAPTER = new UzumAdapter<SettingPerson>() {
        @Override
        public SettingPerson read(UzumReader in) {
            return new SettingPerson(in.readBoolean(),
                    in.readBoolean(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SettingPerson val) {
            throw UzumException.Unsupported();
        }
    };
}
