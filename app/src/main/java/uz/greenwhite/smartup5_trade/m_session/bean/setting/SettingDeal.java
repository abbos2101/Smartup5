package uz.greenwhite.smartup5_trade.m_session.bean.setting;// 31.08.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SettingDeal {

    public final Boolean multiAccounts;
    public final Boolean consignment;
    public final Boolean visitAllow;
    public final Integer recomDay;
    public final String recomCoef;
    public final Boolean returnAllow;
    public final Boolean deliveryDateAllow;
    public final Boolean selectExpeditor;
    public final Boolean mml;
    public final Boolean allowDealDraft;
    public final Boolean showWarehouseBalance;
    public final Boolean completeDealNew;
    public final Boolean allowDealFast;
    public final Boolean requiredDeliveryDate;
    public final Boolean onlyHasBalance;
    public final Boolean allowDealArchive;

    @Deprecated
    public final Boolean dealPhotoWatermark;

    public SettingDeal(Boolean multiAccounts,
                       Boolean consignment,
                       Boolean dealPhotoWatermark,
                       Boolean visitAllow,
                       Integer recomDay,
                       String recomCoef,
                       Boolean returnAllow,
                       Boolean deliveryDateAllow,
                       Boolean selectExpeditor,
                       Boolean mml,
                       Boolean allowDealDraft,
                       Boolean showWarehouseBalance,
                       Boolean completeDealNew,
                       Boolean allowDealFast,
                       Boolean requiredDeliveryDate,
                       Boolean onlyHasBalance,
                       Boolean allowDealArchive) {
        this.multiAccounts = multiAccounts;
        this.consignment = consignment;
        this.dealPhotoWatermark = dealPhotoWatermark;
        this.visitAllow = visitAllow;
        this.recomDay = recomDay;
        this.recomCoef = recomCoef;
        this.returnAllow = returnAllow;
        this.deliveryDateAllow = deliveryDateAllow;
        this.selectExpeditor = selectExpeditor;
        this.mml = mml;
        this.allowDealDraft = allowDealDraft;
        this.showWarehouseBalance = showWarehouseBalance;
        this.completeDealNew = completeDealNew;
        this.allowDealFast = allowDealFast;
        this.requiredDeliveryDate = requiredDeliveryDate;
        this.onlyHasBalance = onlyHasBalance;
        this.allowDealArchive = allowDealArchive;
    }

    public boolean nonEmpty() {
        return multiAccounts != null &&
                dealPhotoWatermark != null &&
                consignment != null &&
                visitAllow != null &&
                recomDay != null &&
                recomCoef != null &&
                returnAllow != null &&
                deliveryDateAllow != null &&
                selectExpeditor != null &&
                mml != null &&
                allowDealDraft != null &&
                showWarehouseBalance != null &&
                completeDealNew != null &&
                allowDealFast != null &&
                requiredDeliveryDate != null &&
                onlyHasBalance != null &&
                allowDealArchive != null;
    }

    public static SettingDeal withParent(SettingDeal setting, SettingDeal parent) {
        if (setting != null) {
            return setting.withParent(parent);
        }
        return parent;
    }

    public SettingDeal withParent(SettingDeal parent) {
        if (nonEmpty()) {
            return this;
        }
        return new SettingDeal(
                Util.nvl(multiAccounts, parent.multiAccounts),
                Util.nvl(consignment, parent.consignment),
                Util.nvl(dealPhotoWatermark, parent.dealPhotoWatermark),
                Util.nvl(visitAllow, parent.visitAllow),
                Util.nvl(recomDay, parent.recomDay),
                Util.nvl(recomCoef, parent.recomCoef),
                Util.nvl(returnAllow, parent.returnAllow),
                Util.nvl(deliveryDateAllow, parent.deliveryDateAllow),
                Util.nvl(selectExpeditor, parent.selectExpeditor),
                Util.nvl(mml, parent.mml),
                Util.nvl(allowDealDraft, parent.allowDealDraft),
                Util.nvl(showWarehouseBalance, parent.showWarehouseBalance),
                Util.nvl(completeDealNew, parent.completeDealNew),
                Util.nvl(allowDealFast, parent.allowDealFast),
                Util.nvl(requiredDeliveryDate, parent.requiredDeliveryDate),
                Util.nvl(onlyHasBalance, parent.onlyHasBalance),
                Util.nvl(allowDealArchive, parent.allowDealArchive));
    }

    public static final UzumAdapter<SettingDeal> UZUM_ADAPTER = new UzumAdapter<SettingDeal>() {
        @Override
        public SettingDeal read(UzumReader in) {
            return new SettingDeal(in.readBoolean(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readInteger(), in.readString(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean(), in.readBoolean(),
                    in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, SettingDeal val) {
            throw AppError.Unsupported();
        }
    };

}
