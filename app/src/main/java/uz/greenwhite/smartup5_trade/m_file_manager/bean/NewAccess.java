package uz.greenwhite.smartup5_trade.m_file_manager.bean;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class NewAccess {

    public final String fileId;
    public final MyArray<FileAccess> userAccesses;

    public NewAccess(String fileId, MyArray<FileAccess> userAccesses) {
        this.fileId = fileId;
        this.userAccesses = userAccesses;
    }

    public static final UzumAdapter<NewAccess> UZUM_ADAPTER = new UzumAdapter<NewAccess>() {
        @Override
        public NewAccess read(UzumReader in) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(UzumWriter out, NewAccess val) {
            out.write(val.fileId);
            out.write(val.userAccesses, FileAccess.UZUM_ADAPTER);
        }
    };
}
