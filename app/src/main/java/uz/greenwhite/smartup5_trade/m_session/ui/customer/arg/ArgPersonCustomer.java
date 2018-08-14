package uz.greenwhite.smartup5_trade.m_session.ui.customer.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerFragment;

public class ArgPersonCustomer extends ArgSession {

    public final int personType;

    public ArgPersonCustomer(ArgSession arg, int personType) {
        super(arg.accountId, arg.filialId);
        this.personType = personType;

    }

    public ArgPersonCustomer(UzumReader in) {
        super(in);
        this.personType = in.readInt();
    }


    public boolean isDoctor() {
        return CustomerFragment.K_DOCTOR == personType;
    }

    public boolean isPharm() {
        return CustomerFragment.K_PHARM == personType;
    }

    public boolean isOutlet() {
        return CustomerFragment.K_PERSON == personType;
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.personType);
    }

    public static final UzumAdapter<ArgPersonCustomer> UZUM_ADAPTER = new UzumAdapter<ArgPersonCustomer>() {
        @Override
        public ArgPersonCustomer read(UzumReader in) {
            return new ArgPersonCustomer(in);
        }

        @Override
        public void write(UzumWriter out, ArgPersonCustomer val) {
            val.write(out);
        }
    };
}
