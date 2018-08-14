package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class FilialSetting {

    public final MyArray<String> noteTypeIds;
    public final MyArray<String> quizSetIds;
    public final MyArray<String> serviceIds;
    public final MyArray<String> commentIds;
    public final MyArray<String> retailAuditIds;
    public final MyArray<String> outletIds;
    public final MyArray<String> storePersonIds;

    public FilialSetting(MyArray<String> noteTypeIds,
                         MyArray<String> quizSetIds,
                         MyArray<String> serviceIds,
                         MyArray<String> commentIds,
                         MyArray<String> retailAuditIds,
                         MyArray<String> outletIds,
                         MyArray<String> storePersonIds) {
        this.noteTypeIds = MyArray.nvl(noteTypeIds);
        this.quizSetIds = MyArray.nvl(quizSetIds);
        this.serviceIds = MyArray.nvl(serviceIds);
        this.commentIds = MyArray.nvl(commentIds);
        this.retailAuditIds = MyArray.nvl(retailAuditIds);
        this.outletIds = MyArray.nvl(outletIds);
        this.storePersonIds = MyArray.nvl(storePersonIds);
    }

    public static final FilialSetting DEFAULT = new FilialSetting(null, null, null, null, null, null, null);

    public static final UzumAdapter<FilialSetting> UZUM_ADAPTER = new UzumAdapter<FilialSetting>() {
        @Override
        public FilialSetting read(UzumReader in) {
            return new FilialSetting(in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY),
                    in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, FilialSetting val) {
            out.write(val.noteTypeIds, STRING_ARRAY);
            out.write(val.quizSetIds, STRING_ARRAY);
            out.write(val.serviceIds, STRING_ARRAY);
            out.write(val.commentIds, STRING_ARRAY);
            out.write(val.retailAuditIds, STRING_ARRAY);
            out.write(val.outletIds, STRING_ARRAY);
            out.write(val.storePersonIds, STRING_ARRAY);
        }
    };
}
