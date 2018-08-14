package uz.greenwhite.smartup5_trade.m_session.ui;// 24.06.2016

import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumParcellable;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.arg.ArgFilial;
import uz.greenwhite.smartup.anor.datasource.AnorDS;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_help.ui.AboutFragment;
import uz.greenwhite.smartup.anor.m_message.ui.MessageIndexFragment;
import uz.greenwhite.smartup.anor.m_setting.arg.ArgSetting;
import uz.greenwhite.smartup.anor.m_setting.ui.SettingFragment;
import uz.greenwhite.smartup.anor.m_task.ui.TaskIndexFragment;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.content.UserNavigationFragment;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_duty.DutyFragment;
import uz.greenwhite.smartup5_trade.m_file_manager.ui.FileManagerIndex;
import uz.greenwhite.smartup5_trade.m_module_edit.ModuleApi;
import uz.greenwhite.smartup5_trade.m_module_edit.arg.ArgModule;
import uz.greenwhite.smartup5_trade.m_module_edit.ui.ModuleSettingFragment;
import uz.greenwhite.smartup5_trade.m_movement.ui.FilialMovementFragment;
import uz.greenwhite.smartup5_trade.m_presentation.ui.PrPresentationListFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.NewReportListFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportSessionFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgCustomer;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSessionOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleMenu;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.DebtorFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.OutletFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.ReturnFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.ShippedFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.TodayFragment;
import uz.greenwhite.smartup5_trade.m_tracking.ui.TrackingFragment;

public class SessionIndexFragment extends UserNavigationFragment {

