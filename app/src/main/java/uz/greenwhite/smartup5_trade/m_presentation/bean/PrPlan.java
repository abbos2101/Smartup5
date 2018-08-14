package uz.greenwhite.smartup5_trade.m_presentation.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PrPlan {

    public static final String T_MCF = "mcf";
    public static final String T_PHARMACY = "pharmacy";
    public static final String T_DOCTOR = "doctor";

    public final String planId;
    public final String planType;
    public final String date;
    public final String time;
    public final String fileSha;
    public final String personId;
    public final String presentationName;
    public final String employees;  // Если PtPlan.planType выбрать то можно указать количество сотрудников
    public final String specialties;
    public final String productId;
    public final String note;

    public PrPlan(String planId,
                  String planType,
                  String date,
                  String time,
                  String fileSha,
                  String personId,
                  String presentationName,
                  String employees,
                  String specialties,
                  String productId,
                  String note) {
        this.planId = planId;
        this.planType = planType;
        this.date = date;
        this.time = time;
        this.fileSha = fileSha;
        this.personId = personId;
        this.presentationName = presentationName;
        this.employees = employees;
        this.specialties = specialties;
        this.productId = productId;
        this.note = note;
    }

    public static final UzumAdapter<PrPlan> UZUM_ADAPTER = new UzumAdapter<PrPlan>() {
        @Override
        public PrPlan read(UzumReader in) {
            return new PrPlan(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PrPlan val) {
            out.write(val.planId);
            out.write(val.planType);
            out.write(val.date);
            out.write(val.time);
            out.write(val.fileSha);
            out.write(val.personId);
            out.write(val.presentationName);
            out.write(val.employees);
            out.write(val.specialties);
            out.write(val.productId);
            out.write(val.note);
        }
    };
}
