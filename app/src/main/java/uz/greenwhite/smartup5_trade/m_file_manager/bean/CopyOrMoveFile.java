package uz.greenwhite.smartup5_trade.m_file_manager.bean;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CopyOrMoveFile {

    public final MyArray<String> fileIds;
    public final String toFolderId;

    public CopyOrMoveFile(MyArray<String> fileIds, String toFolderId) {
        this.fileIds = fileIds;
        this.toFolderId = toFolderId;
    }

    public static final UzumAdapter<CopyOrMoveFile> UZUM_ADAPTER = new UzumAdapter<CopyOrMoveFile>() {
        @Override
        public CopyOrMoveFile read(UzumReader in) {
            return new CopyOrMoveFile(in.readArray(STRING), in.readString());
        }

        @Override
        public void write(UzumWriter out, CopyOrMoveFile val) {
            out.write(val.fileIds, STRING_ARRAY);
            out.write(val.toFolderId);
        }
    };
}
