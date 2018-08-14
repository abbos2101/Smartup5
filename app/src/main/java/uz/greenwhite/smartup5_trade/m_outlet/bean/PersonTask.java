package uz.greenwhite.smartup5_trade.m_outlet.bean;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonTask {

    public final String taskId;
    public final String personId;
    public final String title;
    public final String beginDate;
    public final String endDate;
    public final String personType;
    public final String state;

    public PersonTask(String taskId,
                      String personId,
                      String title,
                      String beginDate,
                      String endDate,
                      String personType,
                      String state) {
        this.taskId = taskId;
        this.personId = personId;
        this.title = title;
        this.state = state;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.personType = personType;
    }

    public static final UzumAdapter<PersonTask> UZUM_ADAPTER = new UzumAdapter<PersonTask>() {
        @Override
        public PersonTask read(UzumReader in) {
            return new PersonTask(
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonTask val) {
            out.write(val.taskId);
            out.write(val.personId);
            out.write(val.title);
            out.write(val.beginDate);
            out.write(val.endDate);
            out.write(val.personType);
            out.write(val.state);
        }
    };
}
