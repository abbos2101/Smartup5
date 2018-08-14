package uz.greenwhite.smartup5_trade.m_outlet.bean.file;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;

public class PersonFileDetail {

    public final String fileSha;
    public final String fileName;
    public final String note;
    public final String contentType;

    public PersonFileDetail(String fileSha, String fileName, String note, String contentType) {
        this.fileSha = fileSha;
        this.fileName = fileName;
        this.note = note;
        this.contentType = contentType;
    }

    public int getMimiTypeIcon() {
        if (contentType.contains("image")) {
            return (R.drawable.ic_photo_black_24dp);
        } else if (contentType.contains("text")) {
            return (R.drawable.ic_insert_drive_file_black_24dp);
        } else if (contentType.contains("video")) {
            return (R.drawable.ic_theaters_black_24dp);
        } else if (contentType.contains("application")) {
            return (R.drawable.ic_widgets_black_24dp);
        } else if (contentType.contains("audio")) {
            return (R.drawable.ic_audiotrack_black_24dp);
        } else {
            return (R.drawable.ic_attachment_black_24dp);
        }
    }

    public static final UzumAdapter<PersonFileDetail> UZUM_ADAPTER = new UzumAdapter<PersonFileDetail>() {
        @Override
        public PersonFileDetail read(UzumReader in) {
            return new PersonFileDetail(
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonFileDetail val) {
            out.write(val.fileSha);
            out.write(val.fileName);
            out.write(val.note);
            out.write(val.contentType);
        }
    };
}
