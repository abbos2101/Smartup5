package uz.greenwhite.smartup5_trade.m_deal.variable;// 30.06.2016

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyFlatMapper;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.common.Balance;
import uz.greenwhite.smartup5_trade.m_deal.common.WP;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.bean.DoctorLastAgree;
import uz.greenwhite.smartup5_trade.m_outlet.bean.DoctorLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletRecomProduct;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonMemo;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SpecialityProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Comment;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialAgent;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialSetting;
import uz.greenwhite.smartup5_trade.m_session.bean.Margin;
import uz.greenwhite.smartup5_trade.m_session.bean.MmlPersonTypeProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.NoteType;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonGroupType;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonPriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.PersonProductSet;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Producer;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSet;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSimilar;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.WarehousePriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizBind;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizRole;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizSet;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditRole;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditSet;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;

public class DealRef {

    private final Scope scope;
    public final String accountId;
    public final String filialId;
    public final String createdBy;
    public final DealHolder dealHolder;
    public final Filial filial;
    public final FilialSetting filialSetting;
    public final Room room;
    public final Outlet outlet;
    public final Setting setting;

    public final boolean isVanseller;

    private final MyArray<Product> products;

    public final Balance balance;

    public final MyArray<Violation> allViolations;
    public final MyArray<Ban> violationBans;
    public BigDecimal lastDealBalance = BigDecimal.ZERO;

    public DealRef(Scope scope, DealHolder dealHolder) {
        Deal d = dealHolder.deal;
        MyArray<Outlet> roomOutlets = DSUtil.getRoomOutlets(scope, d.roomId);

        this.scope = scope;
        this.accountId = this.scope.accountId;
        this.filialId = d.filialId;
        this.createdBy = AdminApi.getAccount(this.accountId).uc.userId;
        this.dealHolder = dealHolder;
        this.filial = this.scope.ref.getFilial(d.filialId);
        this.filialSetting = this.scope.ref.getFilialSetting();
        this.room = this.scope.ref.getRoom(d.roomId);
        this.outlet = roomOutlets.find(d.outletId, Outlet.KEY_ADAPTER);
        this.setting = this.scope.ref.getSettingWithDefault();

        if (filial == null) throw AppError.NullPointer();
        if (room == null) throw AppError.NullPointer();
        if (outlet == null) throw AppError.NullPointer();

        this.products = this.scope.ref.getProducts();

        this.balance = new Balance(this);

        this.allViolations = OutletApi.getPersonViolations(scope, outlet, room.id);
        this.violationBans = OutletApi.prepareViolationBans(this.allViolations);
        this.lastDealBalance = getPersonBalance();

        this.isVanseller = Utils.isRole(scope, scope.ref.getRoleKeys().vanseller);
    }

    public MyArray<ProductSimilar> getProductSimilars() {
        return scope.ref.getProductSimilar().filter(new MyPredicate<ProductSimilar>() {
            @Override
            public boolean apply(ProductSimilar productSimilar) {
                return productSimilar.similarIds.nonEmpty();
            }
        });
    }

