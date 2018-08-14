package uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Producer;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditProduct;

public class VDealRetailAudit extends VariableLike {

    public final RetailAuditProduct retailAuditProduct;
    public final Product product;
    @Nullable
    public final Producer producer;
    @Nullable
    public final Region productRegion;
    @Nullable
    public final ProductPhoto productPhoto;
    public final ValueBigDecimal incomeQuant;
    public final ValueBigDecimal soldQuant;
    public final ValueBigDecimal soldPrice;
    public final ValueBigDecimal inputPrice;
    public final ValueBigDecimal faceQuant;
    public final ValueBigDecimal extFaceTrayQuant;
    public final ValueBigDecimal extFaceFridgeQuant;
    public final ValueBigDecimal extFaceOtherQuant;
    public final ValueBoolean shelfHight;
    public final ValueBoolean shelfMedium;
    public final ValueBoolean shelfLow;
    public final ValueBigDecimal shelfShare;                 // In percents
    public boolean alreadySetted;                            // qayta redaktirovatga kirganida yana kiritip koymasligi uchun

    public VDealRetailAudit(RetailAuditProduct retailAuditProduct,
                            Product product,
                            @Nullable ProductPhoto productPhoto,
                            BigDecimal incomeQuant,
                            BigDecimal soldQuant,
                            BigDecimal soldPrice,
                            BigDecimal faceQuant,
                            BigDecimal extFaceTrayQuant,
                            BigDecimal extFaceFridgeQuant,
                            BigDecimal extFaceOtherQuant,
                            boolean shelfHight,
                            boolean shelfMedium,
                            boolean shelfLow,
                            BigDecimal shelfShare,
                            boolean alreadySetted,
                            BigDecimal inputPrice,
                            @Nullable Producer producer,
                            @Nullable Region productRegion) {
        this.alreadySetted = alreadySetted;

        this.retailAuditProduct = retailAuditProduct;
        this.product = product;
        this.producer = producer;
        this.productRegion = productRegion;
        this.productPhoto = productPhoto;

        this.incomeQuant = new ValueBigDecimal(20, 6);
        this.soldQuant = new ValueBigDecimal(20, 6);
        this.soldPrice = new ValueBigDecimal(20, 6);
        this.inputPrice = new ValueBigDecimal(20, 6);
        this.faceQuant = new ValueBigDecimal(10, 3);
        this.extFaceTrayQuant = new ValueBigDecimal(10, 3);
        this.extFaceFridgeQuant = new ValueBigDecimal(10, 3);
        this.extFaceOtherQuant = new ValueBigDecimal(10, 3);
        this.shelfHight = new ValueBoolean(shelfHight);
        this.shelfMedium = new ValueBoolean(shelfMedium);
        this.shelfLow = new ValueBoolean(shelfLow);
        this.shelfShare = new ValueBigDecimal(3, 0);

        this.incomeQuant.setValue(incomeQuant);
        this.soldQuant.setValue(soldQuant);
        this.soldPrice.setValue(soldPrice);
        this.inputPrice.setValue(inputPrice);
        this.faceQuant.setValue(faceQuant);
        this.extFaceTrayQuant.setValue(extFaceTrayQuant);
        this.extFaceFridgeQuant.setValue(extFaceFridgeQuant);
        this.extFaceOtherQuant.setValue(extFaceOtherQuant);
        this.shelfShare.setValue(shelfShare);

        if (this.incomeQuant.isZero()) this.incomeQuant.setValue(null);
        if (this.soldQuant.isZero()) this.soldQuant.setValue(null);
        if (this.soldPrice.isZero()) this.soldPrice.setValue(null);
        if (this.faceQuant.isZero()) this.faceQuant.setValue(null);
        if (this.extFaceTrayQuant.isZero()) this.extFaceTrayQuant.setValue(null);
        if (this.extFaceFridgeQuant.isZero()) this.extFaceFridgeQuant.setValue(null);
        if (this.extFaceOtherQuant.isZero()) this.extFaceOtherQuant.setValue(null);
        if (this.shelfShare.isZero()) this.shelfShare.setValue(null);
    }

    public boolean hasExtraFacingWithoutOthers() {
        return faceQuant.nonZero() || extFaceTrayQuant.nonZero() || extFaceFridgeQuant.nonZero();
    }

    public BigDecimal getExtraFacingQuantity() {
        return faceQuant.getQuantity().add(extFaceTrayQuant.getQuantity())
                .add(extFaceFridgeQuant.getQuantity()).add(extFaceOtherQuant.getQuantity());
    }

    public boolean hasValue() {
        return incomeQuant.nonZero() || soldQuant.nonZero() || soldPrice.nonZero() || inputPrice.nonZero() ||
                faceQuant.nonZero() || extFaceTrayQuant.nonZero() || extFaceFridgeQuant.nonZero() ||
                extFaceOtherQuant.nonZero() || shelfShare.nonZero() || shelfHight.getValue() ||
                shelfMedium.getValue() || shelfLow.getValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(incomeQuant, soldQuant, soldPrice, faceQuant, extFaceTrayQuant,
                extFaceFridgeQuant, extFaceOtherQuant, shelfHight, shelfMedium, shelfLow, shelfShare, inputPrice).toSuper();
    }

    public static final MyMapper<VDealRetailAudit, String> KEY_ADAPTER = new MyMapper<VDealRetailAudit, String>() {
        @Override
        public String apply(VDealRetailAudit vDealRetailAudit) {
            return vDealRetailAudit.product.id;
        }
    };
}
