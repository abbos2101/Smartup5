package uz.greenwhite.smartup5_trade.m_deal.common;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;

public class WarehouseProductStock {

    private Tuple2 bookedKey(@NonNull Card card, @NonNull String exceptKey) {
        return new Tuple2(card, exceptKey);
    }

    //----------------------------------------------------------------------------------------------

    private final Map<Card, BigDecimal> balance = new HashMap<>();
    // "Tuple2.first as Card" and Tuple2.second as String(exceptKey)
    private final Map<Tuple2, BigDecimal> booked = new HashMap<>();

    //----------------------------------------------------------------------------------------------

    void setBalance(@NonNull Card card, @NonNull BigDecimal balance) {
        if (Card.ANY.equals(card)) throw new AppError("Card code is Any");
        this.balance.put(card, Util.nvl(balance, BigDecimal.ZERO));
    }

    @NonNull
    private BigDecimal getBalance(@NonNull Card card) {
        if (Card.ANY.equals(card)) {
            BigDecimal result = BigDecimal.ZERO;
            for (Card key : this.balance.keySet()) {
                result = result.add(this.balance.get(key));
            }
            return result;
        }
        return Util.nvl(balance.get(card), BigDecimal.ZERO);
    }

    public boolean hasBalance(@NonNull Card card) {
        return getBalance(card).compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean nonBalance(@NonNull Card card) {
        return !hasBalance(card);
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    private BigDecimal getBookedQuantity(@NonNull Card card, @NonNull String exceptKey) {
        BigDecimal result = BigDecimal.ZERO;
        for (Tuple2 key : this.booked.keySet()) {
            if (key.second == exceptKey) continue;
            if (Card.ANY.equals(card) || key.first.equals(card)) {
                result = result.add(this.booked.get(key));
            }
        }
        return result;
    }

    @NonNull
    public BigDecimal getAvailBalance(@NonNull Card card, @NonNull String exceptKey) {
        return getBalance(card).subtract(getBookedQuantity(card, exceptKey));
    }

    //----------------------------------------------------------------------------------------------

    public boolean tryBookQuantity(@NonNull Card card, @NonNull String exceptKey, @NonNull BigDecimal quantity) {
        if (getAvailBalance(card, exceptKey).compareTo(quantity) >= 0) {
            bookQuantity(card, exceptKey, quantity);
            return true;
        }
        return false;
    }

    public void bookQuantity(@NonNull Card card, @NonNull String exceptKey, @NonNull BigDecimal quantity) {
        this.booked.put(bookedKey(card, exceptKey), Util.nvl(quantity, BigDecimal.ZERO));
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    public MyArray<CardQuantity> getCharges(@NonNull Card card, @NonNull String exceptKey) {
        BigDecimal booked = Util.nvl(this.booked.get(bookedKey(card, exceptKey)), BigDecimal.ZERO);

        if (Card.ANY.equals(card)) {

            if (booked.compareTo(BigDecimal.ZERO) != 0) {
                bookQuantity(card, exceptKey, BigDecimal.ZERO);
            }

            BigDecimal avail = getAvailBalance(Card.EMPTY, exceptKey);
            if (avail.compareTo(booked) > 0) {
                bookQuantity(Card.EMPTY, exceptKey, booked);
                return MyArray.from(new CardQuantity(Card.EMPTY.code, booked));
            }

            ArrayList<CardQuantity> result = new ArrayList<>();
            if (avail.compareTo(BigDecimal.ZERO) != 0) {
                bookQuantity(Card.EMPTY, exceptKey, avail);
                result.add(new CardQuantity(Card.EMPTY.code, avail));
                booked = booked.subtract(avail);
            }

            for (Card key : this.balance.keySet()) {
                if (Card.EMPTY.equals(key)) continue;
                avail = getAvailBalance(key, exceptKey);
                if (avail.compareTo(BigDecimal.ZERO) > 0) {
                    if (avail.compareTo(booked) >= 0) {
                        bookQuantity(key, exceptKey, booked);
                        result.add(new CardQuantity(key.code, booked));
                        break;
                    }
                    bookQuantity(key, exceptKey, avail);
                    result.add(new CardQuantity(key.code, avail));
                    booked = booked.subtract(avail);
                }
            }
            return MyArray.from(result);

        } else {
            return MyArray.from(new CardQuantity(card.code, booked));
        }
    }
}