    public static void open(ArgSession arg) {
        Mold.openNavigation(SessionIndexFragment.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        UzumParcellable p = Mold.parcelableArgument(this);
        return Uzum.toValue(p, ArgSession.UZUM_ADAPTER);
    }

    @Override
    protected ArgSession getArgument() {
        return getArgSession();
    }

    private static final int DEFAULT = 1000;

    public static final int DASHBOARD = DEFAULT + 1;
    public static final int EXIT_APP = DEFAULT + 2;
    public static final int SYNC = 1;
    public static final int SETTING = 2;
    public static final int TODAY_VISIT = 3;
    public static final int TODAY_SHIPPED = 4;
    //public static final int VISIT_PLAN = 5;
    public static final int REPORT = 6;
    public static final int CUSTOMER = 7;
    public static final int DUTY = 8;
    public static final int DEBTOR = 9;
    public static final int TRACKING = 10;
    public static final int MESSAGE = 11;
    public static final int TASK = 12;
    public static final int FILE_MANAGER = 13;
    public static final int CONNECT_WITH_US = 14;
    public static final int VISIT_RETURN = 15;
    public static final int REPORT_V2 = 16;
    public static final int WAREHOUSE = 17;
    public static final int MOVEMENT_INPUT = 18;
    public static final int PRESENTATION = 19;
    public static final int OUTLET_SHOP = 101;
    public static final int OUTLET_DOCTOR = 102;
    public static final int OUTLET_PHARM = 103;

    public NavigationView navigationView;

    public static final MyArray<NavigationItem> FORMS = MyArray.from(
            new NavigationItem(TODAY_VISIT, DS.getString(R.string.session_today), R.mipmap.menu_3),
            new NavigationItem(TODAY_SHIPPED, DS.getString(R.string.session_shipped), R.mipmap.menu_3),
            new NavigationItem(REPORT, DS.getString(R.string.session_report), R.mipmap.menu_6),
            new NavigationItem(REPORT_V2, DS.getString(R.string.session_report_v2), R.mipmap.menu_6),
            new NavigationItem(DUTY, DS.getString(R.string.session_duty), R.mipmap.menu_7),
            new NavigationItem(DEBTOR, DS.getString(R.string.session_bedtor), R.mipmap.menu_debtor),
            new NavigationItem(TRACKING, DS.getString(R.string.session_tracking_mp), R.mipmap.menu_tracking),
            new NavigationItem(OUTLET_DOCTOR, DS.getString(R.string.session_outlet_doctors), R.mipmap.menu_4),
            new NavigationItem(OUTLET_PHARM, DS.getString(R.string.session_outlet_pharms), R.mipmap.menu_4),
            new NavigationItem(WAREHOUSE, DS.getString(R.string.session_warehouse), R.mipmap.menu_4),
            new NavigationItem(MOVEMENT_INPUT, DS.getString(R.string.warehouse_movement_incoming), R.mipmap.menu_4)
    );

    public static MyArray<NavigationItem> getFormItems(ArgSession argSession) {
        Setting setting = argSession.getSetting();
        MyArray<NavigationItem> forms = FORMS;

        if (setting.deal.visitAllow) {
            forms = forms.append(new NavigationItem(OUTLET_SHOP, DS.getString(R.string.session_outlets), R.mipmap.menu_4));
        }

        if (setting.deal.returnAllow) {
            forms = forms.append(new NavigationItem(VISIT_RETURN, DS.getString(R.string.session_menu_return), R.mipmap.menu_4));
        }
        MyArray<NavigationItem> result = RoleMenu.sortForms(argSession, RoleMenu.SESSION, forms, NavigationItem.KEY_ADAPTER);

        result = result.prepend(new NavigationItem(SYNC, DS.getString(R.string.admin_sync), R.mipmap.menu_2));
        result = result.prepend(new NavigationItem(DASHBOARD, DS.getString(R.string.session_dashboard), R.mipmap.menu_1));

        result = result.append(new NavigationItem(FILE_MANAGER, DS.getString(R.string.f_manager_title), R.mipmap.menu_file));

        result = result.append(new NavigationItem(MESSAGE, DS.getString(R.string.session_messages), R.mipmap.menu_5));
        result = result.append(new NavigationItem(TASK, DS.getString(R.string.task), R.mipmap.menu_task));
        result = result.append(new NavigationItem(SETTING, DS.getString(R.string.admin_menu_setting), R.mipmap.menu_8));
        result = result.append(new NavigationItem(CONNECT_WITH_US, DS.getString(R.string.session_connection_with_us), R.mipmap.menu_info));
        result = result.append(new NavigationItem(EXIT_APP, DS.getString(R.string.session_exit), R.mipmap.menu_exit));

        if (BuildConfig.DEBUG) {
            result = result.append(new NavigationItem(CUSTOMER, "Customers", R.mipmap.menu_4));
            result = result.append(new NavigationItem(PRESENTATION, "Presentation", R.mipmap.menu_4));
        }

        if (!setting.deal.visitAllow) {

            if (result.contains(OUTLET_DOCTOR, NavigationItem.KEY_ADAPTER)) {
                result = result.filter(new MyPredicate<NavigationItem>() {
                    @Override
                    public boolean apply(NavigationItem navigationItem) {
                        return OUTLET_DOCTOR != navigationItem.id;
                    }
                });
            }

            if (result.contains(OUTLET_PHARM, NavigationItem.KEY_ADAPTER)) {
                result = result.filter(new MyPredicate<NavigationItem>() {
                    @Override
                    public boolean apply(NavigationItem navigationItem) {
                        return OUTLET_PHARM != navigationItem.id;
                    }
                });
            }
        }
        return ModuleApi.INSTANCE.makeFormOrderNo(argSession, ArgModule.SESSION, result);
    }

    private MyArray<Integer> formIds = MyArray.emptyArray();
    private int currentFormId = -1;
    private long lastBackClickTime = 0;

    @Override
    protected void reloadUserNavigationHeader() {
        super.reloadUserNavigationHeader();

        vsHeader.id(R.id.miv_module_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModuleSettingFragment.Companion.open(getActivity(), new ArgModule(getArgSession(), ArgModule.SESSION));
            }
        });
    }

    @Override
    public void onStart() {
        reloadIndexContent();
        super.onStart();
    }

    public void reloadIndexContent() {
        ArgSession arg = getArgSession();
        navigationView = (NavigationView) getActivity().findViewById(R.id.gwslib_navigation);

        MyArray<NavigationItem> formItems = ModuleApi.INSTANCE.makeFormVisible(getArgSession(), ArgModule.SESSION, getFormItems(arg));
        formIds = formItems.map(NavigationItem.KEY_ADAPTER);
        setItems(formItems);
        setBadge(MESSAGE);
        setBadge(TASK);
    }

    @Override
    protected int getDefaultFormId() {
        int formId = ModuleApi.INSTANCE.getMainForm(getArgSession(), ArgModule.SESSION);
        if (formId != -1 && formIds.contains(formId, MyMapper.<Integer>identity())) {
            return formId;
        }
        return super.getDefaultFormId();
    }

    @Override
    public boolean showForm(NavigationItem form) {
        MoldContentFragment f = make(form.id);
        if (f != null) {
            Mold.replaceContent(getActivity(), f, form);
            return true;
        } else {
            Mold.closeIndexDrawer(getActivity());
        }
        return false;
    }

    public MoldContentFragment make(int id) {
        currentFormId = id;
        ArgSession arg = getArgSession();
        switch (id) {
            case WAREHOUSE:
                return WarehouseFragment.Companion.newInstance(getArgSession());
            case MOVEMENT_INPUT:
                return FilialMovementFragment.Companion.newInstance(getArgSession());
            case PRESENTATION:
                return PrPresentationListFragment.Companion.newInstance(getArgSession());
            case DASHBOARD:
                return DashboardFragment.newInstance(getArgSession());
            case SYNC:
                return SyncFragment.newInstance(getArgSession());
            case TODAY_VISIT:
                return TodayFragment.newInstance(getArgSession());
            case TODAY_SHIPPED:
                return ShippedFragment.newInstance(getArgSession());
            case VISIT_RETURN:
                return ReturnFragment.newInstance(getArgSession());
            case OUTLET_SHOP:
                return OutletFragment.newInstance(new ArgSessionOutlet(arg, ""));
            case OUTLET_DOCTOR:
                return OutletFragment.newInstance(new ArgSessionOutlet(arg, Outlet.K_HOSPITAL));
            case OUTLET_PHARM:
                return OutletFragment.newInstance(new ArgSessionOutlet(arg, Outlet.K_PHARMACY));
            case SETTING:
                return SettingFragment.newInstance(new ArgSetting(new ArgFilial(arg.accountId, arg.filialId)));
            case REPORT:
                return ReportSessionFragment.newInstance(getArgSession());
            case REPORT_V2:
                NewReportListFragment.open(getActivity(), getArgSession());
                return null;
            case CUSTOMER:
                return CustomerFragment.newInstance(new ArgCustomer(getArgSession(), 1));
            case DUTY:
                return DutyFragment.newInstance(getArgSession());
            case DEBTOR:
                return DebtorFragment.newInstance(getArgSession());
            case TRACKING:
                TrackingFragment.open(getArgSession());
                return null;
            case MESSAGE:
                MessageIndexFragment.open(new ArgFilial(arg.accountId, arg.filialId));
                return null;
            case TASK:
                TaskIndexFragment.open(new ArgFilial(arg.accountId, arg.filialId));
                return null;
            case CONNECT_WITH_US:
                AboutFragment.open();
                return null;
            case FILE_MANAGER:
                FileManagerIndex.open(getArgSession());
                return null;
            case EXIT_APP:
                exitInSession();
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onDrawerOpened() {
        super.onDrawerOpened();
        reloadIndexContent();
    }

    @Override
    public boolean onBackPressed() {

        if ((System.currentTimeMillis() - lastBackClickTime) <= 2000) { // 2 second
            AdminApi.removeAccountCur();
            DS.clearScope();
            getActivity().finish();

            return true;
        }

        if (currentFormId == getDefaultFormId()) {
            lastBackClickTime = System.currentTimeMillis();
            Mold.makeSnackBar(getActivity(), DS.getString(R.string.session_double_exit)).show();
        } else {
            showForm(getDefaultFormId());
        }
        return true;
    }

    private void exitInSession() {
        UI.confirm(getActivity(),
                DS.getString(R.string.exit_title),
                DS.getString(R.string.exit_content), new Command() {
                    @Override
                    public void apply() {
                        AdminApi.removeAccountCur();
                        DS.clearScope();
                        getActivity().finish();
                    }
                });
    }

    @Override
    public void onDrawerClosed() {
        super.onDrawerClosed();
    }

    //    Sets unread message badge in drawer
    private void setBadge(int menuKey) {
//        getting navigation menu item's title and action view
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(menuKey);
        if (item == null) return;
        MenuItem menuItem = menu.findItem(menuKey).setActionView(R.layout.menu_counter);
        TextView actionView = (TextView) menuItem.getActionView();
        CharSequence menuTitle = menuItem.getTitle();

        if (menuKey == MESSAGE) {
//       save/get new message count to/from Preferences
            MyArray<String> unreadMessageIds = AdminApi.loadNewMsgCountFromPref();
            int unreadMsgSize = unreadMessageIds.size();
//        if unread message count is greater than ZERO, than shows the count
            if (unreadMsgSize > 0) {
                String msgCount = unreadMsgSize > 99 ? "99+" : String.valueOf(unreadMsgSize);
                actionView.setText(msgCount);
                menuItem.setTitle(UI.html().b().v(menuTitle).b().html());
                menuItem.getIcon().setColorFilter(AnorDS.getColor(R.color.dark_silver), PorterDuff.Mode.SRC_IN);
            } else {
                actionView.setText("");
                menuItem.setTitle(menuTitle);
                menuItem.getIcon().clearColorFilter();
            }
        } else if (menuKey == TASK) {
            MyArray<String> newTaskIds = AdminApi.loadNewTaskCountFromPref();
            int newTaskSize = newTaskIds.size();
            if (newTaskSize > 0) {
                String taskCount = newTaskSize > 99 ? "99+" : String.valueOf(newTaskSize);
                actionView.setText(taskCount);
                menuItem.setTitle(UI.html().b().v(menuTitle).b().html());
                menuItem.getIcon().setColorFilter(AnorDS.getColor(R.color.dark_silver), PorterDuff.Mode.SRC_IN);
            } else {
                actionView.setText("");
                menuItem.setTitle(menuTitle);
                menuItem.getIcon().clearColorFilter();
            }
        }
    }
}
