package uz.greenwhite.smartup5_trade.m_module_edit.arg;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.ui.SessionIndexFragment;

public class ArgModule extends ArgSession {


    public static final String SESSION = "module:session";
    public static final String PERSON = "module:person";

    public final String formCode;
    public final String personId;

    public ArgModule(ArgSession arg, String formCode) {
        super(arg.accountId, arg.filialId);
        this.formCode = formCode;
        this.personId = "";
    }

    public ArgModule(ArgOutlet arg, String formCode) {
        super(arg.accountId, arg.filialId);
        this.formCode = formCode;
        this.personId = arg.outletId;
    }

    protected ArgModule(UzumReader in) {
        super(in);
        this.formCode = in.readString();
        this.personId = in.readString();
    }

    public MyArray<NavigationItem> getFormItems() {
        switch (formCode) {
            case SESSION:
                final MyArray<Integer> sessionIds = MyArray.from(SessionIndexFragment.EXIT_APP, SessionIndexFragment.SETTING, SessionIndexFragment.CONNECT_WITH_US);
                MyArray<NavigationItem> sessionFormItems = SessionIndexFragment.getFormItems(this);
                return sessionFormItems.filter(new MyPredicate<NavigationItem>() {
                    @Override
                    public boolean apply(NavigationItem navigationItem) {
                        return !sessionIds.contains(navigationItem.id, MyMapper.<Integer>identity());
                    }
                });

            case PERSON:
                final MyArray<Integer> personIds = MyArray.from(OutletIndexFragment.EXIT);
                ArgOutlet argOutlet = new ArgOutlet(this, personId);
                MyArray<NavigationItem> personFormItems = OutletIndexFragment.getFormItems(argOutlet, false);
                return personFormItems.filter(new MyPredicate<NavigationItem>() {
                    @Override
                    public boolean apply(NavigationItem navigationItem) {
                        return !personIds.contains(navigationItem.id, MyMapper.<Integer>identity());
                    }
                });

            default:
                throw AppError.Unsupported();
        }
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(formCode);
        w.write(personId);
    }

    public static final UzumAdapter<ArgModule> UZUM_ADAPTER = new UzumAdapter<ArgModule>() {
        @Override
        public ArgModule read(UzumReader uzumReader) {
            return new ArgModule(uzumReader);
        }

        @Override
        public void write(UzumWriter uzumWriter, ArgModule argModule) {
            argModule.write(uzumWriter);
        }
    };
}
