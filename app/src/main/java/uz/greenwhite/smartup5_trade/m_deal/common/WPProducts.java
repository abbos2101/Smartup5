package uz.greenwhite.smartup5_trade.m_deal.common;// 01.12.2016

import uz.greenwhite.lib.collection.MyArray;

public class WPProducts {

    public final String warehouseId;
    public final String priceTypeId;
    public final MyArray<CardProduct> cardProducts;

    public WPProducts(String warehouseId, String priceTypeId, MyArray<CardProduct> cardProducts) {
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
        this.cardProducts = cardProducts;
    }
}
