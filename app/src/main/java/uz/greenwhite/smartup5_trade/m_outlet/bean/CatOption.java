package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CatOption {

    public final String optionId;
    public final String name;

    public CatOption(String optionId, String name) {
        this.optionId = optionId;
        this.name = name;
        if (name == null) {
            throw AppError.NullPointer();
        }
    }

    public static final UzumAdapter<CatOption> UZUM_ADAPTER = new UzumAdapter<CatOption>() {
        @Override
        public CatOption read(UzumReader in) {
            return new CatOption(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, CatOption val) {
            out.write(val.optionId);
            out.write(val.name);
        }
    };
}