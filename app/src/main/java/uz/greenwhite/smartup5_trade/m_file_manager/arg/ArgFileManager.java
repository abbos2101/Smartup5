package uz.greenwhite.smartup5_trade.m_file_manager.arg;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

import static uz.greenwhite.lib.uzum.UzumAdapter.STRING_ARRAY;

public class ArgFileManager extends ArgSession {

    public static final int EXPLORE = 0;
    public static final int COPY = 1;
    public static final int MOVE = 2;
    public static final int CREATE = 3;
    public static final int RENAME = 4;

    public final String folderId;
    public final String fileId;
    public final String fileName;
    public final boolean isShared;
    public final int state;
    public final MyArray<String> fileIdsToCopyOrMove;


    public ArgFileManager(ArgSession argSession,
                          String folderId,
                          String fileId,
                          String fileName,
                          boolean isShared,
                          int state,
                          MyArray<String> fileIdsToCopyOrMove) {
        super(argSession.accountId, argSession.filialId);
        this.folderId = folderId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.isShared = isShared;
        this.state = state;
        this.fileIdsToCopyOrMove = fileIdsToCopyOrMove;
    }

    public ArgFileManager(ArgSession argSession, String folderId, String fileId, boolean isShared) {
        super(argSession.accountId, argSession.filialId);
        this.folderId = folderId;
        this.fileId = fileId;
        this.fileName = "";
        this.isShared = isShared;
        this.state = ArgFileManager.EXPLORE;
        this.fileIdsToCopyOrMove = MyArray.emptyArray();
    }

    public ArgFileManager(ArgFileManager arg, String fileName, boolean isShared, int state, MyArray<String> fileIdsToCopyOrMove) {
        super(arg.accountId, arg.filialId);
        this.folderId = arg.folderId;
        this.fileId = arg.fileId;
        this.fileName = fileName;
        this.isShared = isShared;
        this.state = state;
        this.fileIdsToCopyOrMove = fileIdsToCopyOrMove;
    }

    public ArgFileManager(UzumReader in) {
        super(in);
        this.folderId = in.readString();
        this.fileId = in.readString();
        this.fileName = in.readString();
        this.isShared = in.readBoolean();
        this.state = in.readInt();
        this.fileIdsToCopyOrMove = in.readValue(STRING_ARRAY);
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(folderId);
        w.write(fileId);
        w.write(fileName);
        w.write(isShared);
        w.write(state);
        w.write(fileIdsToCopyOrMove, STRING_ARRAY);
    }

    public static final UzumAdapter<ArgFileManager> UZUM_ADAPTER = new UzumAdapter<ArgFileManager>() {
        @Override
        public ArgFileManager read(UzumReader in) {
            return new ArgFileManager(in);
        }

        @Override
        public void write(UzumWriter out, ArgFileManager val) {
            val.write(out);
        }
    };

    public boolean isMoveOrCopy() {
        return this.state == COPY || this.state == MOVE;
    }

    public boolean isExplore() {
        return this.state == EXPLORE;
    }

    public int getState() {
        return this.state;
    }

}
