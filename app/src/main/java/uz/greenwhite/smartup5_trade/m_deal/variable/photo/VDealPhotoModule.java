package uz.greenwhite.smartup5_trade.m_deal.variable.photo;// 30.06.2016

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPhotoModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealPhotoModule extends VDealModule {

    public final VDealPhotoForm form;

    public VDealPhotoModule(VisitModule module, VDealPhotoForm form) {
        super(module);
        this.form = form;
    }


    @Override
    public MyArray<VForm> getModuleForms() {
        if (form == null) {
            return MyArray.emptyArray();
        }
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form != null && form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        if (form != null) return MyArray.from(form).toSuper();
        return MyArray.emptyArray();
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealPhoto> result = new ArrayList<>();
        if (form != null) {
            for (VDealPhoto p : form.photos.getItems()) {
                result.add(new DealPhoto(p.sha, p.date, p.photoType.getText(), p.latLng, p.note.getText()));
            }
        }
        return new DealPhotoModule(MyArray.from(result));
    }
}
