package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.agree.DealAgreeModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverload;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.recom.DealRecomModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit.DealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealServiceModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public abstract class DealModule {

    public final int id;

    protected DealModule(int id) {
        this.id = id;
    }

    public static final MyMapper<DealModule, Integer> KEY_ADAPTER = new MyMapper<DealModule, Integer>() {
        @Override
        public Integer apply(DealModule val) {
            return val.id;
        }
    };

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {

        private UzumAdapter<DealModule> getAdapter(int moduleId) {
            switch (moduleId) {
                case VisitModule.M_ORDER:
                    return DealOrderModule.UZUM_ADAPTER;
                case VisitModule.M_PAYMENT:
                    return DealPaymentModule.UZUM_ADAPTER;
                case VisitModule.M_PHOTO:
                    return DealPhotoModule.UZUM_ADAPTER;
                case VisitModule.M_STOCK:
                    return DealStockModule.UZUM_ADAPTER;
                case VisitModule.M_RETURN:
                    return DealReturnModule.UZUM_ADAPTER;
                case VisitModule.M_RETURN_PAYMENT:
                    return DealRPaymentModule.UZUM_ADAPTER;
                case VisitModule.M_GIFT:
                    return DealGiftModule.UZUM_ADAPTER;
                case VisitModule.M_ACTION:
                    return DealActionModule.UZUM_ADAPTER;
                case VisitModule.M_QUIZ:
                    return DealQuizModule.UZUM_ADAPTER;
                case VisitModule.M_MEMO:
                    return DealMemoModule.UZUM_ADAPTER;
                case VisitModule.M_NOTE:
                    return DealNoteModule.UZUM_ADAPTER;
                case VisitModule.M_SERVICE:
                    return DealServiceModule.UZUM_ADAPTER;
                case VisitModule.M_COMMENT:
                    return DealCommentModule.UZUM_ADAPTER;
                case VisitModule.M_RECOM:
                    return DealRecomModule.UZUM_ADAPTER;
                case VisitModule.M_RETAIL_AUDIT:
                    return DealRetailAuditModule.UZUM_ADAPTER;
                case VisitModule.M_AGREE:
                    return DealAgreeModule.UZUM_ADAPTER;
                case VisitModule.M_OVERLOAD:
                    return DealOverloadModule.UZUM_ADAPTER;
                default:
                    throw new AppError("Invalid outlet type module id=" + moduleId);
            }
        }

        @Override
        public DealModule read(UzumReader in) {
            return getAdapter(in.readInt()).read(in);
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            out.write(val.id);
            getAdapter(val.id).write(out, val);
        }
    };

}
