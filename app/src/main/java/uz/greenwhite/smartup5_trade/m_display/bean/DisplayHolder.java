package uz.greenwhite.smartup5_trade.m_display.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class DisplayHolder {

    public final DisplayBarcode display;
    public final EntryState entryState;

    public DisplayHolder(DisplayBarcode display, EntryState entryState) {
        this.display = display;
        this.entryState = entryState;
    }

    public static final MyMapper<DisplayHolder, String> KEY_ADAPTER = new MyMapper<DisplayHolder, String>() {
        @Override
        public String apply(DisplayHolder displayHolder) {
            return displayHolder.display.entryId;
        }
    };

    public static final UzumAdapter<DisplayHolder> UZUM_ADAPTER = new UzumAdapter<DisplayHolder>() {
        @Override
        public DisplayHolder read(UzumReader in) {
            return new DisplayHolder(in.readValue(DisplayBarcode.UZUM_ADAPTER),
                    in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DisplayHolder val) {
            out.write(val.display, DisplayBarcode.UZUM_ADAPTER);
            out.write(val.entryState, EntryState.UZUM_ADAPTER);
        }
    };
}
