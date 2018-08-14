package uz.greenwhite.smartup5_trade.m_incoming.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class IncomingHeader {

    public final String incomingDate; // Дата
    public final String incomingNumber; // Номер прихода
    public final String currencyId; // Валюта
    public final String providerPersonId; // Поставщик
    public final String note; // Примечание

    public IncomingHeader(String incomingDate,
                          String incomingNumber,
                          String currencyId,
                          String providerPersonId,
                          String note) {
        this.incomingDate = incomingDate;
        this.incomingNumber = incomingNumber;
        this.currencyId = currencyId;
        this.providerPersonId = providerPersonId;
        this.note = note;
    }

    public static IncomingHeader makeDefault(String today) {
        return new IncomingHeader(today, "", "", "", "");
    }

    public static final UzumAdapter<IncomingHeader> UZUM_ADAPTER = new UzumAdapter<IncomingHeader>() {
        @Override
        public IncomingHeader read(UzumReader in) {
            return new IncomingHeader(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, IncomingHeader val) {
            out.write(val.incomingDate);
            out.write(val.incomingNumber);
            out.write(val.currencyId);
            out.write(val.providerPersonId);
            out.write(val.note);
        }
    };
}
