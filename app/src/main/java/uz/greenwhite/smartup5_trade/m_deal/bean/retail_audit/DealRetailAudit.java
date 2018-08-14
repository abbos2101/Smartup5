package uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.Utils;

public class DealRetailAudit {

    public static final String HIGHT = "H";
    public static final String MEDIUM = "M";
    public static final String LOW = "L";

    public final String retailAuditSetId;
    public final String productId;
    public final BigDecimal incomeQuant;
    public final BigDecimal stockQuant;
    public final BigDecimal soldQuant;
    public final BigDecimal soldPrice;
    public final BigDecimal faceQuant;
    public final BigDecimal extFaceTrayQuant;
    public final BigDecimal extFaceFridgeQuant;
    public final BigDecimal extFaceOtherQuant;
    public final MyArray<String> shelfPositions;             // (H)igh, (M)edium, (L)ow
    public final BigDecimal shelfShare;                      // In percents
    public final boolean alreadySetted;                      // qayta redaktirovatga kirganida yana kiritip koymasligi uchun
    public final String inputPrice;

    public DealRetailAudit(String retailAuditSetId,
                           String productId,
                           BigDecimal incomeQuant,
                           BigDecimal stockQuant,
                           BigDecimal soldQuant,
                           BigDecimal soldPrice,
                           BigDecimal faceQuant,
                           BigDecimal extFaceTrayQuant,
                           BigDecimal extFaceFridgeQuant,
                           BigDecimal extFaceOtherQuant,
                           MyArray<String> shelfPositions,
                           BigDecimal shelfShare,
                           Boolean alreadySetted,
                           String inputPrice) {
        this.retailAuditSetId = retailAuditSetId;
        this.productId = productId;
        this.incomeQuant = incomeQuant;
        this.stockQuant = stockQuant;
        this.soldQuant = soldQuant;
        this.soldPrice = soldPrice;
        this.faceQuant = faceQuant;
        this.extFaceTrayQuant = extFaceTrayQuant;
        this.extFaceFridgeQuant = extFaceFridgeQuant;
        this.extFaceOtherQuant = extFaceOtherQuant;
        this.shelfPositions = shelfPositions;
        this.shelfShare = shelfShare;
        this.alreadySetted = Util.nvl(alreadySetted, false);
        this.inputPrice = Util.nvl(inputPrice);
    }

    public static String makeKey(String retailAuditSetId, String productId) {
        return retailAuditSetId + "#" + productId;
    }

    public static final MyMapper<DealRetailAudit, String> KEY_ADAPTER = new MyMapper<DealRetailAudit, String>() {
        @Override
        public String apply(DealRetailAudit val) {
            return makeKey(val.retailAuditSetId, val.productId);
        }
    };

    public static final UzumAdapter<DealRetailAudit> UZUM_ADAPTER = new UzumAdapter<DealRetailAudit>() {
        @Override
        public DealRetailAudit read(UzumReader in) {
            return new DealRetailAudit(in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readValue(STRING_ARRAY), in.readBigDecimal(),
                    in.readBoolean(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealRetailAudit val) {
            out.write(val.retailAuditSetId);
            out.write(val.productId);
            out.write(val.incomeQuant);
            out.write(val.stockQuant);
            out.write(val.soldQuant);
            out.write(val.soldPrice);
            out.write(val.faceQuant);
            out.write(val.extFaceTrayQuant);
            out.write(val.extFaceFridgeQuant);
            out.write(val.extFaceOtherQuant);
            out.write(val.shelfPositions, STRING_ARRAY);
            out.write(val.shelfShare);
            out.write(val.alreadySetted);
            out.write(val.inputPrice);
        }
    };
}
