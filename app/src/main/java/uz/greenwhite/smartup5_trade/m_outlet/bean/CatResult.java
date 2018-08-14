package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CatResult {

    public final String filialId;
    public final String outletId;
    public final String clientTypeId;
    public final MyArray<CatResultDetail> quizes;

    public CatResult(String filialId, String outletId, String clientTypeId, MyArray<CatResultDetail> quizes) {
        this.filialId = filialId;
        this.outletId = outletId;
        this.clientTypeId = clientTypeId;
        this.quizes = quizes;
    }

    public static final UzumAdapter<CatResult> UZUM_ADAPTER = new UzumAdapter<CatResult>() {

        @Override
        public CatResult read(UzumReader in) {
            return new CatResult(
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readArray(CatResultDetail.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, CatResult val) {
            out.write(val.filialId);
            out.write(val.outletId);
            out.write(val.clientTypeId);
            out.write(val.quizes, CatResultDetail.UZUM_ADAPTER);
        }
    };
}
