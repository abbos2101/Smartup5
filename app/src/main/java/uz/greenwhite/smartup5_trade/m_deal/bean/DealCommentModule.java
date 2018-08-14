package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealCommentModule extends DealModule {

    public final MyArray<String> commentIds;

    public DealCommentModule(MyArray<String> commentIds) {
        super(VisitModule.M_COMMENT);
        this.commentIds = commentIds;
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealCommentModule(in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealCommentModule v = (DealCommentModule) val;
            out.write(v.commentIds, STRING_ARRAY);
        }
    };
}
