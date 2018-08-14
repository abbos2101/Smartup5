package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;// 05.11.2016

import uz.greenwhite.lib.collection.MyArray;

public class ProductRow {

    public final String position;
    public final String sku;
    public final MyArray<ProductPriceRow> priceRows;

    public ProductRow(String position, String sku, MyArray<ProductPriceRow> priceRows) {
        this.position = position;
        this.sku = sku;
        this.priceRows = priceRows;
    }

    public static final ProductRow DEFAULT = new ProductRow("", "", MyArray.<ProductPriceRow>emptyArray());
}
