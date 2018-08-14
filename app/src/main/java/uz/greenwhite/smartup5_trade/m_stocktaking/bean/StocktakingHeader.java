package uz.greenwhite.smartup5_trade.m_stocktaking.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class StocktakingHeader {

    public final String date; // Дата
    public final String number; // Номер прихода
    public final String currencyId; // Валюта
    public final String note; // Примечание

    public StocktakingHeader(String date,
                             String number,
                             String currencyId,
                             String note) {
        this.date = date;
        this.number = number;
        this.currencyId = currencyId;
        this.note = note;
    }

    public static StocktakingHeader makeDefault(String today) {
        return new StocktakingHeader(today, "", "", "");
    }

    public static final UzumAdapter<StocktakingHeader> UZUM_ADAPTER = new UzumAdapter<StocktakingHeader>() {
        @Override
        public StocktakingHeader read(UzumReader in) {
            return new StocktakingHeader(
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, StocktakingHeader val) {
            out.write(val.date);
            out.write(val.number);
            out.write(val.currencyId);
            out.write(val.note);
        }
    };
}
