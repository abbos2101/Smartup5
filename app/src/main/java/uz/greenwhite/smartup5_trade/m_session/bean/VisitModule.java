package uz.greenwhite.smartup5_trade.m_session.bean;// 30.06.2016


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class VisitModule {

    public static final int M_INFO = 1;                 //--
    public static final int M_ORDER = 2;                //
    public static final int M_PAYMENT = 3;              //--
    public static final int M_ERROR = 4;                //--
    public static final int M_PHOTO = 5;                //
    public static final int M_CONSIGNMENT = 6;          //
    public static final int M_ATTACH = 7;               //--
    public static final int M_STOCK = 8;                //
    public static final int M_RETURN = 9;               //--
    public static final int M_RETURN_PAYMENT = 10;      //--
    public static final int M_REASON = 11;              //--
    public static final int M_GIFT = 12;                //
    public static final int M_ACTION = 13;              //--
    public static final int M_MEMO = 14;                //
    public static final int M_NOTE = 15;                //
    public static final int M_QUIZ = 16;                //
    public static final int M_SERVICE = 17;             //
    public static final int M_COMMENT = 18;             //
    public static final int M_PKO = 19;                 //--
    public static final int M_RECOM = 20;               //--
    public static final int M_RETAIL_AUDIT = 21;        //
    public static final int M_AGREE = 22;               //
    public static final int M_OVERLOAD = 23;            //
    public static final int M_TOTAL = 24;               //
    public static final int M_SOLD_PRODUCT = 25;        //-- Store
    public static final int M_IMEI = 26;                //-- Store


    public final int id;
    public final boolean mandatory;
    public final String name;

    public VisitModule(int id, Boolean mandatory) {
        this.id = id;
        this.mandatory = Util.nvl(mandatory, false);
        this.name = getName();
    }

    private String getName() {
        switch (this.id) {
            case M_INFO:
                return DS.getString(R.string.info);
            case M_ORDER:
                return DS.getString(R.string.order);
            case M_RETURN_PAYMENT:
            case M_PAYMENT:
                return DS.getString(R.string.payment);
            case M_ERROR:
                return DS.getString(R.string.error);
            case M_PHOTO:
                return DS.getString(R.string.photo);
            case M_CONSIGNMENT:
                return DS.getString(R.string.consignment);
            case M_ATTACH:
                return DS.getString(R.string.attach);
            case M_STOCK:
                return DS.getString(R.string.stock);
            case M_RETURN:
                return DS.getString(R.string.deal_products);
            case M_REASON:
                return DS.getString(R.string.reason_for_return);
            case M_GIFT:
                return DS.getString(R.string.gift);
            case M_ACTION:
                return DS.getString(R.string.action);
            case M_MEMO:
                return DS.getString(R.string.memo);
            case M_NOTE:
                return DS.getString(R.string.note);
            case M_QUIZ:
                return DS.getString(R.string.quiz);
            case M_SERVICE:
                return DS.getString(R.string.service);
            case M_COMMENT:
                return DS.getString(R.string.deal_comment);
            case M_PKO:
                return DS.getString(R.string.deal_pko);
            case M_RETAIL_AUDIT:
                return DS.getString(R.string.deal_retail_audit);
            case M_AGREE:
                return DS.getString(R.string.deal_agree);
            case M_TOTAL:
                return DS.getString(R.string.deal_total_module);
            case M_OVERLOAD:
                return DS.getString(R.string.deal_overload);

            default:
                return "";
        }
    }

    public int getIconResId() {
        switch (this.id) {
            case VisitModule.M_ERROR:
                return R.drawable.ic_explicit_black_24dp;
            case VisitModule.M_INFO:
                return R.drawable.visit_1;
            case VisitModule.M_ORDER:
                return R.drawable.visit_5;
            case VisitModule.M_RETURN_PAYMENT:
            case VisitModule.M_PAYMENT:
                return R.drawable.visit_8;
            case VisitModule.M_PHOTO:
                return R.drawable.ic_photo_black_24dp;
            case VisitModule.M_CONSIGNMENT:
                return R.drawable.ic_confirmation_number_black_24dp;
            case VisitModule.M_STOCK:
                return R.drawable.visit_7;
            case VisitModule.M_RETURN:
                return R.drawable.ic_assignment_returned_black_24dp;
            case VisitModule.M_ATTACH:
                return R.drawable.visit_6;
            case VisitModule.M_REASON:
                return R.drawable.ic_receipt_black_24dp;
            case VisitModule.M_GIFT:
                return R.drawable.visit_2;
            case VisitModule.M_ACTION:
                return R.drawable.ic_add_shopping_cart_black_24dp;
            case VisitModule.M_MEMO:
                return R.drawable.visit_3;
            case VisitModule.M_NOTE:
                return R.drawable.visit_4;
            case VisitModule.M_QUIZ:
                return R.drawable.ic_content_paste_black_36dp;
            case VisitModule.M_SERVICE:
                return R.drawable.ic_extension_black_24dp;
            case VisitModule.M_COMMENT:
                return R.drawable.ic_receipt_black_24dp;
            case VisitModule.M_PKO:
                return R.drawable.ic_monetization_on_black_24dp;
            case VisitModule.M_RETAIL_AUDIT:
                return R.drawable.ic_send_black_24dp;
            case VisitModule.M_AGREE:
                return R.drawable.ic_assignment_black_36dp;
            case VisitModule.M_TOTAL:
                return R.drawable.ic_format_list_numbered_black_24dp;
            case VisitModule.M_OVERLOAD:
                return R.mipmap.ic_overload;

            default:
                return 0;
        }
    }

    public static VisitModule makeDefault(int id) {
        return new VisitModule(id, false);
    }

    public static final MyMapper<VisitModule, Integer> KEY_ADAPTER = new MyMapper<VisitModule, Integer>() {
        @Override
        public Integer apply(VisitModule val) {
            return val.id;
        }
    };
}
