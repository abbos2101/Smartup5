package uz.greenwhite.smartup5_trade.datasource;// 29.10.2016

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.smartup.anor.bean.user.User;
import uz.greenwhite.smartup.anor.datasource.TapeValue;
import uz.greenwhite.smartup.anor.datasource.persist.Tape;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.m_display.bean.PersonDisplay;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatQuiz;
import uz.greenwhite.smartup5_trade.m_outlet.bean.DoctorLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletRecomProduct;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonTask;
import uz.greenwhite.smartup5_trade.m_outlet.bean.ReturnReason;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SpecialityProduct;
import uz.greenwhite.smartup5_trade.m_outlet.bean.file.PersonFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Comment;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.DoctorHospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialAgent;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialSetting;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.NoteType;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPharm;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonMargin;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonPriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonProductSet;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Producer;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductLastInputPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSet;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSimilar;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.WarehouseBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.WarehousePriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.Dashboard;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductTypePlan;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.CashingRequest;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.PrepaymentPaymentTypes;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;
import uz.greenwhite.smartup5_trade.m_session.bean.person.PersonLastDebt;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizBind;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizRole;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizSet;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditRole;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditSet;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;

public class TapeBase {

    private final Scope scope;
    private final Context context;
    private final String filialId;
    private final ConcurrentHashMap<String, MyArray<TapeValue>> buffer = new ConcurrentHashMap<>();

    public TapeBase(Scope scope) {
        this.scope = scope;
        this.context = scope.context;
        this.filialId = scope.filialId;
    }

