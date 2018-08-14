package uz.greenwhite.smartup5_trade.common.google_analytics;// 08.08.2016

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import uz.greenwhite.smartup.anor.m_admin.ui.AccountAddFragment;
import uz.greenwhite.smartup.anor.m_admin.ui.AccountFragment;
import uz.greenwhite.smartup.anor.m_admin.ui.AuthorizedFragment;
import uz.greenwhite.smartup.anor.m_admin.ui.LoginPasswordFragment;
import uz.greenwhite.smartup.anor.m_debug.ui.DeveloperDialog;
import uz.greenwhite.smartup.anor.m_message.ui.ComposeFragment;
import uz.greenwhite.smartup.anor.m_message.ui.MessageIndexFragment;
import uz.greenwhite.smartup.anor.m_setting.ui.ChangePasswordFragment;
import uz.greenwhite.smartup.anor.m_setting.ui.PermissionFragment;
import uz.greenwhite.smartup.anor.m_setting.ui.SettingFragment;
import uz.greenwhite.smartup.anor.m_setting.ui.SyncLogFragment;
import uz.greenwhite.smartup.anor.m_task.ui.TaskIndexFragment;
import uz.greenwhite.smartup.anor.m_task.ui.TaskInfoFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.ActionFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.AttachFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.CommentFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealErrorFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealIndexFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealOutletInfoFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.GiftFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.MemoFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.NoteFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.OrderFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.OverloadFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.PaymentFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.PhotoFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.PhotoInfoFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.QuizFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.RetailAuditFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.ReturnFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.ReturnPaymentFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.ServiceFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.StockFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.TotalFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.ViolationFragment;
import uz.greenwhite.smartup5_trade.m_deal.ui.agree.AgreeFragment;
import uz.greenwhite.smartup5_trade.m_deal_history.ui.DealHistoryFragment;
import uz.greenwhite.smartup5_trade.m_debtor.ui.PrepaymentFragment;
import uz.greenwhite.smartup5_trade.m_debtor.ui.PrepaymentOutletFragment;
import uz.greenwhite.smartup5_trade.m_display.ui.DisplayFragment;
import uz.greenwhite.smartup5_trade.m_display.ui.DisplayReviewFragment;
import uz.greenwhite.smartup5_trade.m_duty.DutyFragment;
import uz.greenwhite.smartup5_trade.m_duty.FilialActionFragment;
import uz.greenwhite.smartup5_trade.m_duty.FilialActionInfoFragment;
import uz.greenwhite.smartup5_trade.m_duty.PriceFragment;
import uz.greenwhite.smartup5_trade.m_file_manager.ui.AccessFragment;
import uz.greenwhite.smartup5_trade.m_file_manager.ui.FileManagerIndex;
import uz.greenwhite.smartup5_trade.m_incoming.ui.IncomingFragment;
import uz.greenwhite.smartup5_trade.m_incoming.ui.IncomingProductFragment;
import uz.greenwhite.smartup5_trade.m_location.ui.LocationFragment;
import uz.greenwhite.smartup5_trade.m_module_edit.ui.ModuleSettingFragment;
import uz.greenwhite.smartup5_trade.m_movement.ui.FilialMovementFragment;
import uz.greenwhite.smartup5_trade.m_movement.ui.MovementFragment;
import uz.greenwhite.smartup5_trade.m_near.ui.MapFragment;
import uz.greenwhite.smartup5_trade.m_near.ui.MapListFragment;
import uz.greenwhite.smartup5_trade.m_near.ui.NearOutletFragment;
import uz.greenwhite.smartup5_trade.m_near.ui.NearOutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_near.ui.NearOutletListFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderGiftFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderInfoFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderInfoIndexFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderOrderFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderPaymentFragment;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderStockFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.CategorizationFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletDebtorFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletInfoFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.PersonFileFragment;
import uz.greenwhite.smartup5_trade.m_outlet.ui.ShippedFragment;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.NaturalPersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.PersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_presentation.ui.PrAddPlanFragment;
import uz.greenwhite.smartup5_trade.m_presentation.ui.PrDatePlanFragment;
import uz.greenwhite.smartup5_trade.m_presentation.ui.PrPresentationListFragment;
import uz.greenwhite.smartup5_trade.m_product.ui.FileFragment;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductPhotoFullFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.NewReportListFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportDateFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportOutletFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportSessionFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportViewFragment;
import uz.greenwhite.smartup5_trade.m_report.ui.TestReportViewFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.DashboardFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.SyncFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.VisitPlanFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.WarehouseFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.DebtorFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.person.OutletFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SAttachFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SDealIndexFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SDealNoteFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SErrorFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SOrderFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SOutletInfoFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SOverloadFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SPaymentFragment;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SReturnReasonFragment;
import uz.greenwhite.smartup5_trade.m_stocktaking.ui.StocktakingFragment;
import uz.greenwhite.smartup5_trade.m_stocktaking.ui.StocktakingProductFragment;
import uz.greenwhite.smartup5_trade.m_tracking.ui.TrackingFragment;
import uz.greenwhite.smartup5_trade.m_tracking.ui.TrackingIndexFragment;
import uz.greenwhite.smartup5_trade.m_tracking.ui.TrackingMapFragment;
import uz.greenwhite.smartup5_trade.m_vp.ui.OutletVisitFragment;
import uz.greenwhite.smartup5_trade.m_vp_outlet.ui.OutletVisitPlanFragment;
import uz.greenwhite.smartup5_trade.m_warehouse.ui.WarehouseIndexFragment;
import uz.greenwhite.smartup5_trade.send_my_location.SendMyLocationFragment;

