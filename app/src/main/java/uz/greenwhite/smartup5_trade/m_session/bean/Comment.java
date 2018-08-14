package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Comment {

    public static final String K_DOCTOR = "Phr:D";
    public static final String K_PHARMACY = "Phr:P";

    public final String commentId, name, orderNo, commentKind;

    public Comment(String commentId, String name, String orderNo, String commentKind) {
        this.commentId = commentId;
        this.name = name;
        this.orderNo = orderNo;
        this.commentKind = commentKind;
    }

    public static final MyMapper<Comment, String> KEY_ADAPTER = new MyMapper<Comment, String>() {
        @Override
        public String apply(Comment comment) {
            return comment.commentId;
        }
    };

    public static final UzumAdapter<Comment> UZUM_ADAPTER = new UzumAdapter<Comment>() {
        @Override
        public Comment read(UzumReader in) {
            return new Comment(in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Comment val) {
            out.write(val.commentId);
            out.write(val.name);
            out.write(val.orderNo);
            out.write(val.commentKind);
        }
    };
}
