package uz.greenwhite.smartup5_trade.schedule;

import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class GPSPref {

    public static final String K_CARE_WORK = "schedule:care_work";

    //----------------------------------------------------------------------------------------------

    public static void setNotifyCareWorkTime(String accountId) {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        DS.getPref().save(K_CARE_WORK + ":" + accountId, today);
    }

    public static boolean calledNotifyCareWork(String accountId) {
        String lastTime = Util.nvl(DS.getPref().load(K_CARE_WORK + ":" + accountId));
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return today.equals(lastTime);
    }


    //----------------------------------------------------------------------------------------------

}
