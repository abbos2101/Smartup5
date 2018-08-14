package uz.greenwhite.smartup5_trade.m_duty.arg;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_duty.DutyApi;
import uz.greenwhite.smartup5_trade.m_duty.bean.FilialActionRow;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;

public class ArgCondition extends ArgSession {

    public final String actionId;

    public ArgCondition(ArgSession arg, String actionId) {
        super(arg.accountId, arg.filialId);
        this.actionId = actionId;
    }

    protected ArgCondition(UzumReader in) {
        super(in);
        this.actionId = in.readString();
    }

    public FilialActionRow getFilialActionRow() {
        return DutyApi.getPersonActions(getScope())
                .findFirst(new MyPredicate<FilialActionRow>() {
                    @Override
                    public boolean apply(FilialActionRow val) {
                        return val.action.actionId.equals(actionId);
                    }
                });
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.actionId);
    }

    public static final UzumAdapter<ArgCondition> UZUM_ADAPTER = new UzumAdapter<ArgCondition>() {
        @Override
        public ArgCondition read(UzumReader in) {
            return new ArgCondition(in);
        }

        @Override
        public void write(UzumWriter out, ArgCondition val) {
            val.write(out);
        }
    };
}
