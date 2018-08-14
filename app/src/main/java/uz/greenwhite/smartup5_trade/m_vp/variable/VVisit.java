package uz.greenwhite.smartup5_trade.m_vp.variable;// 23.09.2016

import android.text.TextUtils;

import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;

public class VVisit extends VariableLike {

    public final String accountId;
    public final String filialId;
    public final String roomId;
    public final String date;
    public final ValueArray<VOutlet> outlets;

    public VVisit(String accountId, String filialId, String roomId, String date, ValueArray<VOutlet> outlets) {
        this.accountId = accountId;
        this.filialId = filialId;
        this.roomId = roomId;
        this.date = date;
        this.outlets = outlets;
    }

    public MyArray<OutletPlan> convert(Scope scope) {
        ArrayList<OutletPlan> r = new ArrayList<>();
        for (VOutlet vOutlet : this.outlets.getItems()) {
            String entryId = vOutlet.visit.localId;
            boolean created = vOutlet.visit.created;
            boolean entryIdEmpty = TextUtils.isEmpty(entryId);

            if ((created && vOutlet.check.getValue()) ||
                    (!created && !vOutlet.check.getValue())) {
                if (!entryIdEmpty) {
                    scope.ds.db.entryDelete(entryId);
                }
                continue;
            }

            if (entryIdEmpty) {
                entryId = String.valueOf(AdminApi.nextSequence());
            }

            String visitDate = vOutlet.check.getValue() ? date : OutletPlan.makeDeleteDate(date);

            r.add(new OutletPlan(filialId, vOutlet.outlet.id, visitDate, entryId, created, roomId));
        }
        return DSUtil.removeOutletPlanDuplicate(MyArray.from(r));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return outlets.getItems().toSuper();
    }
}
