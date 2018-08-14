package uz.greenwhite.smartup5_trade.m_deal_history.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class HDealProduct {

    public final String productId;
    public final String warehouseId;
    public final String priceTypeId;
    public final String cardCode;
    public final String price;
    public final String quantity;
    public final String margin;
    public final String currencyId;
    public final Boolean mml;
    public final String bonusId;
    public final String loadId;
    public final String productUnitId;

    public HDealProduct(String productId,
                        String warehouseId,
                        String priceTypeId,
                        String cardCode,
                        String price,
                        String quantity,
                        String margin,
                        String currencyId,
                        Boolean mml,
                        String bonusId,
                        String loadId,
                        String productUnitId) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
        this.cardCode = cardCode;
        this.price = price;
        this.quantity = quantity;
        this.margin = margin;
        this.currencyId = currencyId;
        this.mml = mml;
        this.bonusId = bonusId;
        this.loadId = loadId;
        this.productUnitId = productUnitId;
    }

    public static final UzumAdapter<HDealProduct> UZUM_ADAPTER = new UzumAdapter<HDealProduct>() {
        @Override
        public HDealProduct read(UzumReader in) {
            return new HDealProduct(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readBoolean(),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, HDealProduct val) {
            out.write(val.productId);
            out.write(val.warehouseId);
            out.write(val.priceTypeId);
            out.write(val.cardCode);
            out.write(val.price);
            out.write(val.quantity);
            out.write(val.margin);
            out.write(val.currencyId);
            out.write(val.mml);
            out.write(val.bonusId);
            out.write(val.loadId);
            out.write(val.productUnitId);
        }
    };

}
