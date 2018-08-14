package uz.greenwhite.smartup5_trade.m_display.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Display {

    public final String displayInventId;
    public final String displayId;
    public final String code;
    public final String shortName;
    public final String barcode;
    public final String inventNumber;
    public final String photoSha;

    public Display(String displayInventId,
                   String displayId,
                   String code,
                   String shortName,
                   String barcode,
                   String inventNumber,
                   String photoSha) {
        this.displayInventId = displayInventId;
        this.displayId = displayId;
        this.code = code;
        this.shortName = shortName;
        this.barcode = barcode;
        this.inventNumber = inventNumber;
        this.photoSha = photoSha;
    }

    public static final MyMapper<Display,String>KEY_ADAPTER = new MyMapper<Display, String>() {
        @Override
        public String apply(Display display) {
            return display.displayInventId;
        }
    };

    public static final UzumAdapter<Display> UZUM_ADAPTER = new UzumAdapter<Display>() {
        @Override
        public Display read(UzumReader in) {
            return new Display(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Display val) {
            out.write(val.displayInventId);
            out.write(val.displayId);
            out.write(val.code);
            out.write(val.shortName);
            out.write(val.barcode);
            out.write(val.inventNumber);
            out.write(val.photoSha);
        }
    };
}
