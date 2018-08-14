package uz.greenwhite.smartup5_trade.m_display.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DisplayBarcode {

    public final String entryId;
    public final String filialId;
    public final String outletId;
    public final MyArray<DisplayRequest> displayBarcode;

    public DisplayBarcode(String entryId,
                          String filialId,
                          String outletId,
                          MyArray<DisplayRequest> displayBarcode) {
        this.entryId = entryId;
        this.filialId = filialId;
        this.outletId = outletId;
        this.displayBarcode = displayBarcode;
    }

    public static final MyMapper<DisplayBarcode, String> KEY_ADAPTER = new MyMapper<DisplayBarcode, String>() {
        @Override
        public String apply(DisplayBarcode val) {
            return val.outletId;
        }
    };

    public static UzumAdapter<DisplayBarcode> UZUM_ADAPTER = new UzumAdapter<DisplayBarcode>() {
        @Override
        public DisplayBarcode read(UzumReader in) {
            return new DisplayBarcode(in.readString(), in.readString(),
                    in.readString(), in.readArray(DisplayRequest.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DisplayBarcode val) {
            out.write(val.entryId);
            out.write(val.filialId);
            out.write(val.outletId);
            out.write(val.displayBarcode, DisplayRequest.UZUM_ADAPTER);
        }
    };
}
