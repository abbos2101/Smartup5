package uz.greenwhite.smartup5_trade.m_deal.builder;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit.DealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit.DealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Producer;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.retail_audit.RetailAuditSet;

public class BuilderRetailAudit {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealRetailAudit> initial;

    public BuilderRetailAudit(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private MyArray<DealRetailAudit> getInitial() {
        DealRetailAuditModule auditModule = dealRef.findDealModule(module.id);
        return auditModule != null ? auditModule.items : MyArray.<DealRetailAudit>emptyArray();
    }

    private MyArray<RetailAuditSet> getRetailAuditSets() {
        Set<String> auditSets = dealRef.getFilialRoleRetailAuditSetIds().asSet();

        for (DealRetailAudit audit : initial) {
            auditSets.add(audit.retailAuditSetId);
        }
        return MyArray.from(auditSets).map(new MyMapper<String, RetailAuditSet>() {
            @Override
            public RetailAuditSet apply(String retailAuditSetId) {
                return dealRef.getRetailAuditSet(retailAuditSetId);
            }
        }).filterNotNull();
    }

    private MyArray<VDealRetailAudit> makeRetailAudits(RetailAuditSet retailAuditSet) {
        MyArray<ProductPhoto> productPhotos = dealRef.getProductPhotos();
        ArrayList<VDealRetailAudit> result = new ArrayList<>();
        for (String productId : retailAuditSet.productIds) {
            RetailAuditProduct retailAuditProduct = dealRef.getRetailAuditProduct(productId);
            Product product = dealRef.findProduct(productId);

            if (product == null || retailAuditProduct == null) continue;

            Producer producer = dealRef.getProducer(product.producerId);
            Region region = null;
            if (producer != null) {
                region = dealRef.getRegion(producer.regionId);
            }

            String key = DealRetailAudit.makeKey(retailAuditSet.retailAuditSetId, productId);
            DealRetailAudit dealRetailAudit = initial.find(key, DealRetailAudit.KEY_ADAPTER);

            BigDecimal incomeQuant = null;
            BigDecimal soldQuant = null;
            BigDecimal soldPrice = null;
            BigDecimal inputPrice = null;
            BigDecimal faceQuant = null;
            BigDecimal extFaceTrayQuant = null;
            BigDecimal extFaceFridgeQuant = null;
            BigDecimal extFaceOtherQuant = null;
            boolean shelfHight = false;
            boolean shelfMedium = false;
            boolean shelfLow = false;
            BigDecimal shelfShare = null;
            boolean alreadySetted = false;

            if (dealRetailAudit != null) {
                incomeQuant = dealRetailAudit.incomeQuant;
                soldQuant = dealRetailAudit.soldQuant;

                if (!TextUtils.isEmpty(dealRetailAudit.inputPrice)) {
                    inputPrice = new BigDecimal(dealRetailAudit.inputPrice);
                }

                soldPrice = dealRetailAudit.soldPrice;
                faceQuant = dealRetailAudit.faceQuant;
                extFaceTrayQuant = dealRetailAudit.extFaceTrayQuant;
                extFaceFridgeQuant = dealRetailAudit.extFaceFridgeQuant;
                extFaceOtherQuant = dealRetailAudit.extFaceOtherQuant;
                shelfHight = dealRetailAudit.shelfPositions.contains(DealRetailAudit.HIGHT, MyMapper.<String>identity());
                shelfMedium = dealRetailAudit.shelfPositions.contains(DealRetailAudit.MEDIUM, MyMapper.<String>identity());
                shelfLow = dealRetailAudit.shelfPositions.contains(DealRetailAudit.LOW, MyMapper.<String>identity());
                shelfShare = dealRetailAudit.shelfShare;
                alreadySetted = dealRetailAudit.alreadySetted;
            }

            ProductPhoto productPhoto = productPhotos.find(productId, ProductPhoto.KEY_ADAPTER);

            result.add(new VDealRetailAudit(retailAuditProduct, product, productPhoto, incomeQuant,
                    soldQuant, soldPrice, faceQuant, extFaceTrayQuant, extFaceFridgeQuant,
                    extFaceOtherQuant, shelfHight, shelfMedium, shelfLow, shelfShare, alreadySetted, inputPrice,
                    producer, region));
        }
        return MyArray.from(result);
    }

    private MyArray<VDealRetailAuditForm> makeForms() {
        MyArray<RetailAuditSet> retailAuditSets = getRetailAuditSets();
        ArrayList<VDealRetailAuditForm> forms = new ArrayList<>();
        for (RetailAuditSet retailAuditSet : retailAuditSets) {
            MyArray<VDealRetailAudit> vDealRetailAudits = makeRetailAudits(retailAuditSet);
            if (vDealRetailAudits.isEmpty()) continue;

            forms.add(new VDealRetailAuditForm(module, retailAuditSet,
                    new ValueArray<>(vDealRetailAudits)));
        }
        return MyArray.from(forms);
    }

    public VDealRetailAuditModule build() {
        return new VDealRetailAuditModule(module, new ValueArray<>(makeForms()));
    }


}
