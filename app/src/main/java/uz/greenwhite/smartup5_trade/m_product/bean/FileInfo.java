package uz.greenwhite.smartup5_trade.m_product.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;


public class FileInfo {

    public final String fileSha;
    public final String title;
    public final String note;
    public final String contentType;

    public FileInfo(String fileSha, String title, String note, String contentType) {
        this.fileSha = fileSha;
        this.title = title;
        this.note = note;
        this.contentType = contentType;
    }

    public static final MyMapper<FileInfo, String> KEY_ADAPTER = new MyMapper<FileInfo, String>() {
        @Override
        public String apply(FileInfo fileInfo) {
            return fileInfo.fileSha;
        }
    };

    public static final UzumAdapter<FileInfo> UZUM_ADAPTER = new UzumAdapter<FileInfo>() {
        @Override
        public FileInfo read(UzumReader in) {
            return new FileInfo(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, FileInfo val) {
            out.write(val.fileSha);
            out.write(val.title);
            out.write(val.note);
            out.write(val.contentType);
        }
    };
}
