package uz.greenwhite.smartup5_trade.datasource;// 17.06.2016

import android.graphics.Bitmap;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.BitmapUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.datasource.AnorDataSource;
import uz.greenwhite.smartup.anor.datasource.persist.DatabaseMate;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.PhotoConfig;

public class DataSource extends AnorDataSource {

    public DataSource(DatabaseMate db) {
        super(db);
    }

    public String savePhoto(String id, String entryId, Bitmap bitmap, PhotoConfig photoConfig) {
        if (id == null || bitmap == null) {
            throw AppError.NullPointer();
        }
        try {
            Bitmap bitmapThumb = Utils.resizeByMaximum(bitmap, photoConfig.thumbSize);
            Bitmap bitmapPhoto = Utils.resizeByMaximum(bitmap, photoConfig.size);
            byte thumb[] = BitmapUtil.toBytes(bitmapThumb, 90);
            byte photo[] = BitmapUtil.toBytes(bitmapPhoto, 90);
            String sha = Util.calcSHA(photo);
            db.photoInsert(id, entryId, sha, thumb, photo);
            return sha;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
