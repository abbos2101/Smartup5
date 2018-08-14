package uz.greenwhite.smartup5_trade.m_incoming.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Incoming {

    public final String localId;
    public final String filialId;
    public final String warehouseId; // Склад
    public final IncomingHeader header;
    public final MyArray<IncomingProduct> products;

    public Incoming(String localId, String filialId, String warehouseId, IncomingHeader header, MyArray<IncomingProduct> products) {
        this.localId = localId;
        this.filialId = filialId;
        this.warehouseId = warehouseId;
        this.header = header;
        this.products = products;
    }

    public static final UzumAdapter<Incoming> UZUM_ADAPTER = new UzumAdapter<Incoming>() {
        @Override
        public Incoming read(UzumReader in) {
            return new Incoming(in.readString(),in.readString(),
                    in.readString(), in.readValue(IncomingHeader.UZUM_ADAPTER),
                    in.readArray(IncomingProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Incoming val) {
            out.write(val.localId);
            out.write(val.filialId);
            out.write(val.warehouseId);
            out.write(val.header, IncomingHeader.UZUM_ADAPTER);
            out.write(val.products, IncomingProduct.UZUM_ADAPTER);
        }
    };
}
