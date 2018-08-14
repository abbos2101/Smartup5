package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class FilialExpeditor {

    public final String userId;
    public final String name;

    public FilialExpeditor(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static final UzumAdapter<FilialExpeditor> UZUM_ADAPTER = new UzumAdapter<FilialExpeditor>() {
        @Override
        public FilialExpeditor read(UzumReader in) {
            return new FilialExpeditor(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, FilialExpeditor val) {
            out.write(val.userId);
            out.write(val.name);
        }
    };
}
