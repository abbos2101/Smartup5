package uz.greenwhite.smartup5_trade.m_vp_outlet;// 13.12.2016

import android.support.annotation.NonNull;
import android.text.TextUtils;

import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp_outlet.ui.VPlanData;

public class VPOutletApi {

    @SuppressWarnings("ConstantConditions")
    public static void saveVisitPlan(@NonNull Scope scope, VPlanData data) {
        OutletVisitPlan visitPlan = data.vPlan.toValue();
        if (TextUtils.isEmpty(visitPlan.localId)) {
            long id = AdminApi.nextSequence();
            String localId = String.valueOf(id);
            visitPlan = visitPlan.changeLocalId(localId);
        }
        scope.entry.saveOutletVisitPlan(visitPlan);
    }
}
