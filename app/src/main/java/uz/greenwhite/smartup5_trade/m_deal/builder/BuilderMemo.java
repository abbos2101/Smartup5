package uz.greenwhite.smartup5_trade.m_deal.builder;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealMemo;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealMemoModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.memo.VDealMemoForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.memo.VDealMemoModule;
import uz.greenwhite.smartup5_trade.m_outlet.bean.Memo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderMemo {

    public final DealRef dealRef;
    public final VisitModule module;
    public final DealMemo initial;

    public BuilderMemo(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private DealMemo getInitial() {
        DealMemoModule dealModule = dealRef.findDealModule(module.id);
        return dealModule != null ? dealModule.memo : null;
    }

    private VDealMemoForm makeForm() {
        PersonMemo memos = dealRef.getOutletMemos();
        String memo = initial != null ? initial.memo : "";
        MyArray<Memo> m = memos.memos.sort(new Comparator<Memo>() {
            @Override
            public int compare(Memo l, Memo r) {
                return r.date.compareTo(l.date);
            }
        });
        return new VDealMemoForm(module, m, memo);
    }

    public VDealMemoModule build() {
        return new VDealMemoModule(module, makeForm());
    }

}

