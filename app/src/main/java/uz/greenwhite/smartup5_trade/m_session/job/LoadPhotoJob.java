package uz.greenwhite.smartup5_trade.m_session.job;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.OutputStream;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.http.HttpRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.ShortJob;

public class LoadPhotoJob implements ShortJob<Bitmap> {

    private final String url;

    public LoadPhotoJob(String url) {
        AppError.checkNull(url);
        this.url = url;
    }

    @Override
    public Bitmap execute() throws Exception {
        return HttpUtil.post(url, new HttpRequest<Bitmap>() {
            @Override
            public void send(OutputStream outputStream) throws Exception {

            }

            @Override
            public Bitmap receive(InputStream in) throws Exception {
                return BitmapFactory.decodeStream(in);
            }
        });
    }
}
