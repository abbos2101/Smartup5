package uz.greenwhite.smartup5_trade.m_display.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DisplayRequest {

    public static MyArray<Integer> getStates() {
        return MyArray.from(NOT_FOUND, FOUND, NEW, LINKED);
    }

    public static final int NOT_FOUND = 0;
    public static final int FOUND = 1;
    public static final int NEW = 2;
    public static final int LINKED = 3;

    public final String barcode, displayInventId, photoSha, note;
    public final Integer state;

    public DisplayRequest(String barcode,
                          String displayInventId,
                          String photoSha,
                          String note,
                          Integer state) {
        /* if reading entry old Display on new version
         * auto unlink all @class DisplayRequest change @params DisplayRequest.state=NEW
         * Hamasini New qilib jonatadi
         * */
        if (state == null || state == -1) {
            displayInventId = "";
            state = NEW;
        }
        this.barcode = barcode;
        this.displayInventId = displayInventId;
        this.photoSha = Util.nvl(photoSha);
        this.note = Util.nvl(note);
        this.state = state;

        if (TextUtils.isEmpty(barcode)) {
            throw AppError.NullPointer();
        }
    }

    public static final MyMapper<DisplayRequest, String> KEY_ADAPTER = new MyMapper<DisplayRequest, String>() {
        @Override
        public String apply(DisplayRequest val) {
            return val.barcode;
        }
    };

    public static UzumAdapter<DisplayRequest> UZUM_ADAPTER = new UzumAdapter<DisplayRequest>() {
        @Override
        public DisplayRequest read(UzumReader in) {
            return new DisplayRequest(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readInteger());
        }

        @Override
        public void write(UzumWriter out, DisplayRequest val) {
            out.write(val.barcode);
            out.write(val.displayInventId);
            out.write(val.photoSha);
            out.write(val.note);
            out.write(val.state);
        }
    };
}