public class TrackingFragmentNames {

    private static final Map<String, String> names = new HashMap<>();

    public static void init() {
        names.put(DeveloperDialog.class.getName(), "admin(Для разработчиков)");
        names.put(PermissionFragment.class.getName(), "admin.setting(Разрешений)");
        names.put(SettingFragment.class.getName(), "admin(Настройки)");
        names.put(SyncFragment.class.getName(), "admin(Синхронизация)");
        names.put(AccountAddFragment.class.getName(), "admin(Add account)");
        names.put(AccountFragment.class.getName(), "admin(Account)");
        names.put(AuthorizedFragment.class.getName(), "admin(Authorized)");
        names.put(LoginPasswordFragment.class.getName(), "admin(Login password)");

        names.put(MessageIndexFragment.class.getName(), "message(Index)");
        names.put(ComposeFragment.class.getName(), "message(Compose)");

        names.put(SettingFragment.class.getName(), "setting()");
        names.put(PermissionFragment.class.getName(), "setting(Permission)");
        names.put(ChangePasswordFragment.class.getName(), "setting(Change password)");
        names.put(SyncLogFragment.class.getName(), "setting(Sync log)");

        names.put(TaskIndexFragment.class.getName(), "task(Index)");
        names.put(TaskInfoFragment.class.getName(), "task(Info)");

        names.put(AgreeFragment.class.getName(), "deal(Соглашение)");
        names.put(ActionFragment.class.getName(), "deal(Action)");
        names.put(AttachFragment.class.getName(), "deal(Attach)");
        names.put(CommentFragment.class.getName(), "deal(Комментарий)");
        names.put(DisplayFragment.class.getName(), "deal(Оборудование)");
        names.put(DealErrorFragment.class.getName(), "deal(Error)");
        names.put(DealIndexFragment.class.getName(), "deal(Index)");
        names.put(DealOutletInfoFragment.class.getName(), "deal(Outlet info)");
        names.put(GiftFragment.class.getName(), "deal(Промо материал)");
        names.put(MemoFragment.class.getName(), "deal(Заметки)");
        names.put(NoteFragment.class.getName(), "deal(Примечание)");
        names.put(OrderFragment.class.getName(), "deal(Заказ)");
        names.put(OverloadFragment.class.getName(), "deal(Overload)");
        names.put(PhotoFragment.class.getName(), "deal(Photo)");
        names.put(PhotoInfoFragment.class.getName(), "deal(Photo info)");
        names.put(PaymentFragment.class.getName(), "deal(Payment)");
        names.put(uz.greenwhite.smartup5_trade.m_product.ui.PhotoFragment.class.getName(), "product(Photo)");
        names.put(QuizFragment.class.getName(), "deal(Опрос)");
        names.put(RetailAuditFragment.class.getName(), "deal(Розничный аудит)");
        names.put(ReturnFragment.class.getName(), "deal(Return)");
        names.put(ReturnPaymentFragment.class.getName(), "deal(Return payment)");
        names.put(ServiceFragment.class.getName(), "deal(Service)");
        names.put(StockFragment.class.getName(), "deal(Остатки)");
        names.put(TotalFragment.class.getName(), "deal(Total)");
        names.put(ViolationFragment.class.getName(), "deal(Violation)");
        names.put(DealHistoryFragment.class.getName(), "deal(History)");

        names.put(DebtorFragment.class.getName(), "debtor()");
        names.put(PrepaymentFragment.class.getName(), "debtor(Prepayment)");
        names.put(PrepaymentOutletFragment.class.getName(), "debtor(Prepayment outlet)");

        names.put(DisplayFragment.class.getName(), "display()");
        names.put(DisplayReviewFragment.class.getName(), "display(Review)");

        names.put(OutletFragment.class.getName(), "dual_visit.visit(Запланированные клиенты)");

        names.put(DutyFragment.class.getName(), "duty()");
        names.put(FilialActionFragment.class.getName(), "duty(Filial action)");
        names.put(FilialActionInfoFragment.class.getName(), "duty(Filial action info)");
        names.put(PriceFragment.class.getName(), "duty(Price)");

        names.put(AccessFragment.class.getName(), "file_manager(Access)");
        names.put(FileManagerIndex.class.getName(), "file_manager(index)");

        names.put(IncomingFragment.class.getName(), "incoming()");
        names.put(IncomingProductFragment.class.getName(), "incoming(Product)");

        names.put(LocationFragment.class.getName(), "location()");

        names.put(ModuleSettingFragment.class.getName(), "module_edit(Setting)");

        names.put(FilialMovementFragment.class.getName(), "movement(Filial movement)");
        names.put(MovementFragment.class.getName(), "movement()");

        names.put(MapFragment.class.getName(), "near(Map)");
        names.put(MapListFragment.class.getName(), "near(Map list)");
        names.put(NearOutletFragment.class.getName(), "near(Outlet)");
        names.put(NearOutletIndexFragment.class.getName(), "near(Outlet index)");
        names.put(NearOutletListFragment.class.getName(), "near(Outlet list)");

        names.put(OrderGiftFragment.class.getName(), "order_info(Gift)");
        names.put(OrderInfoFragment.class.getName(), "order_info()");
        names.put(OrderInfoIndexFragment.class.getName(), "order_info(Info index)");
        names.put(OrderOrderFragment.class.getName(), "order_info(Order)");
        names.put(OrderPaymentFragment.class.getName(), "order_info(Payment)");
        names.put(OrderPaymentFragment.class.getName(), "order_info(Payment)");
        names.put(OrderStockFragment.class.getName(), "order_info(Stock)");

        names.put(CategorizationFragment.class.getName(), "outlet(Categorization)");
        names.put(OutletDebtorFragment.class.getName(), "outlet(Debtor)");
        names.put(OutletInfoFragment.class.getName(), "outlet(Info)");
        names.put(OutletIndexFragment.class.getName(), "outlet(Index)");
        names.put(PersonFileFragment.class.getName(), "outlet(Person file)");
        names.put(ShippedFragment.class.getName(), "outlet(Shipped)");

        names.put(PersonCreateFragment.class.getName(), "person_edit(Legal person create)");
        names.put(NaturalPersonCreateFragment.class.getName(), "person_edit(Natural person create)");

        names.put(PrAddPlanFragment.class.getName(), "presentation(Add plan)");
        names.put(PrDatePlanFragment.class.getName(), "presentation(Date plan)");
        names.put(PrPresentationListFragment.class.getName(), "presentation(Presentation list)");

        names.put(FileFragment.class.getName(), "product(File)");
        names.put(PhotoFragment.class.getName(), "product(Photo)");
        names.put(ProductInfoFragment.class.getName(), "product(info)");
        names.put(ProductPhotoFullFragment.class.getName(), "product(Photo full)");

        names.put(NewReportListFragment.class.getName(), "report(New report list)");
        names.put(ReportDateFragment.class.getName(), "report(Date)");
        names.put(ReportFragment.class.getName(), "report()");
        names.put(ReportOutletFragment.class.getName(), "report(Outlet)");
        names.put(ReportSessionFragment.class.getName(), "report(Session)");
        names.put(ReportViewFragment.class.getName(), "report(View)");
        names.put(TestReportViewFragment.class.getName(), "report(Test view)");

        names.put(DebtorFragment.class.getName(), "session(Person debtor)");
        names.put(OutletFragment.class.getName(), "session(Person outlet)");
        names.put(uz.greenwhite.smartup5_trade.m_session.ui.person.ReturnFragment.class.getName(), "session(Person return)");
        names.put(uz.greenwhite.smartup5_trade.m_session.ui.person.ShippedFragment.class.getName(), "session(Person return)");
        names.put(uz.greenwhite.smartup5_trade.m_session.ui.person.TodayFragment.class.getName(), "session(Today)");
        names.put(DashboardFragment.class.getName(), "session(Dashboard)");
        names.put(SyncFragment.class.getName(), "session(Sync)");
        names.put(VisitPlanFragment.class.getName(), "session(Visit plan)");
        names.put(WarehouseFragment.class.getName(), "session(Warehouse)");
        names.put(CustomerFragment.class.getName(), "session(Customer)");

        names.put(SAttachFragment.class.getName(), "shipped(Attach)");
        names.put(SDealIndexFragment.class.getName(), "shipped(Deal index)");
        names.put(SDealNoteFragment.class.getName(), "shipped(Deal note)");
        names.put(SErrorFragment.class.getName(), "shipped(Deal error)");
        names.put(SOrderFragment.class.getName(), "shipped(Deal order)");
        names.put(SOutletInfoFragment.class.getName(), "shipped(Outlet info)");
        names.put(SOverloadFragment.class.getName(), "shipped(Overload)");
        names.put(SPaymentFragment.class.getName(), "shipped(Payment)");
        names.put(SReturnReasonFragment.class.getName(), "shipped(Return reason)");

        names.put(StocktakingFragment.class.getName(), "stocktaking()");
        names.put(StocktakingProductFragment.class.getName(), "shipped(Product)");

        names.put(TrackingFragment.class.getName(), "tracking()");
        names.put(TrackingIndexFragment.class.getName(), "tracking(Index)");
        names.put(TrackingMapFragment.class.getName(), "tracking(Map)");

        names.put(OutletVisitFragment.class.getName(), "vp(Outlet visit)");
        names.put(OutletVisitPlanFragment.class.getName(), "vp(Outlet visit plan)");

        names.put(WarehouseIndexFragment.class.getName(), "warehouse(Index)");

        names.put(SendMyLocationFragment.class.getName(), "send_my_location()");

    }

    public static String getName(Fragment f) {
        return names.get(f.getClass().getName());
    }
}
