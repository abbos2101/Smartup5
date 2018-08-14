package uz.greenwhite.smartup5_trade.m_file_manager.arg;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgCreateOrRenameDialog extends ArgSession {


    public static ArgCreateOrRenameDialog newArgCreateFolder(ArgSession arg, String folderId, MyArray<UserFile> items) {
        MyArray<String> fileNames = items.map(new MyMapper<UserFile, String>() {
            @Override
            public String apply(UserFile myFile) {
                return myFile.fileName;
            }
        });
        return new ArgCreateOrRenameDialog(arg, "", folderId, "", fileNames);
    }

    public static ArgCreateOrRenameDialog newArgRenameFile(ArgSession arg, String fileId, String fileName, MyArray<UserFile> items) {
        MyArray<String> fileNames = items.map(new MyMapper<UserFile, String>() {
            @Override
            public String apply(UserFile myFile) {
                return myFile.fileName;
            }
        });
        return new ArgCreateOrRenameDialog(arg, fileId, "", fileName, fileNames);
    }

    public final String fileId;
    public final String folderId;
    public final String fileName;
    public final MyArray<String> folderAndFileName;

    private ArgCreateOrRenameDialog(ArgSession arg,
                                    String fileId,
                                    String folderId,
                                    String fileName,
                                    MyArray<String> folderAndFileName) {
        super(arg.accountId, arg.filialId);
        this.fileId = fileId;
        this.folderId = folderId;
        this.fileName = fileName;
        this.folderAndFileName = folderAndFileName;
    }

    private ArgCreateOrRenameDialog(UzumReader in) {
        super(in);
        this.fileId = in.readString();
        this.folderId = in.readString();
        this.fileName = in.readString();
        this.folderAndFileName = in.readValue(UzumAdapter.STRING_ARRAY);
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.fileId);
        w.write(this.folderId);
        w.write(this.fileName);
        w.write(this.folderAndFileName, UzumAdapter.STRING_ARRAY);
    }

    public static UzumAdapter<ArgCreateOrRenameDialog> UZUM_ADAPTER = new UzumAdapter<ArgCreateOrRenameDialog>() {
        @Override
        public ArgCreateOrRenameDialog read(UzumReader in) {
            return new ArgCreateOrRenameDialog(in);
        }

        @Override
        public void write(UzumWriter out, ArgCreateOrRenameDialog val) {
            val.write(out);
        }
    };
}
