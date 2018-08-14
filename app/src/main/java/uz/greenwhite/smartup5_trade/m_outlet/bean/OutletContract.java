package uz.greenwhite.smartup5_trade.m_outlet.bean;// 24.10.2016

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletContract {

    public final String outletId;
    public final String contractId;
    public final String contractNumber;

    public OutletContract(String outletId, String contractId, String contractNumber) {
        this.outletId = outletId;
        this.contractId = contractId;
        this.contractNumber = contractNumber;
    }

    public static Tuple2 getKey(String outletId, String contractId) {
        return new Tuple2(outletId, contractId);
    }

    public static final MyMapper<OutletContract, Tuple2> KEY_ADAPTER = new MyMapper<OutletContract, Tuple2>() {
        @Override
        public Tuple2 apply(OutletContract val) {
            return getKey(val.outletId, val.contractId);
        }
    };

    public static final UzumAdapter<OutletContract> UZUM_ADAPTER = new UzumAdapter<OutletContract>() {
        @Override
        public OutletContract read(UzumReader in) {
            return new OutletContract(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletContract val) {
            out.write(val.outletId);
            out.write(val.contractId);
            out.write(val.contractNumber);
        }
    };
}
