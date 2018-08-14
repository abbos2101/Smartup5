package uz.greenwhite.smartup5_trade.m_deal.variable;// 30.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public abstract class VDealModule extends VModule {

    public final int iconRes;

    protected VDealModule(VisitModule module) {
        super(module);
        AppError.checkNull(module);

        this.iconRes = module.getIconResId();
    }

    public abstract DealModule convertToDealModule();

    @Override
    public int getModuleId() {
        return ((VisitModule) tag).id;
    }

    @Override
    public int getIconResId() {
        return iconRes;
    }

    public CharSequence getTitle() {
        return ((VisitModule) tag).name;
    }

    @Override
    public boolean isMandatory() {
        return ((VisitModule) tag).mandatory;
    }

    public final boolean isReady() {
        return !((VisitModule) tag).mandatory || hasValue();
    }

    public ErrorResult getMandatoryError() {
        if (!isReady()) {
            return ErrorResult.make(DS.getString(R.string.deal_module_is_mandatory, ((VisitModule) tag).name));
        }
        return ErrorResult.NONE;
    }

    public static final MyMapper<VDealModule, Integer> KEY_ADAPTER = new MyMapper<VDealModule, Integer>() {
        @Override
        public Integer apply(VDealModule module) {
            return module.getModuleId();
        }
    };
}