    public BigDecimal getPersonBalance() {
        PersonBalanceReceivable personBalanceReceivable = scope.ref.getPersonBalanceReceivables().find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER);
        BigDecimal balance = personBalanceReceivable != null ? personBalanceReceivable.amount : BigDecimal.ZERO;
        return DSUtil.getAllDeals(scope).reduce(balance, new MyReducer<BigDecimal, DealHolder>() {
            @Override
            public BigDecimal apply(BigDecimal balance, DealHolder deal) {
                if (deal.deal.outletId.equals(outlet.id) && (deal.entryState.isReady() || deal.entryState.isLocked())) {
                    DealPaymentModule paymentModule = (DealPaymentModule) deal.deal.modules.find(VisitModule.M_PAYMENT, DealModule.KEY_ADAPTER);
                    if (paymentModule == null || paymentModule.payments.isEmpty()) return balance;
                    DealPayment payment = paymentModule.payments.get(0);
                    Currency currency = scope.ref.getCurrency(payment.currencyId);
                    if (currency != null)
                        balance = balance.subtract(payment.value.multiply(Util.nvl(currency.price, BigDecimal.ZERO)));
                }
                return balance;
            }
        });

    }

    public MyArray<WP> makeWP() {
        final MyArray<String> roomWarehouseIds = room.warehouseIds;
        MyArray<String> priceTypeIds = getPriceTypeIds();

        MyArray<WarehousePriceType> result = getWarehousePriceType().filter(new MyPredicate<WarehousePriceType>() {
            @Override
            public boolean apply(WarehousePriceType val) {
                return roomWarehouseIds.contains(val.warehouseId, MyMapper.<String>identity())
                        && val.priceTypeIds.nonEmpty();
            }
        });

        Set<WP> wps = new HashSet<>();
        if (result.nonEmpty()) {
            for (WarehousePriceType r : result) {
                for (String priceTypeId : Utils.intersect(r.priceTypeIds, priceTypeIds)) {
                    wps.add(new WP(r.warehouseId, priceTypeId));
                }
            }
        }

        for (String w : roomWarehouseIds) {
            if (!result.contains(w, WarehousePriceType.KEY_ADAPTER)) {
                for (String p : priceTypeIds) {
                    wps.add(new WP(w, p));
                }
            }
        }
        return MyArray.from(wps);
    }

    public boolean isRole(String... pCodes) {
        return Utils.isRole(scope, pCodes);
    }

    public boolean isRole(Integer... roleIds) {
        return Utils.isRole(scope, roleIds);
    }

    public TradeRoleKeys getRoleKeys() {
        return scope.ref.getRoleKeys();
    }

    public Product findProduct(final String productId) {
        return products.find(productId, Product.KEY_ADAPTER);
    }

    public Producer getProducer(String producerId) {
        return scope.ref.getProducers().find(producerId, Producer.KEY_ADAPTER);
    }

    public Region getRegion(String regionId) {
        return scope.ref.getRegions().find(regionId, Region.KEY_ADAPTER);
    }

    public MyArray<String> getFilialProductIds() {
        return scope.ref.getFilial(filialId).productIds;
    }

    public MyArray<VisitModule> getModuleIds() {
        return scope.ref.getFilial(filialId).
                getVisitModules(outlet.personKind);
    }

    @SuppressWarnings("unchecked")
    public <T extends DealModule> T findDealModule(int moduleId) {
        return (T) dealHolder.deal.modules.find(moduleId, DealModule.KEY_ADAPTER);
    }

    public PhotoType getPhotoType(String photoTypeId) {
        return scope.ref.getPhotoType(photoTypeId);
    }

    public MyArray<String> getPriceTypeIds() {
        PersonPriceType priceType = scope.ref.getPersonPriceTypes()
                .find(outlet.id, PersonPriceType.KEY_ADAPTER);

        if (priceType == null || priceType.priceTypeIds.isEmpty()) {
            return room.priceTypeIds;
        }

        if (room.priceTypeIds.isEmpty()) return priceType.priceTypeIds;

        return Utils.intersect(priceType.priceTypeIds, room.priceTypeIds);
    }

    @Nullable
    private MyArray<String> getProductSetIds(String productSetId) {
        if (!TextUtils.isEmpty(productSetId)) {
            MyArray<ProductSet> productSets = scope.ref.getProductSets();
            ProductSet productSet = productSets.find(productSetId, ProductSet.KEY_ADAPTER);
            if (productSet != null) {
                return productSet.productId;
            }
            return MyArray.emptyArray();
        }
        return null;
    }

    private MyArray<String> intersectProductIds(MyArray<String> idsRoom, MyArray<String> idsPerson) {
        if (idsRoom != null && idsRoom.isEmpty() ||
                idsPerson != null && idsPerson.isEmpty()) {
            return MyArray.emptyArray();
        }

        MyArray<String> pIds;
        if (idsRoom == null && idsPerson == null) {
            pIds = filial.productIds;
        } else if (idsRoom == null) {
            pIds = Utils.intersect(idsPerson, filial.productIds);
        } else if (idsPerson == null) {
            pIds = Utils.intersect(idsRoom, filial.productIds);
        } else {
            pIds = Utils.intersect(Utils.intersect(idsRoom, idsPerson), filial.productIds);
        }
        return Utils.intersect(pIds, room.productIds);
    }

    public MyArray<String> getProductIds() {
        MyArray<String> idsRoom = getProductSetIds(room.psOrder);
        MyArray<String> idsPerson = null;

        MyArray<PersonProductSet> personProductSets = scope.ref.getPersonProductSets();
        PersonProductSet personProductSet = personProductSets.find(outlet.id, PersonProductSet.KEY_ADAPTER);
        if (personProductSet != null) {
            idsPerson = getProductSetIds(personProductSet.psOrder);
        }
        return intersectProductIds(idsRoom, idsPerson);
    }

    public MyArray<String> getGiftProductIds() {
        MyArray<String> idsRoom = getProductSetIds(room.psGift);
        MyArray<String> idsPerson = null;

        MyArray<PersonProductSet> personProductSets = scope.ref.getPersonProductSets();
        PersonProductSet personProductSet = personProductSets.find(outlet.id, PersonProductSet.KEY_ADAPTER);
        if (personProductSet != null) {
            idsPerson = getProductSetIds(personProductSet.psGift);
        }
        return intersectProductIds(idsRoom, idsPerson);
    }

    public MyArray<String> getStockProductIds() {
        MyArray<String> idsRoom = getProductSetIds(room.psStock);
        MyArray<String> idsPerson = null;

        MyArray<PersonProductSet> personProductSets = scope.ref.getPersonProductSets();
        PersonProductSet personProductSet = personProductSets.find(outlet.id, PersonProductSet.KEY_ADAPTER);
        if (personProductSet != null) {
            idsPerson = getProductSetIds(personProductSet.psStock);
        }
        return intersectProductIds(idsRoom, idsPerson);
    }

    public MyArray<ProductPrice> getProductPrices() {
        return scope.ref.getProductPrices();
    }

    public MyArray<ProductBalance> getProductBalances() {
        return scope.ref.getProductBalances();
    }

    public MyArray<ProductGroup> getProductGroups() {
        return scope.ref.getProductGroups();
    }

    public MyArray<ProductType> getProductTypes() {
        return scope.ref.getProductTypes();
    }

    public Warehouse getWarehouse(String warehouseId) {
        return scope.ref.getWarehouse(warehouseId);
    }

    public PriceType getPriceType(String priceTypeId) {
        return scope.ref.getPriceType(priceTypeId);
    }

    public PaymentType getPaymentType(String paymentTypeId) {
        return scope.ref.getPaymentTypes(paymentTypeId);
    }

    public RoundModel getRoundModel() {
        return scope.ref.getFilial(filialId).roundModel;
    }

    public MyArray<Margin> getMargins() {
        return DSUtil.getOutletMargin(scope, room.id, outlet.id);
    }

    public Currency getCurrency(String currencyId) {
        return scope.ref.getCurrency(currencyId);
    }

    public MyArray<DealHolder> getAllReadyDeals() {
        return DSUtil.getAllDeals(scope).filter(new MyPredicate<DealHolder>() {
            @Override
            public boolean apply(DealHolder val) {
                return val.entryState.isReady() || val.entryState.isLocked();
            }
        });
    }

    public MyArray<PhotoType> getPhotoType() {
        return scope.ref.getPhotoTypes();
    }

    public MyArray<ProductPhoto> getProductPhotos() {
        return scope.ref.getProductPhotos();
    }

    public MyArray<ProductFile> getProductFiles() {
        return scope.ref.getProductFiles();
    }

    public MyArray<ProductBarcode> getProductBarcode() {
        return scope.ref.getProductBarcodes();
    }

    public ProductBarcode getProductBarcode(String productId) {
        return scope.ref.getProductBarcode(productId);
    }

    @NonNull
    public MyArray<OutletContract> getOutletContracts() {
        return scope.ref.getOutletContracts(outlet.id);
    }

    @NonNull
    public MyArray<RoomExpeditor> getExpeditor() {
        return scope.ref.getRoomExpeditors(room.id);
    }

    @NonNull
    public MyArray<FilialAgent> getFilialAgents() {
        return scope.ref.getFilialAgents();
    }

    @NonNull
    public MyArray<FilialExpeditor> getFilialExpeditors() {
        return scope.ref.getFilialExpeditors();
    }

    public MyArray<WarehousePriceType> getWarehousePriceType() {
        return scope.ref.getWarehousePriceType();
    }

    public OutletRecomProduct getOutletRecomProduct() {
        return scope.ref.getOutletRecomProduct(outlet.id);
    }

    public MyArray<MmlPersonTypeProduct> getMmlPersonTypeProducts() {
        final Set<String> personTypeIds = outlet.groupValues.map(new MyMapper<OutletGroupValue, String>() {
            @Override
            public String apply(OutletGroupValue outletGroupValue) {
                return outletGroupValue.typeId;
            }
        }).asSet();
        return scope.ref.getMmlPersonTypeProducts().filter(new MyPredicate<MmlPersonTypeProduct>() {
            @Override
            public boolean apply(MmlPersonTypeProduct mmlPersonTypeProduct) {
                return personTypeIds.contains(mmlPersonTypeProduct.personTypeId);
            }
        });
    }

    public MyArray<PersonAction> getPersonActions() {
        return scope.ref.getPersonActions().filter(new MyPredicate<PersonAction>() {
            @Override
            public boolean apply(PersonAction action) {
                return action.groupTypes.isEmpty() ||
                        action.groupTypes.contains(new MyPredicate<PersonGroupType>() {
                            @Override
                            public boolean apply(final PersonGroupType personGroupType) {
                                return outlet.groupValues.contains(new MyPredicate<OutletGroupValue>() {
                                    @Override
                                    public boolean apply(OutletGroupValue outletGroupValue) {
                                        return personGroupType.groupId.equals(outletGroupValue.groupId) && personGroupType.typeId.equals(outletGroupValue.typeId);
                                    }
                                });
                            }
                        });

            }
        });
    }

    public MyArray<Overload> getOverloads() {
        return scope.ref.getOverloads().filter(new MyPredicate<Overload>() {
            @Override
            public boolean apply(Overload val) {
                return val.groupTypes.isEmpty() ||
                        val.groupTypes.contains(new MyPredicate<PersonGroupType>() {
                            @Override
                            public boolean apply(final PersonGroupType personGroupType) {
                                return outlet.groupValues.contains(new MyPredicate<OutletGroupValue>() {
                                    @Override
                                    public boolean apply(OutletGroupValue outletGroupValue) {
                                        return personGroupType.groupId.equals(outletGroupValue.groupId) && personGroupType.typeId.equals(outletGroupValue.typeId);
                                    }
                                });
                            }
                        });

            }
        });
    }


    public PriceEditable getPriceEditable(String priceTypeId) {
        return scope.ref.getPriceEditable(priceTypeId);
    }

    public PersonMemo getOutletMemos() {
        PersonMemo memo = scope.ref.getOutletMemo(outlet.id);
        if (memo == null) {
            return PersonMemo.makeDefault(outlet.id);
        }
        return memo;
    }

    public NoteType getNoteType(String noteTypeId) {
        return scope.ref.getNoteType(noteTypeId);
    }

    public Quiz getQuiz(String quizId) {
        return scope.ref.getQuiz(quizId);
    }

    public QuizBind getQuizBind(String quizId) {
        return scope.ref.getQuizBind(quizId);
    }

    public QuizSet getQuizSet(String quizSetId) {
        return scope.ref.getQuizSet(quizSetId);
    }

    public MyArray<String> getFilialRoleQuizSetIds() {
        return scope.ref.getQuizRoles()
                .filter(new MyPredicate<QuizRole>() {
                    @Override
                    public boolean apply(QuizRole quizRole) {
                        return filial.roleIds.contains(quizRole.roleId, MyMapper.<String>identity());
                    }
                })
                .flatMap(new MyFlatMapper<QuizRole, String>() {
                    @Override
                    public MyArray<String> apply(QuizRole element) {
                        return element.quizSetIds;
                    }
                })
                .filter(new MyPredicate<String>() {
                    @Override
                    public boolean apply(String quizSetId) {
                        return filialSetting.quizSetIds.contains(quizSetId, MyMapper.<String>identity());
                    }
                });
    }

    public RetailAuditProduct getRetailAuditProduct(String productId) {
        return scope.ref.getRetailAuditProducts().find(productId, RetailAuditProduct.KEY_ADAPTER);
    }

    public RetailAuditSet getRetailAuditSet(String retailAuditId) {
        return scope.ref.getRetailAuditSet().find(retailAuditId, RetailAuditSet.KEY_ADAPTER);
    }

    public MyArray<String> getFilialRoleRetailAuditSetIds() {
        return scope.ref.getRetailAuditRoles(filial.roleIds)
                .flatMap(new MyFlatMapper<RetailAuditRole, String>() {
                    @Override
                    public MyArray<String> apply(RetailAuditRole element) {
                        return element.retailAuditSetIds;
                    }
                })
                .filter(new MyPredicate<String>() {
                    @Override
                    public boolean apply(String retailAuditId) {
                        return filialSetting.retailAuditIds.contains(retailAuditId, MyMapper.<String>identity());
                    }
                });
    }

    public Comment findComment(String commentId) {
        return scope.ref.findComment(commentId);
    }

    public SpecialityProduct getSpecialityProduct(String specialityId) {
        return scope.ref.getSpecialityProduct().find(specialityId, SpecialityProduct.KEY_ADAPTER);
    }

    public MyArray<DoctorLastAgree> getDoctorLastAgrees() {
        DoctorLastInfo doctorLastInfo = scope.ref.getDoctorLastInfo().find(outlet.id, DoctorLastInfo.KEY_ADAPTER);
        if (doctorLastInfo == null) return MyArray.emptyArray();
        return doctorLastInfo.doctorLastAgrees;
    }
}
