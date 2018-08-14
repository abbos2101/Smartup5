package uz.greenwhite.smartup5_trade.m_outlet.row;// 21.09.2016

import uz.greenwhite.smartup5_trade.R;

public class OutletInfoRow {

    public static final Object TAG = new Object();

    public static final int STRING = 1;
    public static final int PHONE = 2;
    public static final int LOCATION = 3;

    public final String title;
    public final String detail;
    public final Object tag;
    public final int type;
    public final int icon;

    public OutletInfoRow(String title, String detail, Object tag, int type) {
        this.title = title;
        this.detail = detail;
        this.tag = tag;
        this.type = type;
        this.icon = getIcon();
    }

    public OutletInfoRow(String title, String detail, int type) {
        this(title, detail, TAG, type);
    }

    public boolean is(int type) {
        return this.type == type;
    }

    private int getIcon() {
        switch (type) {
            case PHONE:
                return R.drawable.ic_phone_black_36dp;
            case LOCATION:
                return R.drawable.ic_map_black_36dp;
            default:
                return 0;
        }
    }
}
