package uz.greenwhite.smartup5_trade.m_deal.builder;

import android.text.TextUtils;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealCommentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.comment.VDealComment;
import uz.greenwhite.smartup5_trade.m_deal.variable.comment.VDealCommentForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.comment.VDealCommentModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Comment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

class BuilderComment {

    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<String> initialComments;

    BuilderComment(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initialComments = getInitial();
    }

    private MyArray<String> getInitial() {
        DealCommentModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.commentIds : MyArray.<String>emptyArray();
    }

    private MyArray<Comment> getComments() {

        MyArray<String> commentIds = initialComments.union(dealRef.filialSetting.commentIds);
        MyArray<Comment> comments = commentIds.map(new MyMapper<String, Comment>() {
            @Override
            public Comment apply(String commentId) {
                return dealRef.findComment(commentId);
            }
        }).filterNotNull().sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment l, Comment r) {
                int compare = CharSequenceUtil.compareToIgnoreCase(l.orderNo, r.orderNo);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.name, r.name);
                }
                return compare;
            }
        });

        if (!TextUtils.isEmpty(dealRef.outlet.personKind)) {
            if (Outlet.K_HOSPITAL.equals(dealRef.outlet.personKind)) {
                comments = comments.filter(new MyPredicate<Comment>() {
                    @Override
                    public boolean apply(Comment comment) {
                        return comment.commentKind.equals(Comment.K_DOCTOR);
                    }
                });
            } else if (Outlet.K_PHARMACY.equals(dealRef.outlet.personKind)) {
                comments = comments.filter(new MyPredicate<Comment>() {
                    @Override
                    public boolean apply(Comment comment) {
                        return comment.commentKind.equals(Comment.K_PHARMACY);
                    }
                });
            } else {
                comments = comments.filter(new MyPredicate<Comment>() {
                    @Override
                    public boolean apply(Comment comment) {
                        return TextUtils.isEmpty(comment.commentKind);
                    }
                });
            }
        } else {
            comments = comments.filter(new MyPredicate<Comment>() {
                @Override
                public boolean apply(Comment comment) {
                    return TextUtils.isEmpty(comment.commentKind);
                }
            });
        }
        return comments;
    }

    public VDealCommentForm makeForm() {
        MyArray<VDealComment> result = getComments().map(new MyMapper<Comment, VDealComment>() {
            @Override
            public VDealComment apply(Comment comment) {
                return new VDealComment(comment, initialComments
                        .contains(comment.commentId, MyMapper.<String>identity()));
            }
        });
        return new VDealCommentForm(module, new ValueArray<>(result));
    }

    public VDealCommentModule build() {
        return new VDealCommentModule(module, makeForm());
    }
}
