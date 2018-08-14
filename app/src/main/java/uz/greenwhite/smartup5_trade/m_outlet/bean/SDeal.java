package uz.greenwhite.smartup5_trade.m_outlet.bean;// 08.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SNote;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SService;

public class SDeal {

    public final String dealId;                 // 1
    public final String outletId;               // 2
    public final String filialId;               // 3
    public final String deliveryDate;           // 4
    public final RoundModel roundModel;         // 5
    public final MyArray<SOrder> orders;        // 6
    public final MyArray<SPayment> payments;    // 7
    public final SAttach attach;                // 8
    public final String returnReasonId;         // 9
    public final MyArray<SService> service;     // 10
    public final SNote note;                    // 11
    public final MyArray<SOverload> overload;   // 12

    public SDeal(String dealId,
                 String outletId,
                 String filialId,
                 String deliveryDate,
                 String roundModel,
                 MyArray<SOrder> orders,
                 MyArray<SPayment> payments,
                 SAttach attach,
                 String returnReasonId,
                 MyArray<SService> service,
                 SNote note,
                 MyArray<SOverload> overload) {
        this.dealId = dealId;
        this.outletId = outletId;
        this.filialId = filialId;
        this.deliveryDate = deliveryDate;
        this.roundModel = RoundModel.make(roundModel);
        this.orders = orders;
        this.payments = payments;
        this.attach = Util.nvl(attach, SAttach.DEFAULT);
        this.returnReasonId = Util.nvl(returnReasonId);
        this.service = MyArray.nvl(service);
        this.note = note;
        this.overload= MyArray.nvl(overload);
    }

    public static final MyMapper<SDeal, String> KEY_ADAPTER = new MyMapper<SDeal, String>() {
        @Override
        public String apply(SDeal val) {
            return val.dealId;
        }
    };

    public static final UzumAdapter<SDeal> UZUM_ADAPTER = new UzumAdapter<SDeal>() {
        @Override
        public SDeal read(UzumReader in) {
            return new SDeal(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readArray(SOrder.UZUM_ADAPTER), in.readArray(SPayment.UZUM_ADAPTER),
                    in.readValue(SAttach.UZUM_ADAPTER), in.readString(),
                    in.readArray(SService.UZUM_ADAPTER), in.readValue(SNote.UZUM_ADAPTER),
                    in.readArray(SOverload.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, SDeal val) {
            out.write(val.dealId);
            out.write(val.outletId);
            out.write(val.filialId);
            out.write(val.deliveryDate);
            out.write(val.roundModel.model);
            out.write(val.orders, SOrder.UZUM_ADAPTER);
            out.write(val.payments, SPayment.UZUM_ADAPTER);
            out.write(val.attach, SAttach.UZUM_ADAPTER);
            out.write(val.returnReasonId);
            out.write(val.service, SService.UZUM_ADAPTER);
            out.write(val.note, SNote.UZUM_ADAPTER);
            out.write(val.overload, SOverload.UZUM_ADAPTER);
        }
    };
}
