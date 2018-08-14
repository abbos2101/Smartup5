package uz.greenwhite.smartup5_trade.m_shipped.variable;// 09.09.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public abstract class VSDealModule extends VModule {

    public final int iconRes;

    protected VSDealModule(VisitModule module) {
        super(module);
        AppError.checkNull(module);
        this.iconRes = module.getIconResId();
    }

    public CharSequence getTitle() {
        return ((VisitModule) tag).name;
    }

    @Override
    public int getModuleId() {
        return ((VisitModule) tag).id;
    }

    @Override
    public int getIconResId() {
        return iconRes;
    }
}
