package uz.greenwhite.smartup5_trade.m_display.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonDisplay {

    public final String personId;
    public final MyArray<Display> displays;

    public PersonDisplay(String personId,
                         MyArray<Display> displays) {
        this.personId = personId;
        this.displays = MyArray.nvl(displays);
    }

    public static final MyMapper<PersonDisplay, String> KEY_ADAPTER = new MyMapper<PersonDisplay, String>() {
        @Override
        public String apply(PersonDisplay val) {
            return val.personId;
        }
    };

    public static final UzumAdapter<PersonDisplay> UZUM_ADAPTER = new UzumAdapter<PersonDisplay>() {

        @Override
        public PersonDisplay read(UzumReader in) {
            return new PersonDisplay(in.readString(), in.readArray(Display.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonDisplay val) {
            out.write(val.personId);
            out.write(val.displays, Display.UZUM_ADAPTER);
        }
    };
}
