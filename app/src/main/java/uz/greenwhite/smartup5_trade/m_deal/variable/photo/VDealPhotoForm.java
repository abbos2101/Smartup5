package uz.greenwhite.smartup5_trade.m_deal.variable.photo;// 30.06.2016


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.BitmapUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealApi;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class VDealPhotoForm extends VDealForm {

    public final ValueArray<VDealPhoto> photos;
    public final DealRef dealRef;


    public VDealPhotoForm(VisitModule module, ValueArray<VDealPhoto> photos, DealRef dealRef) {
        super(module);
        this.photos = photos;
        this.dealRef = dealRef;
    }


    public void addPhoto(Scope scope, Setting setting, PhotoType photoType, File photoFile, MyArray<String> texts) throws Exception {
        String entryId = String.valueOf(dealRef.dealHolder.deal.dealLocalId);
        Bitmap bitmap = BitmapUtil.decodeFile(photoFile, 100);
        if (setting.deal.dealPhotoWatermark) {
            bitmap = Utils.resizeByMaximum(bitmap, setting.common.photoConfig.size);
            bitmap = drawTextToBitmap(bitmap, texts);
        }

        String sha = scope.entry.savePhoto(scope, entryId, bitmap, setting.common.photoConfig);
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATETIME);
        ValueSpinner photoTypeSpinner = makePhotoType(photoType);
        VDealPhoto p = new VDealPhoto(sha, photoTypeSpinner, new ValueString(200), today, "");
        photos.append(p);

        if (AdminApi.isImageGallery(scope.accountId)) {
            copyPhotoToDCIM(bitmap);
        }
    }

    private ValueSpinner makePhotoType(PhotoType photoType) {
        MyArray<PhotoType> photoTypes = dealRef.getPhotoType();

        MyArray<SpinnerOption> options = photoTypes.map(new MyMapper<PhotoType, SpinnerOption>() {
            @Override
            public SpinnerOption apply(PhotoType p) {
                return new SpinnerOption(p.typeId, p.name, p);
            }
        });

        SpinnerOption option = options.find(String.valueOf(photoType.typeId), SpinnerOption.KEY_ADAPTER);
        if (option == null) {
            option = new SpinnerOption(photoType.typeId, photoType.name, photoType);
            options = options.append(option);
        }

        return new ValueSpinner(options, option);
    }

    public void makePhotoSaved(Scope scope, String sha, int state) {
        scope.ds.db.photoUpdateStateBySha(sha, state);
    }

    public void deletePhoto(String sha) {
        VDealPhoto dp = photos.getItems().find(sha, VDealPhoto.KEY_ADAPTER);
        photos.delete(dp);
    }

    public void removeNotSavedPhoto(Scope scope) {
        scope.ds.db.photoDeleteByState(EntryState.NOT_SAVED);
    }

    public Bitmap getPhoto(Scope scope, VDealPhoto photo) {
        return DealApi.getPhotoInTable(scope, photo.sha);
    }

    public Bitmap getFullPhoto(Scope scope, VDealPhoto photo) {
        return DealApi.getFullPhotoInTable(scope, photo.sha);
    }

    public VDealPhoto findVDealPhoto(String photoSha) {
        return this.photos.getItems().find(photoSha, VDealPhoto.KEY_ADAPTER);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(photos).toSuper();
    }

    @Override
    public boolean hasValue() {
        return photos.getItems().nonEmpty();
    }


    private String getGalleryPath() {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        folder = new File(folder.getAbsolutePath() + "/smartup5");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath();
    }

    private void copyPhotoToDCIM(Bitmap bitmap) throws IOException {
        String imageFileName = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATETIME) + ".jpg";
        File dst = new File(getGalleryPath(), imageFileName);
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            byte photo[] = BitmapUtil.toBytes(bitmap, 90);
            outChannel.write(ByteBuffer.wrap(photo));
        } finally {
            outChannel.close();
        }
    }

    private static Bitmap drawTextToBitmap(Bitmap photo, MyArray<String> texts) {
        AppError.checkNull(photo);

        int textSize = (int) DS.getResources().getDimension(R.dimen.watermark_size);
        int textLength = texts.size();

        Bitmap bitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawRect(0,
                bitmap.getHeight() - (textSize * (textLength + 1)),
                bitmap.getWidth(),
                bitmap.getHeight(), p);

        for (String text : texts) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            paint.setShadowLayer(4f, 0, 0, Color.WHITE);

            int y = (int) (bitmap.getHeight() - (paint.getTextSize() * textLength--));
            canvas.drawText(text, 10, y, paint);
        }

        return bitmap;
    }
}

