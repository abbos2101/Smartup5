package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class FilialAgent {

    public final String userId;
    public final String name;

    public FilialAgent(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static final UzumAdapter<FilialAgent> UZUM_ADAPTER = new UzumAdapter<FilialAgent>() {
        @Override
        public FilialAgent read(UzumReader in) {
            return new FilialAgent(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, FilialAgent val) {
            out.write(val.userId);
            out.write(val.name);
        }
    };
}
