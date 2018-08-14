package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 14.12.2016

import android.text.TextUtils;

import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.smartup.anor.ErrorUtil;

public class PhotoConfig {

    public final int size;
    public final int thumbSize;

    private PhotoConfig(int size, int thumbSize) {
        this.size = size;
        this.thumbSize = thumbSize;
    }

    public static final PhotoConfig DEFAULT = new PhotoConfig(640, 160);

    private static int parsePhotoSize(String photoSize) {
        try {
            if (!TextUtils.isEmpty(photoSize)) {
                if (CharSequenceUtil.containsIgnoreCase(photoSize, "x")) {
                    String size = photoSize.split("x")[0];
                    return Integer.parseInt(size);
                } else {
                    return Integer.parseInt(photoSize);
                }
            }
        } catch (Exception ex) {
            ErrorUtil.saveThrowable(new Exception("photo size = " + photoSize, ex));
        }
        return DEFAULT.size;
    }

    public static PhotoConfig make(String photoSize) {
        int size = Math.max(Math.min(parsePhotoSize(photoSize), 1000), 200);
        return new PhotoConfig(size, DEFAULT.thumbSize);
    }
}
