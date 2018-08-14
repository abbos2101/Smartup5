package uz.greenwhite.smartup5_trade;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory;
import uz.greenwhite.smartup5_trade.m_session.bean.person.PersonLastDebt;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;

public class NotificationUtil {

    public static final int RADIUS = 200;   // m.
    public static final int CONSIGNMENT_MIN_DATE = 1;   // d.
    public static final float PREPAYMENT_MIN_AMOUNT = 100.000F;   // base currency.

    //============================= KEYS ======================================
    public static final int KEY_NOTIFICATION_DEBTOR = 11;
    public static final int KEY_NOTIFICATION_SALES = 22;
    public static final int KEY_NOTIFICATION_PREPAYMENT = 33;
    public static final int KEY_NOTIFICATION_CONSIGN = 44;
    public static final int KEY_NOTIFICATION_NOT_VISITED = 55;
    public static final int KEY_NOTIFICATION_OUT_OF_TERRITORY = 66;
    public static final int KEY_NOTIFICATION_OUT_OF_NOT_ACB = 77;
    public static final int KEY_NOTIFICATION_UPDATE = 88;

    /**
     * Method to show update dialog
     */
    public static void notifyUpdate(Context mContext, String appUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appUrl));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle(DS.getString(R.string.update_available))
                .setSmallIcon(R.drawable.smartup_logo_black)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(DS.getString(R.string.update_message)))
                .build();

        NotificationManager nManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nManager != null) {
            nManager.notify(KEY_NOTIFICATION_UPDATE, notification);
        }
    }

    /**
     * Method to check internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static void notifyUser(Context context, Scope scope, Location location) {
        final TradeRoleKeys roleKeys = scope.ref.getRoleKeys();
        MyArray<Role> filialRoles = DSUtil.getFilialRoles(scope);

        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MyArray<Outlet> filialOutlets = DSUtil.getFilialOutlets(scope);
        MyArray<Outlet> filteredOutlets = filialOutlets.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                if (outlet.hasLocation()) {
                    LatLng outletLatLng = LocationUtil.convertStringToLatLng(outlet.latLng);
                    return outletLatLng != null && NearMapUtil.distanceBetweenInMeter(latLng, outletLatLng) < RADIUS;
                } else return false;
            }
        });

        if (filteredOutlets.nonEmpty()) {

            if (containsRole(filialRoles, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.supervisor)) {
//            todo show not-ACB notification;
            }

            if (containsRole(filialRoles, roleKeys.billCollector)) {
                final MyArray<PersonBalanceReceivable> balanceReceivables = scope.ref.getPersonBalanceReceivables();
                MyArray<Outlet> debtorOutlets = filteredOutlets.filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        PersonBalanceReceivable balanceReceivable = balanceReceivables
                                .find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER);
                        return balanceReceivable != null && balanceReceivable.amount.floatValue() < 0f;                          // if balance is minus
                    }
                });

                if (debtorOutlets.nonEmpty()) {
                    MyArray<String> outletIds = debtorOutlets.map(new MyMapper<Outlet, String>() {
                        @Override
                        public String apply(Outlet outlet) {
                            return outlet.id;
                        }
                    });
                    showNotification(context, KEY_NOTIFICATION_DEBTOR, "Debtors",
                            "There are debtor outlets near you. " +
                                    "Check them out", outletIds);
                }

            }

            if (containsRole(filialRoles, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.supervisor)) {
//            todo show Sales notification;

                final MyArray<DealHistory> dealHistories = scope.ref.getDealHistories();
                filialOutlets.filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(final Outlet outlet) {
                        MyArray<DealHistory> outletDealHistories = dealHistories.filter(new MyPredicate<DealHistory>() {
                            @Override
                            public boolean apply(DealHistory dealHistory) {
                                return dealHistory.personId.equals(outlet.id);
                            }
                        });

                        outletDealHistories.findFirst(new MyPredicate<DealHistory>() {
                            @Override
                            public boolean apply(DealHistory dealHistory) {
                                return false;
                            }
                        });

                        return false;
                    }
                });
            }

            if (containsRole(filialRoles, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.billCollector)) {

                final MyArray<PersonBalanceReceivable> balanceReceivables = scope.ref.getPersonBalanceReceivables();
                MyArray<Outlet> outletsWithLowPrepayment = filteredOutlets.filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        PersonBalanceReceivable balanceReceivable = balanceReceivables
                                .find(outlet.id, PersonBalanceReceivable.KEY_ADAPTER);

                        if (balanceReceivable == null) return false;
                        float b = balanceReceivable.amount.floatValue();
                        return 0 < b && b < PREPAYMENT_MIN_AMOUNT;
                    }
                }).filterNotNull();

                if (outletsWithLowPrepayment.nonEmpty()) {
                    MyArray<String> outletIds = outletsWithLowPrepayment.map(new MyMapper<Outlet, String>() {
                        @Override
                        public String apply(Outlet outlet) {
                            return outlet.id;
                        }
                    });

                    showNotification(context, KEY_NOTIFICATION_PREPAYMENT, "Low prepayment",
                            "There are outlets who will be out of prepayment soon. " +
                                    "Check them out", outletIds);
                }

                final MyArray<PersonLastDebt> personLastDebts = scope.ref.getPersonLastDebts();
                MyArray<Outlet> outletsWithNearConsignmentDate = filteredOutlets.filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(final Outlet outlet) {
                        PersonLastDebt lastDebt = personLastDebts.findFirst(new MyPredicate<PersonLastDebt>() {
                            @Override
                            public boolean apply(PersonLastDebt personLastDebt) {
                                return personLastDebt.personId.equals(outlet.id);
                            }
                        });

                        if (lastDebt == null) return false;

                        Integer expireDate = Integer.parseInt(DateUtil.convert(lastDebt.expireDate, DateUtil.FORMAT_AS_NUMBER));
                        Integer today = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
                        int v = expireDate - today;

                        return v > 0 && v <= CONSIGNMENT_MIN_DATE;
                    }
                }).filterNotNull();

                if (outletsWithNearConsignmentDate.nonEmpty()) {
                    MyArray<String> outletIds = outletsWithNearConsignmentDate.map(new MyMapper<Outlet, String>() {
                        @Override
                        public String apply(Outlet outlet) {
                            return outlet.id;
                        }
                    });

                    showNotification(context, KEY_NOTIFICATION_CONSIGN, "Near consignment date",
                            "There are outlets whose consignment date is reaching an end. " +
                                    "Check them out", outletIds);
                }

            }

            if (containsRole(filialRoles, roleKeys.agent, roleKeys.agentMerchandiser, roleKeys.supervisor, roleKeys.merchandiser)) {
//            todo show not-Visited notification;
//            todo out-of-location notification;
//            todo geofencing;
            }

        }
    }

    private static boolean containsRole(MyArray<Role> filialRoles, String... pCodes) {
        final MyArray<String> pCodeIds = MyArray.from(pCodes);

        return filialRoles.filter(new MyPredicate<Role>() {
            @Override
            public boolean apply(final Role role) {
                return pCodeIds.contains(new MyPredicate<String>() {
                    @Override
                    public boolean apply(String pCode) {
                        return pCode.equals(role.pCode);
                    }
                });
            }
        }).nonEmpty();
    }

    public static void showNotification(Context context, int notificationKey, String title, @Nullable String message, MyArray<String> arg) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        todo open outlet fragment with specified outlet ids
        intent.putExtra("args", arg.mkString(","));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.smartup_logo_black)
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(message)) {
            builder.setContentText(message);
        }

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nManager != null) {
            try {
                nManager.notify(notificationKey, builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
