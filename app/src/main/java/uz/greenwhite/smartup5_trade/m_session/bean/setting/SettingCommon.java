package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 26.07.2016

import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumException;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SettingCommon {

    public final Integer workTimeBegin;
    public final Integer workTimeEnd;
    public final Boolean syncEnable;
    public final Boolean gpsEnable;
    public final Integer syncInterval;
    public final Boolean workWithRoot;
    public final PhotoConfig photoConfig;
    public final Boolean showKPIPlanInDashboard;

    @Deprecated
    public final Boolean visitHistoryAllow;

    public SettingCommon(Integer workTimeBegin,
                         Integer workTimeEnd,
                         Boolean syncEnable,
                         Boolean gpsEnable,
                         Integer syncInterval,
                         PhotoConfig photoConfig,
                         Boolean visitHistoryAllow,
                         Boolean workWithRoot,
                         Boolean showKPIPlanInDashboard) {
        this.workTimeBegin = workTimeBegin;
        this.workTimeEnd = workTimeEnd;
        this.syncEnable = syncEnable;
        this.gpsEnable = gpsEnable;
        this.syncInterval = syncInterval;
        this.photoConfig = photoConfig;
        this.visitHistoryAllow = visitHistoryAllow;
        this.workWithRoot = workWithRoot;
        this.showKPIPlanInDashboard = showKPIPlanInDashboard;
    }

    public boolean nonEmpty() {
        return workTimeBegin != null &&
                workTimeEnd != null &&
                syncEnable != null &&
                gpsEnable != null &&
                syncInterval != null &&
                photoConfig != null &&
                visitHistoryAllow != null &&
                workWithRoot != null &&
                showKPIPlanInDashboard != null;
    }

    private SettingCommon withParent(SettingCommon parent) {
        if (nonEmpty()) {
            return this;
        }
        return new SettingCommon(
                Util.nvl(this.workTimeBegin, parent.workTimeBegin),
                Util.nvl(this.workTimeEnd, parent.workTimeEnd),
                Util.nvl(this.syncEnable, parent.syncEnable),
                Util.nvl(this.gpsEnable, parent.gpsEnable),
                Util.nvl(this.syncInterval, parent.syncInterval),
                Util.nvl(this.photoConfig, parent.photoConfig),
                Util.nvl(this.visitHistoryAllow, parent.visitHistoryAllow),
                Util.nvl(this.workWithRoot, parent.workWithRoot),
                Util.nvl(this.showKPIPlanInDashboard, parent.showKPIPlanInDashboard)
        );
    }

    public static SettingCommon withParent(SettingCommon setting, SettingCommon parent) {
        if (setting != null) {
            return setting.withParent(parent);
        }
        return parent;
    }

    public static final UzumAdapter<SettingCommon> UZUM_ADAPTER = new UzumAdapter<SettingCommon>() {
        @Override
        public SettingCommon read(UzumReader in) {
            return new SettingCommon(
                    NumberUtil.tryParse(in.readString()),
                    NumberUtil.tryParse(in.readString()),
                    in.readBoolean(), in.readBoolean(),
                    NumberUtil.tryParse(in.readString()),
                    PhotoConfig.make(in.readString()),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SettingCommon val) {
            throw UzumException.Unsupported();
        }
    };
}
