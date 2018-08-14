package uz.greenwhite.smartup5_trade.m_vp.builder;// 23.09.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.SessionUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.DoctorHospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_vp.ArgVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp.variable.VOutlet;
import uz.greenwhite.smartup5_trade.m_vp.variable.VVisit;

public class BuilderVisit {

    public static String stringify(final VVisit vDeal) {
        MyArray<OutletPlan> result = vDeal.outlets.getItems().map(new MyMapper<VOutlet, OutletPlan>() {
            @Override
            public OutletPlan apply(VOutlet vOutlet) {
                return new OutletPlan(
                        vDeal.filialId,
                        vOutlet.outlet.id,
                        vDeal.date,
                        vOutlet.visit.localId,
                        vOutlet.visit.created,
                        vOutlet.visit.roomId
                );
            }
        });
        return Uzum.toJson(result, OutletPlan.UZUM_ADAPTER.toArray());
    }

    public static VVisit make(final ArgVisitPlan arg) {
        final Scope scope = arg.getScope();
        final MyArray<OutletPlan> visits = DSUtil.getOutletVisits(scope);

        MyArray<VOutlet> result = DSUtil.getRoomOutlets(scope, arg.roomId)
                .map(new MyMapper<Outlet, VOutlet>() {
                    @Override
                    public VOutlet apply(final Outlet outlet) {
                        OutletPlan outletPlan = visits.findFirst(new MyPredicate<OutletPlan>() {
                            @Override
                            public boolean apply(OutletPlan outletPlan) {
                                return (outletPlan.roomId.equals(arg.roomId) || TextUtils.isEmpty(outletPlan.roomId)) &&
                                        outletPlan.outletId.equals(outlet.id) &&
                                        outletPlan.date.equals(arg.date);
                            }
                        });
                        if (outletPlan == null) {
                            final String argumentDate = OutletPlan.makeDeleteDate(arg.date);
                            outletPlan = visits.findFirst(new MyPredicate<OutletPlan>() {
                                @Override
                                public boolean apply(OutletPlan outletPlan) {
                                    return (outletPlan.roomId.equals(arg.roomId) || TextUtils.isEmpty(outletPlan.roomId)) &&
                                            outletPlan.outletId.equals(outlet.id) &&
                                            outletPlan.date.equals(argumentDate);
                                }
                            });
                        }
                        boolean check = false;
                        if (outletPlan != null) {
                            check = !outletPlan.date.startsWith("d");
                        }

                        if (outlet.isDoctor()) {
                            MyArray<OutletType> outletTypes = scope.ref.getOutletTypes();
                            OutletType outletType = outletTypes.find(((OutletDoctor) outlet).specialityId, OutletType.KEY_ADAPTER);
                            DoctorHospital hospital = SessionUtil.getHospital(scope, ((OutletDoctor) outlet).legalPersonId);
                            CharSequence detail = outlet.address;

                            if (hospital != null) {
                                if (!TextUtils.isEmpty(hospital.shortName)) {
                                    detail = ((String) detail).concat("\n" + hospital.shortName);
                                } else {
                                    detail = ((String) detail).concat("\n" + hospital.name);
                                }
                            }

                            if (outletType != null && !TextUtils.isEmpty(outletType.name)) {
                                detail = ((String) detail).concat("\n" + outletType.name);
                            }

                            return new VOutlet(outletPlan, outlet, check, detail);
                        } else {
                            return new VOutlet(outletPlan, outlet, check, null);
                        }
                    }
                });
        ValueArray<VOutlet> vOutlets = new ValueArray<>(result);
        vOutlets.readyToChange();
        return new VVisit(arg.accountId, arg.filialId, arg.roomId, arg.date, vOutlets);
    }
}
