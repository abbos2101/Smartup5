package uz.greenwhite.smartup5_trade.m_deal.common;// 08.12.2016

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGift;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealAction;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverload;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class Balance {

    @NonNull
    private Tuple2 wpsKey(@NonNull String warehouseId, @NonNull String productId) {
        return new Tuple2(warehouseId, productId);
    }

    //Tuple2 is "Tuple2.first as warehouseId" and "Tuple2.second as productId"
    private final Map<Tuple2, WarehouseProductStock> wps = new HashMap<>();

    public Balance(@NonNull DealRef dealRef) {
        init(dealRef);
    }

    //----------------------------------------------------------------------------------------------

    private void init(final @NonNull DealRef dealRef) {
        Map<Tuple3, BigDecimal> odb = new HashMap<>();

        final MyArray<DealHolder> deals = dealRef.getAllReadyDeals().filter(new MyPredicate<DealHolder>() {
            @Override
            public boolean apply(DealHolder deal) {
                return !deal.deal.dealLocalId.equals(dealRef.dealHolder.deal.dealLocalId);
            }
        });

        for (DealHolder holder : deals) {

            DealOrderModule om = (DealOrderModule) holder.deal.modules.find(VisitModule.M_ORDER, DealModule.KEY_ADAPTER);
            if (om != null) {
                for (DealOrder order : om.orders) {
                    balanceSum(order.warehouseId, order.productId, order.orderQuantities, odb);
                }
            }

            DealGiftModule gm = (DealGiftModule) holder.deal.modules.find(VisitModule.M_GIFT, DealModule.KEY_ADAPTER);
            if (gm != null) {
                for (DealGift gift : gm.gifts) {
                    balanceSum(gift.warehouseId, gift.productId, gift.orderQuantities, odb);
                }
            }

            DealActionModule am = (DealActionModule) holder.deal.modules.find(VisitModule.M_ACTION, DealModule.KEY_ADAPTER);
            if (am != null) {
                for (DealAction action : am.actions) {
                    balanceSum(action.warehouseId, action.productId, action.orderQuantities, odb);
                }
            }

            DealOverloadModule overloadModule = (DealOverloadModule) holder.deal.modules.find(VisitModule.M_OVERLOAD, DealModule.KEY_ADAPTER);
            if (overloadModule != null) {
                for (DealOverload item : overloadModule.overloads) {
                    balanceSum(item.warehouseId, item.productId, item.orderQuantities, odb);
                }
            }
        }

        MyArray<ProductBalance> balances = dealRef.getProductBalances();

        for (ProductBalance pb : balances) {
            Tuple2 key = wpsKey(pb.warehouseId, pb.productId);
            WarehouseProductStock wps = this.wps.get(key);
            if (wps == null) {
                wps = new WarehouseProductStock();
                this.wps.put(key, wps);
            }


            Tuple3 odbKey = new Tuple3(pb.warehouseId, pb.productId, pb.cardCode);
            BigDecimal balance = pb.balance.subtract(Util.nvl(odb.get(odbKey), BigDecimal.ZERO));
            wps.setBalance(Card.make(pb.cardCode), balance);
        }
    }

    private void balanceSum(@NonNull String warehouseId,
                            @NonNull String productId,
                            @NonNull MyArray<CardQuantity> balances,
                            @NonNull Map<Tuple3, BigDecimal> odb) {
        for (CardQuantity val : balances) {
            Tuple3 key = new Tuple3(warehouseId, productId, val.cardCode);
            BigDecimal balance = Util.nvl(odb.get(key), BigDecimal.ZERO);
            odb.put(key, balance.add(val.quantity));
        }
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    public WarehouseProductStock getBalance(@NonNull String warehouseId, @NonNull String productId) {
        if (TextUtils.isEmpty(warehouseId) || TextUtils.isEmpty(productId))
            throw new AppError("warehouseId or productId is null");
        return this.wps.get(wpsKey(warehouseId, productId));
    }
}
