package uz.greenwhite.smartup5_trade.m_file_manager.arg;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.FileAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.User;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgAccess extends ArgSession {

    public final String fileId;
    public final MyArray<FileAccess> fileAccesses;
    public final String userId;
    public final MyArray<User> users;

    public ArgAccess(String accountId, String filialId, String fileId, MyArray<FileAccess> fileAccesses, String userId, MyArray<User> users) {
        super(accountId, filialId);
        this.fileId = fileId;
        this.fileAccesses = fileAccesses;
        this.userId = Util.nvl(userId);
        this.users = MyArray.nvl(users);
    }

    public ArgAccess(UzumReader in) {
        super(in);
        this.fileId = in.readString();
        this.fileAccesses = in.readValue(FileAccess.UZUM_ADAPTER.toArray());
        this.userId = in.readString();
        this.users = in.readValue(User.UZUM_ADAPTER.toArray());
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(fileId);
        w.write(fileAccesses, FileAccess.UZUM_ADAPTER);
        w.write(userId);
        w.write(users, User.UZUM_ADAPTER);
    }

    public static final UzumAdapter<ArgAccess> UZUM_ADAPTER = new UzumAdapter<ArgAccess>() {
        @Override
        public ArgAccess read(UzumReader in) {
            return new ArgAccess(in);
        }

        @Override
        public void write(UzumWriter out, ArgAccess val) {
            val.write(out);
        }
    };
}
