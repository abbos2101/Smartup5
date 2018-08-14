package uz.greenwhite.smartup5_trade.datasource;// 29.10.2016

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletRecomProduct;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Comment;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.NoteType;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.PrepaymentPaymentTypes;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizBind;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizSet;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditRole;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;

public class TapeMate extends TapeBase {

    public TapeMate(Scope scope) {
        super(scope);
    }

    public Filial getFilial(String filialId) {
        return getFilials().find(filialId, Filial.KEY_ADAPTER);
    }

    public Role getRole(String roleId) {
        return getRoles().find(roleId, Role.KEY_ADAPTER);
    }

    public Room getRoom(String roomId) {
        return getRooms().find(roomId, Room.KEY_ADAPTER);
    }

    public RoomOutletIds getRoomOutletIds(String roomId) {
        return getRoomOutletIds().find(roomId, RoomOutletIds.KEY_ADAPTER);
    }

    public Product getProduct(String productId) {
        return getProducts().find(productId, Product.KEY_ADAPTER);
    }

    public Margin getMargin(String marginId) {
        return getMargins().find(marginId, Margin.KEY_ADAPTER);
    }

    public Warehouse getWarehouse(String warehouseId) {
        return getWarehouses().find(warehouseId, Warehouse.KEY_ADAPTER);
    }

    public PaymentType getPaymentTypes(String paymentTypeId) {
        return getPaymentTypes().find(paymentTypeId, PaymentType.KEY_ADAPTER);
    }

    public PriceType getPriceType(String priceTypeId) {
        return getPriceTypes().find(priceTypeId, PriceType.KEY_ADAPTER);
    }

    public Currency getCurrency(String currencyId) {
        return getCurrencys().find(currencyId, Currency.KEY_ADAPTER);
    }

    public PhotoType getPhotoType(String photoTypeId) {
        return getPhotoTypes().find(photoTypeId, PhotoType.KEY_ADAPTER);
    }

    public ProductBarcode getProductBarcode(String productId) {
        return getProductBarcodes().find(productId, ProductBarcode.KEY_ADAPTER);
    }

    public ProductFile getProductFile(String productId) {
        return getProductFiles().find(productId, ProductFile.KEY_ADAPTER);
    }

    public ProductPhoto getProductPhoto(String productId) {
        return getProductPhotos().find(productId, ProductPhoto.KEY_ADAPTER);
    }

    public SDeal getSDeal(String dealId) {
        return getSDeals().find(dealId, SDeal.KEY_ADAPTER);
    }

    public OutletRecomProduct getOutletRecomProduct(String outletId) {
        return getOutletRecomProducts().find(outletId, OutletRecomProduct.KEY_ADAPTER);
    }

    public PriceEditable getPriceEditable(String priceTypeId) {
        return getPriceEditables().find(priceTypeId, PriceEditable.KEY_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public QuizBind getQuizBind(String quizId) {
        return getQuizBinds().find(quizId, QuizBind.KEY_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public PersonMemo getOutletMemo(String outletId) {
        return getOutletMemos().find(outletId, PersonMemo.KEY_ADAPTER);
    }

    public NoteType getNoteType(String noteTypeId) {
        return getNoteTypes().find(noteTypeId, NoteType.KEY_ADAPTER);
    }

    public Quiz getQuiz(String quizId) {
        return getQuizs().find(quizId, Quiz.KEY_ADAPTER);
    }

    public QuizSet getQuizSet(String quizSetId) {
        return getQuizSets().find(quizSetId, QuizSet.KEY_ADAPTER);
    }

    public DebtorOutlet getDebtorOutlet(String outletId) {
        return getDebtorOutlets().find(outletId, DebtorOutlet.KEY_ADAPTER);
    }

    public Comment findComment(String commentId) {
        return getComments().find(commentId, Comment.KEY_ADAPTER);
    }

    @Nullable
    public RoleSetting findRoleSetting(String roleId) {
        return getRoleSettings().find(roleId, RoleSetting.KEY_ADAPTER);
    }

    @NonNull
    public MyArray<PrepaymentPaymentTypes> filterByPaymentKind(@NonNull final String paymentKind) {
        return getPrepaymentPaymentTypes().filter(new MyPredicate<PrepaymentPaymentTypes>() {
            @Override
            public boolean apply(PrepaymentPaymentTypes val) {
                return paymentKind.equals(val.paymentKind);
            }
        });
    }

    @NonNull
    public MyArray<OutletContract> getOutletContracts(final String outletId) {
        return getOutletContracts().filter(new MyPredicate<OutletContract>() {
            @Override
            public boolean apply(OutletContract val) {
                return outletId.equals(val.outletId);
            }
        });
    }

    @NonNull
    public MyArray<RoomExpeditor> getRoomExpeditors(final String roomId) {
        return getRoomExpeditors().filter(new MyPredicate<RoomExpeditor>() {
            @Override
            public boolean apply(RoomExpeditor val) {
                return roomId.equals(val.roomId);
            }
        });
    }


    @NonNull
    public MyArray<RetailAuditRole> getRetailAuditRoles(final MyArray<String> roleIds) {
        return getRetailAuditRoles().filter(new MyPredicate<RetailAuditRole>() {
            @Override
            public boolean apply(RetailAuditRole retailAuditRole) {
                return roleIds.contains(retailAuditRole.roleId, MyMapper.<String>identity());
            }
        });
    }
}
