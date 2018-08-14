package uz.greenwhite.smartup5_trade.datasource;// 29.10.2016

import android.graphics.Bitmap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.smartup.anor.datasource.EntryValue;
import uz.greenwhite.smartup.anor.datasource.persist.DatabaseMate;
import uz.greenwhite.smartup.anor.datasource.persist.Entry;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayBarcode;
import uz.greenwhite.smartup5_trade.m_incoming.bean.Incoming;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingPost;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResult;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.categorization.CatHolder;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.PhotoConfig;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.Stocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;

public class EntryBase {

    private final DatabaseMate db;
    private final String filialId;

    public EntryBase(DatabaseMate db, String filialId) {
        this.db = db;
        this.filialId = filialId;
    }

    private <E> EntryValue<E> createEntryValue(Entry entry, final UzumAdapter<E> adapter) {
        if (entry == null) {
            return null;
        }
        return new EntryValue<E>(entry.entryId, entry.refType, entry.state, entry.serverResult, Uzum.toValue(entry.val, adapter));
    }

    private <E> EntryValue<E> loadEntry(String entryId, final UzumAdapter<E> adapter) {
        return createEntryValue(db.entryLoadOne(entryId), adapter);
    }

    private <E> MyArray<EntryValue<E>> loadEntryAll(String refType, final UzumAdapter<E> adapter) {
        return db.entryLoadAll(filialId, refType).map(new MyMapper<Entry, EntryValue<E>>() {
            @Override
            public EntryValue<E> apply(Entry entry) {
                return createEntryValue(entry, adapter);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<OutletPlan> getOutletVisits() {
        return loadEntryAll(RT.VISIT_PERSON, OutletPlan.UZUM_ADAPTER).map(new MyMapper<EntryValue<OutletPlan>, OutletPlan>() {
            @Override
            public OutletPlan apply(EntryValue<OutletPlan> val) {
                return val.value;
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public OutletVisitPlan getOutletVisitPlan(String filialId, String roomId, String outletId) {
        return loadEntryAll(RT.VISIT_PLAN, OutletVisitPlan.UZUM_ADAPTER)
                .map(new MyMapper<EntryValue<OutletVisitPlan>, OutletVisitPlan>() {
                    @Override
                    public OutletVisitPlan apply(EntryValue<OutletVisitPlan> val) {
                        return val.value;
                    }
                })
                .find(OutletVisitPlan.getKey(filialId, roomId, outletId), OutletVisitPlan.KEY_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<SDealHolder> getSDealHolders() {
        return loadEntryAll(RT.SHIPPED, SDeal.UZUM_ADAPTER).map(new MyMapper<EntryValue<SDeal>, SDealHolder>() {
            @Override
            public SDealHolder apply(EntryValue<SDeal> val) {
                return new SDealHolder(val.entryId, val.value, val.getEntryState());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<DebtorHolder> getDebtorHolder() {
        return loadEntryAll(RT.PERSON_DEBTOR, Debtor.UZUM_ADAPTER).map(new MyMapper<EntryValue<Debtor>, DebtorHolder>() {
            @Override
            public DebtorHolder apply(EntryValue<Debtor> val) {
                return new DebtorHolder(val.entryId, val.value, val.getEntryState());
            }
        });
    }

    public SDealHolder getSDealHolder(String entryId) {
        EntryValue<SDeal> val = loadEntry(entryId, SDeal.UZUM_ADAPTER);
        return new SDealHolder(val.entryId, val.value, val.getEntryState());
    }

    //----------------------------------------------------------------------------------------------
    public DealHolder getDeal(String entryId) {
        EntryValue<Deal> val = loadEntry(entryId, Deal.UZUM_ADAPTER);
        if (val == null) return null;
        return new DealHolder(val.value, val.getEntryState());
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<MovementIncomingHolder> getAllMovementIncoming() {
        return loadEntryAll(RT.SAVE_POST_FILIAL_MOVEMENT, MovementIncomingPost.UZUM_ADAPTER).map(new MyMapper<EntryValue<MovementIncomingPost>, MovementIncomingHolder>() {
            @Override
            public MovementIncomingHolder apply(EntryValue<MovementIncomingPost> val) {
                return new MovementIncomingHolder(val.value, val.getEntryState());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public IncomingHolder getIncoming(String entryId) {
        EntryValue<Incoming> val = loadEntry(entryId, Incoming.UZUM_ADAPTER);
        if (val == null) return null;
        return new IncomingHolder(val.value, val.getEntryState());
    }

    public MyArray<IncomingHolder> getAllIncoming() {
        return loadEntryAll(RT.SAVE_INCOMING, Incoming.UZUM_ADAPTER).map(new MyMapper<EntryValue<Incoming>, IncomingHolder>() {
            @Override
            public IncomingHolder apply(EntryValue<Incoming> val) {
                return new IncomingHolder(val.value, val.getEntryState());
            }
        });
    }

    public StocktakingHolder getStocktaking(String entryId) {
        EntryValue<Stocktaking> val = loadEntry(entryId, Stocktaking.UZUM_ADAPTER);
        if (val == null) return null;
        return new StocktakingHolder(val.value, val.getEntryState());
    }

    public MyArray<StocktakingHolder> getAllStocktaking() {
        return loadEntryAll(RT.SAVE_STOCKTAKING, Stocktaking.UZUM_ADAPTER).map(new MyMapper<EntryValue<Stocktaking>, StocktakingHolder>() {
            @Override
            public StocktakingHolder apply(EntryValue<Stocktaking> val) {
                return new StocktakingHolder(val.value, val.getEntryState());
            }
        });
    }

    public MyArray<DealHolder> getOrderDeals() {
        return loadEntryAll(RT.DEAL_ORDER, Deal.UZUM_ADAPTER).map(new MyMapper<EntryValue<Deal>, DealHolder>() {
            @Override
            public DealHolder apply(EntryValue<Deal> val) {
                return new DealHolder(val.value, val.getEntryState());
            }
        });
    }

    public MyArray<DealHolder> getReturnDeals() {
        return loadEntryAll(RT.DEAL_RETURN, Deal.UZUM_ADAPTER).map(new MyMapper<EntryValue<Deal>, DealHolder>() {
            @Override
            public DealHolder apply(EntryValue<Deal> val) {
                return new DealHolder(val.value, val.getEntryState());
            }
        });
    }

    public MyArray<DealHolder> getExtraordinaryDeals() {
        return loadEntryAll(RT.DEAL_EXTRAORDINARY, Deal.UZUM_ADAPTER).map(new MyMapper<EntryValue<Deal>, DealHolder>() {
            @Override
            public DealHolder apply(EntryValue<Deal> val) {
                return new DealHolder(val.value, val.getEntryState());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<EntryValue<OutletLocation>> getOutletLocations() {
        return loadEntryAll(RT.PERSON_LOCATION, OutletLocation.UZUM_ADAPTER);
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<EntryValue<DisplayBarcode>> getOutletDisplayBarcodes() {
        return loadEntryAll(RT.DISPLAY_BARCODE, DisplayBarcode.UZUM_ADAPTER);
    }

    public EntryValue<DisplayBarcode> getOutletDisplayBarcode(final String outletId) {
        return getOutletDisplayBarcodes().findFirst(new MyPredicate<EntryValue<DisplayBarcode>>() {
            @Override
            public boolean apply(EntryValue<DisplayBarcode> val) {
                return val.value.outletId.equals(outletId);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public MyArray<CatHolder> getCatHolders() {
        return loadEntryAll(RT.SAVE_CATEGORIZATION, CatResult.UZUM_ADAPTER).map(new MyMapper<EntryValue<CatResult>, CatHolder>() {
            @Override
            public CatHolder apply(EntryValue<CatResult> val) {
                return new CatHolder(val.entryId, val.value, val.getEntryState());
            }
        });
    }

    public MyArray<EntryValue<CatResult>> getOutletCatQuizes() {
        return loadEntryAll(RT.SAVE_CATEGORIZATION, CatResult.UZUM_ADAPTER);
    }

    public EntryValue<CatResult> getOutletCatQuiz(final String outletId) {
        return getOutletCatQuizes().findFirst(new MyPredicate<EntryValue<CatResult>>() {
            @Override
            public boolean apply(EntryValue<CatResult> o) {
                return o.value.outletId.equals(outletId);
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private <E> void saveEntry(String entryId, String refType, E val, UzumAdapter<E> adapter) {
        db.entrySave(entryId, filialId, refType, Uzum.toBytes(val, adapter));
    }

    public void saveOutletLocation(String entryId, OutletLocation val) {
        saveEntry(entryId, RT.PERSON_LOCATION, val, OutletLocation.UZUM_ADAPTER);
        db.tryMakeStateReady(entryId);
    }

    public void saveSDeal(String entryId, SDeal deal, boolean ready) {
        saveEntry(entryId, RT.SHIPPED, deal, SDeal.UZUM_ADAPTER);
        if (ready) {
            db.tryMakeStateReady(entryId);
            if (db.entryLoadState(entryId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.sdeal_error_in_ready_sdeal));
            }
        }
    }

    public String savePhoto(Scope scope, String entryId, Bitmap bitmap, PhotoConfig photoConfig) {
        String id = String.valueOf(AdminApi.nextSequence());
        return scope.ds.savePhoto(id, entryId, bitmap, photoConfig);
    }

    public void saveOutletVisit(Scope scope, OutletPlan val) {
        saveEntry(val.localId, RT.VISIT_PERSON, val, OutletPlan.UZUM_ADAPTER);
        scope.ds.db.tryMakeStateReady(val.localId);
    }

    public void saveOutletVisitPlan(OutletVisitPlan val) {
        int state = db.entryLoadState(val.localId);
        if (state == EntryState.READY) {
            db.tryMakeStateSaved(val.localId);
        }
        saveEntry(val.localId, RT.VISIT_PLAN, val, OutletVisitPlan.UZUM_ADAPTER);
        db.tryMakeStateReady(val.localId);
    }

    public void saveOutletDisplayBarcode(DisplayBarcode val) {
        saveEntry(val.entryId, RT.DISPLAY_BARCODE, val, DisplayBarcode.UZUM_ADAPTER);
    }

    public void saveCategorization(CatHolder holder, boolean ready) {
        Entry entry = db.entryLoadOne(holder.entryId);
        if (entry != null) {
            EntryState entryState = entry.getEntryState();
            if (entryState.isReady()) {
                db.tryMakeStateSaved(holder.entryId);
            }
        }
        saveEntry(holder.entryId, RT.SAVE_CATEGORIZATION, holder.outletCatQuiz, CatResult.UZUM_ADAPTER);
        if (ready) {
            db.tryMakeStateReady(holder.entryId);
            if (db.entryLoadState(holder.entryId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.categorization_finish_error));
            }
        }

    }

    public void saveDeal(Deal deal, boolean ready) {
        Entry entry = db.entryLoadOne(deal.dealLocalId);
        if (entry != null) {
            EntryState entryState = entry.getEntryState();
            if (!entry.refType.equals(deal.getEntryName())) {
                String message = String.format("entry refType not equal: Entry refType:%s, Deal refType:%s",
                        entry.refType, deal.getEntryName());
                throw new AppError(message);
            }

            if (entryState.isReady()) {
                db.tryMakeStateSaved(deal.dealLocalId);
            }
        }
        saveEntry(deal.dealLocalId, deal.getEntryName(), deal, Deal.UZUM_ADAPTER);

        if (ready) {
            db.tryMakeStateReady(deal.dealLocalId);
            if (db.entryLoadState(deal.dealLocalId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.deal_error_in_ready_deal));
            }
        }
    }

    public void saveIncoming(Incoming incoming, boolean ready) {
        Entry entry = db.entryLoadOne(incoming.localId);
        if (entry != null) {
            EntryState entryState = entry.getEntryState();
            if (entryState.isReady()) {
                db.tryMakeStateSaved(incoming.localId);
            }
        }
        saveEntry(incoming.localId, RT.SAVE_INCOMING, incoming, Incoming.UZUM_ADAPTER);

        if (ready) {
            db.tryMakeStateReady(incoming.localId);
            if (db.entryLoadState(incoming.localId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.incoming_save_error));
            }
        }
    }

    public void saveStocktaking(Stocktaking stocktaking, boolean ready) {
        Entry entry = db.entryLoadOne(stocktaking.localId);
        if (entry != null) {
            EntryState entryState = entry.getEntryState();
            if (entryState.isReady()) {
                db.tryMakeStateSaved(stocktaking.localId);
            }
        }
        saveEntry(stocktaking.localId, RT.SAVE_STOCKTAKING, stocktaking, Stocktaking.UZUM_ADAPTER);

        if (ready) {
            db.tryMakeStateReady(stocktaking.localId);
            if (db.entryLoadState(stocktaking.localId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.stocktaking_save_error));
            }
        }
    }


    public void saveMovementIncoming(MovementIncomingPost incoming, boolean ready) {
        Entry entry = db.entryLoadOne(incoming.entryId);
        if (entry != null) {
            EntryState entryState = entry.getEntryState();
            if (entryState.isReady()) {
                db.tryMakeStateSaved(incoming.entryId);
            }
        }
        saveEntry(incoming.entryId, RT.SAVE_POST_FILIAL_MOVEMENT, incoming, MovementIncomingPost.UZUM_ADAPTER);

        if (ready) {
            db.tryMakeStateReady(incoming.entryId);
            if (db.entryLoadState(incoming.entryId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.movement_incoming_save_error));
            }
        }
    }
}
