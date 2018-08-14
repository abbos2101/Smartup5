package uz.greenwhite.smartup5_trade.m_presentation.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgPrAddPlan extends ArgPrDatePlan {

    public final String date;
    public final String time;
    public final String fileSha;

    public ArgPrAddPlan(ArgPrDatePlan arg, String date, String time, String fileSha) {
        super(arg, arg.planType);
        this.date = date;
        this.time = time;
        this.fileSha = fileSha;
    }

    protected ArgPrAddPlan(UzumReader in) {
        super(in);
        this.date = in.readString();
        this.time = in.readString();
        this.fileSha = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(date);
        w.write(time);
        w.write(fileSha);
    }

    public static final UzumAdapter<ArgPrAddPlan> UZUM_ADAPTER = new UzumAdapter<ArgPrAddPlan>() {
        @Override
        public ArgPrAddPlan read(UzumReader in) {
            return new ArgPrAddPlan(in);
        }

        @Override
        public void write(UzumWriter out, ArgPrAddPlan val) {
            val.write(out);
        }
    };
}
