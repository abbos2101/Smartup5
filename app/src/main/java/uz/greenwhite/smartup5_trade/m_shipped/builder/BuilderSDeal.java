package uz.greenwhite.smartup5_trade.m_shipped.builder;// 09.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.overload.VSOverloadModule;

public class BuilderSDeal {

    public static String stringify(VSDeal vDeal) {
        SDealHolder old = vDeal.sDealRef.holder;
        SDeal sDeal = vDeal.convertToSDeal();
        SDealHolder dealHolder = new SDealHolder(old.entryId, sDeal, old.entryState);
        return Uzum.toJson(dealHolder, SDealHolder.UZUM_ADAPTER);
    }

    public static VSDeal make(Scope scope, SDealHolder dealHolder, String dealId) {
        SDealRef dealRef = new SDealRef(scope, dealHolder, dealId);
        ValueArray<VSDealModule> modules = makeModules(dealRef);
        return new VSDeal(dealRef, modules);
    }

    public static ValueArray<VSDealModule> makeModules(final SDealRef dealRef) {
        MyArray<VSDealModule> result = MyArray.from(
                new BuilderSOrder(dealRef).build(),
                new VSOverloadModule(dealRef.sDeal.overload),
                new BuilderSPayment(dealRef).build(),
                new BuilderSReturnReason(dealRef).build(),
                new BuilderSAttach(dealRef).build()
        ).filterNotNull();
        return new ValueArray<>(result);
    }
}
