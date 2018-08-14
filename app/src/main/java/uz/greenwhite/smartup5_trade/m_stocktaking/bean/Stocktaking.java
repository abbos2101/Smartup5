package uz.greenwhite.smartup5_trade.m_stocktaking.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Stocktaking {

    public final String localId;
    public final String filialId;
    public final String warehouseId; // Склад
    public final StocktakingHeader header;
    public final MyArray<StocktakingProduct> products;

    public Stocktaking(String localId, String filialId, String warehouseId, StocktakingHeader header, MyArray<StocktakingProduct> products) {
        this.localId = localId;
        this.filialId = filialId;
        this.warehouseId = warehouseId;
        this.header = header;
        this.products = products;
    }

    public static final UzumAdapter<Stocktaking> UZUM_ADAPTER = new UzumAdapter<Stocktaking>() {
        @Override
        public Stocktaking read(UzumReader in) {
            return new Stocktaking(in.readString(),in.readString(),
                    in.readString(), in.readValue(StocktakingHeader.UZUM_ADAPTER),
                    in.readArray(StocktakingProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Stocktaking val) {
            out.write(val.localId);
            out.write(val.filialId);
            out.write(val.warehouseId);
            out.write(val.header, StocktakingHeader.UZUM_ADAPTER);
            out.write(val.products, StocktakingProduct.UZUM_ADAPTER);
        }
    };
}
