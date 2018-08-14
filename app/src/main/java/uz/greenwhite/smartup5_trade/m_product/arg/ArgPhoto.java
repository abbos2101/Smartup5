package uz.greenwhite.smartup5_trade.m_product.arg;// 16.08.2016

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgPhoto extends ArgSession {

    public final String sha;
    public final boolean disk;

    public ArgPhoto(String accountId, String filialId, String sha, Boolean disk) {
        super(accountId, filialId);
        this.sha = sha;
        this.disk = Util.nvl(disk, false);
    }

    public ArgPhoto(String accountId, String filialId, String sha) {
        this(accountId, filialId, sha, false);
    }

    public static final UzumAdapter<ArgPhoto> UZUM_ADAPTER = new UzumAdapter<ArgPhoto>() {
        @Override
        public ArgPhoto read(UzumReader in) {
            return new ArgPhoto(in.readString(), in.readString(), in.readString(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, ArgPhoto val) {
            out.write(val.accountId);
            out.write(val.filialId);
            out.write(val.sha);
            out.write(val.disk);
        }
    };
}
