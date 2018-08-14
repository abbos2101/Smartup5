package uz.greenwhite.smartup5_trade.m_product.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;


public class PhotoInfo {

    public final String fileSha;
    public final String title;
    public final String note;
    public final String orderNo;
    public final String contentType;

    public PhotoInfo(String fileSha, String title, String note, String orderNo, String contentType) {
        this.fileSha = fileSha;
        this.title = title;
        this.note = note;
        this.orderNo = orderNo;
        this.contentType = contentType;
    }

    public static final UzumAdapter<PhotoInfo> UZUM_ADAPTER = new UzumAdapter<PhotoInfo>() {
        @Override
        public PhotoInfo read(UzumReader in) {
            return new PhotoInfo(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PhotoInfo val) {
            out.write(val.fileSha);
            out.write(val.title);
            out.write(val.note);
            out.write(val.orderNo);
            out.write(val.contentType);
        }
    };
}