    public void init() {
        clear();
        String name = scope.ds.getTapeDatabaseName();
        if (TextUtils.isEmpty(name)) {
            throw new AppError("tape file name is empty");
        }
        FileInputStream in = null;
        try {
            in = context.openFileInput(name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8129);
            String line;
            HashMap<String, ArrayList<TapeValue>> map = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                int index = line.indexOf("\t");
                String filialTypeCode = line.substring(0, index);
                String[] ftc = filialTypeCode.split(" ");

                String filialId = ftc[0];
                String type = ftc[1];
                String code = ftc[2];
                String values = line.substring(index, line.length());

                if (!"0".equals(filialId) && !this.filialId.equals(filialId)) {
                    continue;
                }

                ArrayList<TapeValue> tapeValues = map.get(type);
                if (tapeValues == null) {
                    tapeValues = new ArrayList<>();
                    map.put(type, tapeValues);
                }
                tapeValues.add(new TapeValue(code, values));
            }

            for (Map.Entry<String, ArrayList<TapeValue>> v : map.entrySet()) {
                buffer.put(v.getKey(), MyArray.from(v.getValue()));
            }
        } catch (IOException e) {
            throw new AppError(e);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MyArray<Tape> getAllTapes() {
        ArrayList<Tape> result = new ArrayList<>();
        for (Map.Entry<String, MyArray<TapeValue>> v : buffer.entrySet()) {
            String key = v.getKey();
            for (TapeValue t : v.getValue()) {
                result.add(new Tape(filialId, key, t.tapeCode, t.getString()));
            }
        }
        return MyArray.from(result);
    }

    public void clear() {
        buffer.clear();
    }

    @SuppressWarnings("unchecked")
    private <E> MyArray<E> getTapes(String tapeType, UzumAdapter<E> adapter) {
        if (buffer.isEmpty()) init();
        MyArray<TapeValue> result = buffer.get(tapeType);
        if (result == null) return MyArray.emptyArray();
        ArrayList<E> r = new ArrayList<>();
        for (TapeValue v : result) r.add(v.getValue(adapter));
        return MyArray.from(r);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <E> E getTape(String tapeType, String tapeCode, UzumAdapter<E> adapter) {
        if (buffer.isEmpty()) init();
        MyArray<TapeValue> result = MyArray.nvl(buffer.get(tapeType));
        TapeValue val = result.find(tapeCode, TapeValue.KEY_ADAPTER);
        if (val == null) return null;
        return val.getValue(adapter);
    }

    //----------------------------------------------------------------------------------------------
    public User getUser() {
        return getTape(RT.USER, "0", User.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Filial> getFilials() {
        return getTapes(RT.FILIAL, Filial.UZUM_ADAPTER);
    }

    public FilialSetting getFilialSetting() {
        MyArray<FilialSetting> result = getTapes(RT.FILIAL_SETTING, FilialSetting.UZUM_ADAPTER);
        return result.nonEmpty() ? result.get(0) : FilialSetting.DEFAULT;
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Role> getRoles() {
        return getTapes(RT.ROLE, Role.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Outlet> getOutlets() {
        return getTapes(RT.PERSON, Outlet.UZUM_ADAPTER);
    }

    public MyArray<OutletDoctor> getOutletDoctors() {
        return getTapes(RT.PERSON_DOCTOR, OutletDoctor.UZUM_ADAPTER);
    }

    public MyArray<DoctorHospital> getDoctorHospitals() {
        return getTapes(RT.DOCTOR_HOSPITALS, DoctorHospital.UZUM_ADAPTER);
    }

    public MyArray<OutletPharm> getOutletPharms() {
        return getTapes(RT.PERSON_PHARM, OutletPharm.UZUM_ADAPTER);
    }


    //----------------------------------------------------------------------------------------------
    public MyArray<Room> getRooms() {
        return getTapes(RT.ROOM, Room.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<RoomOutletIds> getRoomOutletIds() {
        return getTapes(RT.ROOM_PERSON_IDS, RoomOutletIds.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<OutletGroup> getOutletGroups() {
        return getTapes(RT.PERSON_GROUP, OutletGroup.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<OutletType> getOutletTypes() {
        return getTapes(RT.PERSON_TYPE, OutletType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Margin> getMargins() {
        return getTapes(RT.MARGIN, Margin.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Product> getProducts() {
        return getTapes(RT.PRODUCT, Product.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Warehouse> getWarehouses() {
        return getTapes(RT.WAREHOUSE, Warehouse.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    //TODO
    public MyArray<ProductPrice> getProductPrices() {
        MyArray<MyArray<ProductPrice>> items = getTapes(RT.PRODUCT_PRICES, ProductPrice.UZUM_ADAPTER.toArray());
        return items.nonEmpty() ? items.get(0) : MyArray.<ProductPrice>emptyArray();
    }

    //----------------------------------------------------------------------------------------------
    //TODO
    public MyArray<ProductBalance> getProductBalances() {
        MyArray<MyArray<ProductBalance>> items = getTapes(RT.PRODUCT_BALANCE, ProductBalance.UZUM_ADAPTER.toArray());
        return items.nonEmpty() ? items.get(0) : MyArray.<ProductBalance>emptyArray();
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PaymentType> getPaymentTypes() {
        return getTapes(RT.PAYMENT_TYPE, PaymentType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PriceType> getPriceTypes() {
        return getTapes(RT.PRICE_TYPE, PriceType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Currency> getCurrencys() {
        return getTapes(RT.CURRENCY, Currency.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PhotoType> getPhotoTypes() {
        return getTapes(RT.PHOTO_TYPE, PhotoType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ProductBarcode> getProductBarcodes() {
        return getTapes(RT.PRODUCT_BARCODE, ProductBarcode.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ProductFile> getProductFiles() {
        return getTapes(RT.PRODUCT_FILE, ProductFile.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ProductPhoto> getProductPhotos() {
        return getTapes(RT.PRODUCT_PHOTO, ProductPhoto.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ProductGroup> getProductGroups() {
        return getTapes(RT.PRODUCT_GROUP, ProductGroup.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ProductType> getProductTypes() {
        return getTapes(RT.PRODUCT_TYPE, ProductType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<SDeal> getSDeals() {
        return getTapes(RT.PERSON_SHIPPED, SDeal.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    //TODO
    public MyArray<OutletPlan> getOutletVisits() {
        MyArray<MyArray<OutletPlan>> r = getTapes(RT.PERSON_VISIT, OutletPlan.UZUM_ADAPTER.toArray());
        return r.nonEmpty() ? r.get(0) : MyArray.<OutletPlan>emptyArray();
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<ReturnReason> getReturnReasons() {
        MyArray<MyArray<ReturnReason>> r = getTapes(RT.RETURN_REASON, ReturnReason.UZUM_ADAPTER.toArray());
        return r.nonEmpty() ? r.get(0) : null;
    }

    //----------------------------------------------------------------------------------------------
    @NonNull
    public MyArray<OutletContract> getOutletContracts() {
        return MyArray.nvl(getTape(RT.PERSON_CONTRACT, "0", OutletContract.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------
    public Setting getSettingWithDefault() {
        Setting setting = getTape(RT.SYSTEM_SETTING, "0", Setting.UZUM_ADAPTER);
        return Setting.withParent(setting, Setting.DEFAULT);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<WarehousePriceType> getWarehousePriceType() {
        return getTapes(RT.WAREHOUSE_PRICE_TYPE, WarehousePriceType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<OutletRecomProduct> getOutletRecomProducts() {
        return getTapes(RT.PERSON_RECOM_PRODUCT, OutletRecomProduct.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PersonAction> getPersonActions() {
        return getTapes(RT.ACTION_V2, PersonAction.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public OutletVisitPlan getOutletVisitPlan(String roomId, String outletId) {
        return getTape(RT.PERSON_VISIT_PLAN, roomId + "-" + outletId, OutletVisitPlan.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public TradeRoleKeys getRoleKeys() {
        return Util.nvl(getTape(RT.ROLE_PREF_CODES, "0", TradeRoleKeys.UZUM_ADAPTER), TradeRoleKeys.DEFAULT);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PriceEditable> getPriceEditables() {
        return getTapes(RT.PRICE_EDITABLE, PriceEditable.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<PersonMemo> getOutletMemos() {
        return getTapes(RT.PERSON_MEMO, PersonMemo.UZUM_ADAPTER);
    }

    public MyArray<NoteType> getNoteTypes() {
        return getTapes(RT.NOTE_TYPE, NoteType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<Quiz> getQuizs() {
        return getTapes(RT.QUIZ, Quiz.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<CatQuiz> getCatQuizes() {
        return getTapes(RT.CAT_QUIZ, CatQuiz.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<QuizRole> getQuizRoles() {
        return getTapes(RT.QUIZ_ROLE, QuizRole.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<QuizSet> getQuizSets() {
        return getTapes(RT.QUIZ_SET, QuizSet.UZUM_ADAPTER);
    }


    public MyArray<QuizBind> getQuizBinds() {
        return getTapes(RT.QUIZ_BIND, QuizBind.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public Dashboard getDashboard() {
        MyArray<Dashboard> r = getTapes(RT.DASHBOARD, Dashboard.UZUM_ADAPTER);
        return r.isEmpty() ? Dashboard.DEFAULT : r.get(0);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<DebtorOutlet> getDebtorOutlets() {
        return getTapes(RT.DEBTOR_PERSONS_V2, DebtorOutlet.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<PersonLastInfo> getPersonLastInfo() {
        return getTapes(RT.PERSON_LAST_INFO, PersonLastInfo.UZUM_ADAPTER);
    }

    public MyArray<PersonDisplay> getPersonDisplay() {
        return getTapes(RT.PERSON_DISPLAY, PersonDisplay.UZUM_ADAPTER);
    }

    public MyArray<PersonProductSet> getPersonProductSets() {
        return getTapes(RT.PERSON_PRODUCT_SET, PersonProductSet.UZUM_ADAPTER);
    }

    public MyArray<ProductSet> getProductSets() {
        return getTapes(RT.PRODUCT_SET, ProductSet.UZUM_ADAPTER);
    }

    public MyArray<PersonMargin> getPersonMargins() {
        return getTapes(RT.PERSON_MARGIN, PersonMargin.UZUM_ADAPTER);
    }

    public MyArray<PersonPriceType> getPersonPriceTypes() {
        return getTapes(RT.PERSON_PRICE_TYPE, PersonPriceType.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<Region> getRegions() {
        return getTapes(RT.REGION, Region.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<Comment> getComments() {
        return getTapes(RT.COMMENT, Comment.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<RoleSetting> getRoleSettings() {
        return getTapes(RT.ROLE_SETTING, RoleSetting.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<PrepaymentPaymentTypes> getPrepaymentPaymentTypes() {
        MyArray<MyArray<PrepaymentPaymentTypes>> r = getTapes(RT.PREPAYMENT_PAYMENT_TYPES, PrepaymentPaymentTypes.UZUM_ADAPTER.toArray());
        return r.isEmpty() ? MyArray.<PrepaymentPaymentTypes>emptyArray() : r.get(0);
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<RoomExpeditor> getRoomExpeditors() {
        return MyArray.nvl(getTape(RT.ROOM_EXPEDITOR, "0", RoomExpeditor.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<FilialAgent> getFilialAgents() {
        return MyArray.nvl(getTape(RT.FILIAL_AGENT, "0", FilialAgent.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<FilialExpeditor> getFilialExpeditors() {
        return MyArray.nvl(getTape(RT.FILIAL_EXPEDITOR, "0", FilialExpeditor.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<MmlPersonTypeProduct> getMmlPersonTypeProducts() {
        return MyArray.nvl(getTape(RT.MML_PERSON_TYPE_PRODUCT, "0", MmlPersonTypeProduct.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<RetailAuditRole> getRetailAuditRoles() {
        return getTapes(RT.RETAIL_AUDIT_ROLE, RetailAuditRole.UZUM_ADAPTER);
    }

    @NonNull
    public MyArray<RetailAuditSet> getRetailAuditSet() {
        return getTapes(RT.RETAIL_AUDIT_SET, RetailAuditSet.UZUM_ADAPTER);
    }

    @NonNull
    public MyArray<RetailAuditProduct> getRetailAuditProducts() {
        return MyArray.nvl(getTape(RT.RETAIL_AUDIT_PRODUCT, "0", RetailAuditProduct.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<SpecialityProduct> getSpecialityProduct() {
        return getTapes(RT.SPECIALITIES_PRODUCTS, SpecialityProduct.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<DoctorLastInfo> getDoctorLastInfo() {
        return getTapes(RT.DOCTOR_LAST_INFO, DoctorLastInfo.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<PersonBalanceReceivable> getPersonBalanceReceivables() {
        return getTapes(RT.PERSON_BALANCE_RECEIVABLE, PersonBalanceReceivable.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<DashboardProductTypePlan> getDashboardProductTypePlans() {
        return MyArray.nvl(getTape(RT.DASHBOARD_PRODUCT_TYPE_PLAN, "0", DashboardProductTypePlan.UZUM_ADAPTER.toArray()));
    }

    @NonNull
    public MyArray<DashboardProductPlan> getDashboardProductPlans() {
        return MyArray.nvl(getTape(RT.DASHBOARD_PRODUCT_ROOM_PLAN, "0", DashboardProductPlan.UZUM_ADAPTER.toArray()));
    }

    @NonNull
    public MyArray<DashboardPlan> getDashboardRoomPlans() {
        return MyArray.nvl(getTape(RT.DASHBOARD_ROOM_PLAN, "0", DashboardPlan.UZUM_ADAPTER.toArray()));
    }


    //----------------------------------------------------------------------------------------------

    public MyArray<CashingRequest> getCashingRequests() {
        return MyArray.nvl(getTape(RT.CASHIN_REQUEST, "0", CashingRequest.UZUM_ADAPTER.toArray()));
    }


    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<PersonFile> getPersonFiles() {
        return getTapes(RT.PERSON_FILES, PersonFile.UZUM_ADAPTER);
    }

    @Nullable
    public PersonFile getPersonFiles(String personId) {
        return getTape(RT.PERSON_FILES, personId, PersonFile.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<Overload> getOverloads() {
        return getTapes(RT.OVERLOAD, Overload.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<PersonTask> getPersonTasks() {
        return MyArray.nvl(getTape(RT.PERSON_TASK, "0", PersonTask.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<WarehouseBalance> getWarehouseBalances() {
        return MyArray.nvl(getTape(RT.PRODUCT_BALANCE_DETAIL, "0", WarehouseBalance.UZUM_ADAPTER.toArray()));
    }

    public MyArray<ProductLastInputPrice> getProductLastInputPrice() {
        return getTapes(RT.PRODUCT_LAST_INPUT_PRICE, ProductLastInputPrice.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<PersonLastDebt> getPersonLastDebts() {
        return MyArray.nvl(getTape(RT.PERSON_LAST_DEBT, "0", PersonLastDebt.UZUM_ADAPTER.toArray()));
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<ProductSimilar> getProductSimilar() {
        return getTapes(RT.PRODUCT_SIMILAR, ProductSimilar.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<Producer> getProducers() {
        return getTapes(RT.PRODUCER, Producer.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------

    public MyArray<MovementIncoming> getMovementIncomings() {
        return getTapes(RT.TO_FILIAL_MOVEMENT, MovementIncoming.UZUM_ADAPTER);
    }

    public MyArray<Violation> getViolations() {
        return getTapes(RT.VIOLATION, Violation.UZUM_ADAPTER);
    }

    //retrieves the info about deals that have already been made and are being waited for delivery
    public MyArray<DealHistory> getDealHistories() {
        return MyArray.nvl(getTape(RT.DEAL_HISTORY, "0", DealHistory.UZUM_ADAPTER.toArray()));
    }

}
