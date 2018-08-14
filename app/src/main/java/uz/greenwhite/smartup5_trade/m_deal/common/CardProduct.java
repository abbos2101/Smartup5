package uz.greenwhite.smartup5_trade.m_deal.common;// 01.12.2016

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;

public class CardProduct {

    public final String cardCode;
    public final String productId;

    public CardProduct(String cardCode, String productId) {
        this.cardCode = cardCode;
        this.productId = productId;
    }

    public Tuple2 getKey(String cardCode, String productId) {
        return new Tuple2(cardCode, productId);
    }

    public Tuple2 getKey() {
        return getKey(this.cardCode, this.productId);
    }

    public static final MyMapper<CardProduct, Tuple2> KEY_ADAPTER = new MyMapper<CardProduct, Tuple2>() {
        @Override
        public Tuple2 apply(CardProduct cardProduct) {
            return cardProduct.getKey();
        }
    };
}
