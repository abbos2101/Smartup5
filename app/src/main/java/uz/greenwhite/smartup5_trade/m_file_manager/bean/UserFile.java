package uz.greenwhite.smartup5_trade.m_file_manager.bean;


import android.text.TextUtils;

import java.text.DecimalFormat;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;

public class UserFile {

    public final String level;
    public final String folderId;
    public final String fileId;
    public final String fileName;
    public final String sha;
    public final String extension;
    public final String ownerId;
    public final String ownerName;
    public final String createdBy;
    public final String modifiedBy;
    public final String createdOn;
    public final String modifiedOn;
    public final String fileSize;


    public UserFile(String level, String folderId,
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
                    String fileSize) {
        this.folderId = folderId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.modifiedOn = modifiedOn;
        this.extension = extension;
        this.sha = sha;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
        this.createdOn = createdOn;
        this.fileSize = Util.nvl(fileSize, "");
        this.level = level;
    }

    public CharSequence getFileInfo(String folderName) {
        float sizeB = Float.parseFloat(fileSize);
        float sizeMB = sizeB / (1024f * 1024f);
        DecimalFormat format = new DecimalFormat("#.##");
        String sizeFinal = format.format(sizeMB);
        return UI.html().b().v("Название: ").b().v(fileName)
                .br().b().v("Размер: ").b().v(String.valueOf(sizeFinal)).v(" МБ")
                .br().b().v("В папке: ").b().v(folderName)
                .br().b().v("Создан: ").b().v(createdOn + " | " + ownerName)
                .br().b().v("Изменен: ").b().v(modifiedOn)
                .html();
    }

    public boolean isFolder() {
        return TextUtils.isEmpty(sha);
    }

    public int getIconResId(boolean isShared) {
        if (isFolder()) {
            return isShared ? R.drawable.ic_folder_shared_black_24dp : R.drawable.ic_folder_black_24dp;
        } else {
            if (extension.contains("image")) {
                return (R.drawable.ic_photo_black_24dp);
            } else if (extension.contains("text")) {
                return (R.drawable.ic_insert_drive_file_black_24dp);
            } else if (extension.contains("video")) {
                return (R.drawable.ic_theaters_black_24dp);
            } else if (extension.contains("application")) {
                return (R.drawable.ic_widgets_black_24dp);
            } else if (extension.contains("audio")) {
                return (R.drawable.ic_audiotrack_black_24dp);
            } else {
                return (R.drawable.ic_attachment_black_24dp);
            }
        }
    }

    public static final UzumAdapter<UserFile> UZUM_ADAPTER = new UzumAdapter<UserFile>() {
        @Override
        public UserFile read(UzumReader r) {
            return new UserFile(
                    r.readString(), r.readString(),
                    r.readString(), r.readString(),
                    r.readString(), r.readString(),
                    r.readString(), r.readString(),
                    r.readString(), r.readString(),
                    r.readString(), r.readString(),
                    Util.nvl(r.readString(), ""));
        }

        @Override
        public void write(UzumWriter out, UserFile val) {
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
        }
    };
}
