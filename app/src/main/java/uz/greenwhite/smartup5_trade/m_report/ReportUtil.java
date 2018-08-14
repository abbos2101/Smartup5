package uz.greenwhite.smartup5_trade.m_report;// 05.09.2016

import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;

public class ReportUtil {

    public static final int REP_001 = 1;
    public static final int REP_002 = 2;
    public static final int REP_003 = 3;
    public static final int REP_004 = 4;
    public static final int REP_005 = 5;
    public static final int REP_006 = 6;
    public static final int REP_007 = 7;
    public static final int REP_008 = 8;
    public static final int REP_009 = 9;
    public static final int REP_010 = 10;
    public static final int REP_011 = 11;
    public static final int REP_012 = 12;

    public static String getRepTitle(String code) {
        switch (code) {
            case RT.FMCG_REP_001:
                return DS.getString(R.string.rep_title_001);
            case RT.FMCG_REP_002:
                return DS.getString(R.string.rep_title_002);
            case RT.FMCG_REP_003:
                return DS.getString(R.string.rep_title_003);
            case RT.FMCG_REP_004:
                return DS.getString(R.string.rep_title_004);
            case RT.FMCG_REP_005:
                return DS.getString(R.string.rep_title_005);
            case RT.FMCG_REP_006:
                return DS.getString(R.string.rep_title_006);
            case RT.FMCG_REP_007:
                return DS.getString(R.string.rep_title_007);
            case RT.FMCG_REP_008:
                return DS.getString(R.string.rep_title_008);
            case RT.FMCG_REP_009:
                return DS.getString(R.string.rep_title_009);
            case RT.FMCG_REP_010:
                return DS.getString(R.string.rep_title_010);
            case RT.FMCG_REP_011:
                return DS.getString(R.string.rep_title_011);
            case RT.FMCG_REP_012:
                return DS.getString(R.string.rep_title_012);
            default:
                return code;
        }
    }
}
