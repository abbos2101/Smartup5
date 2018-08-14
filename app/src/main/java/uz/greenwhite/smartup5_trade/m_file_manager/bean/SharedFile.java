package uz.greenwhite.smartup5_trade.m_file_manager.bean;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SharedFile extends UserFile {

    public final String accessType;

    public SharedFile(String level,
                      String folderId,
                      String fileId,
                      String fileName,
                      String sha,
                      String extension,
                      String ownerId,
                      String ownerName,
                      String createdBy,
                      String modifiedBy,
                      String createdOn,
                      String modifiedOn,
                      String filesize,
                      String accessType) {
        super(level, folderId, fileId, fileName, sha, extension, ownerId, ownerName, createdBy, modifiedBy, createdOn, modifiedOn, filesize);
        this.accessType = accessType;
    }

    public static final UzumAdapter<SharedFile> UZUM_ADAPTER = new UzumAdapter<SharedFile>() {
        @Override
        public SharedFile read(UzumReader in) {
            return new SharedFile(in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), Util.nvl(in.readString(), ""),
                    Util.nvl(in.readString(), ""), Util.nvl(in.readString(), ""));
        }

        @Override
        public void write(UzumWriter out, SharedFile val) {
            out.write(val.level);
            out.write(val.folderId);
            out.write(val.fileId);
            out.write(val.fileName);
            out.write(val.sha);
            out.write(val.extension);
            out.write(val.ownerId);
            out.write(val.ownerName);
            out.write(val.createdBy);
            out.write(val.modifiedBy);
            out.write(val.createdOn);
            out.write(val.modifiedOn);
            out.write(val.fileSize);
            out.write(val.accessType);
        }
    };
}
