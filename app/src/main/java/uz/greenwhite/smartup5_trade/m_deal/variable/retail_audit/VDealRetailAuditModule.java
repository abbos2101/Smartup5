package uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit.DealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit.DealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealRetailAuditModule extends VDealModule {

    public final ValueArray<VDealRetailAuditForm> forms;

    public VDealRetailAuditModule(VisitModule module, ValueArray<VDealRetailAuditForm> forms) {
        super(module);
        this.forms = forms;
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return forms.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        return forms.getItems().contains(new MyPredicate<VDealRetailAuditForm>() {
            @Override
            public boolean apply(VDealRetailAuditForm vDealRetailAuditForm) {
                return vDealRetailAuditForm.hasValue();
            }
        });
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<DealRetailAudit> result = new ArrayList<>();
        for (VDealRetailAuditForm form : forms.getItems()) {
            for (VDealRetailAudit val : form.retailAudits.getItems()) {
                if (val.hasValue()) {
                    BigDecimal incomeQuant = val.incomeQuant.getQuantity();

                    BigDecimal soldQuant = val.soldQuant.getQuantity();
                    BigDecimal soldPrice = val.soldPrice.getQuantity();
                    String inputPrice = Util.nvl(String.valueOf(val.inputPrice.getQuantity()));
                    BigDecimal faceQuant = val.faceQuant.getQuantity();
                    BigDecimal extFaceTrayQuant = val.extFaceTrayQuant.getQuantity();
                    BigDecimal extFaceFridgeQuant = val.extFaceFridgeQuant.getQuantity();
                    BigDecimal extFaceOtherQuant = val.extFaceOtherQuant.getQuantity();

                    MyArray<String> shelfSharePosition = MyArray.from(val.shelfHight.getValue() ? DealRetailAudit.HIGHT : null,
                            val.shelfMedium.getValue() ? DealRetailAudit.MEDIUM : null,
                            val.shelfLow.getValue() ? DealRetailAudit.LOW : null).filterNotNull();

                    BigDecimal shelfShare = val.shelfShare.getQuantity();
                    boolean alreadySetted = val.alreadySetted;

                    BigDecimal stockQuant = val.getExtraFacingQuantity();

                    result.add(new DealRetailAudit(form.retailAuditSet.retailAuditSetId, val.product.id,
                            incomeQuant, stockQuant, soldQuant, soldPrice, faceQuant, extFaceTrayQuant,
                            extFaceFridgeQuant, extFaceOtherQuant, shelfSharePosition, shelfShare, alreadySetted, inputPrice));
                }
            }
        }
        return new DealRetailAuditModule(MyArray.from(result));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return forms.getItems().toSuper();
    }
}
