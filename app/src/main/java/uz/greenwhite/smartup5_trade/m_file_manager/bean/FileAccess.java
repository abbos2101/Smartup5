package uz.greenwhite.smartup5_trade.m_file_manager.bean;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class FileAccess {

    public static final String ACCESS_READ = "R";
    public static final String ACCESS_WRITE = "A";
    public static final String ACCESS_EDIT = "E";
    public static final String ACCESS_FULL = "F";

    //  this value indicates for whom the access was created
    public final String forUserId;
    public final String userName;
    public final String accessType;
    //if this value is true, then the access creator is current user
    public final boolean isAccessCreator;

    public FileAccess(String forUserId,
                      String userName,
                      String accessType,
                      boolean isAccessCreator) {
        this.forUserId = forUserId;
        this.userName = userName;
        this.accessType = accessType;
        this.isAccessCreator = isAccessCreator;
    }

    public static final UzumAdapter<FileAccess> UZUM_ADAPTER = new UzumAdapter<FileAccess>() {
        @Override
        public FileAccess read(UzumReader in) {
            return new FileAccess(in.readString(), in.readString(),
                    in.readString(), in.readInt() == 1);
        }

        @Override
        public void write(UzumWriter out, FileAccess val) {
            out.write(val.forUserId);
            out.write(val.userName);
            out.write(val.accessType);
            out.write(val.isAccessCreator);
        }
    };

    public String getAccessName() {
        switch (accessType) {
            case ACCESS_READ:
                return DS.getString(R.string.file_access_read);
            case ACCESS_WRITE:
                return DS.getString(R.string.file_access_add);
            case ACCESS_EDIT:
                return DS.getString(R.string.file_access_edit);
            case ACCESS_FULL:
                return DS.getString(R.string.file_access_full);
        }
        return "";
    }

}
