package uz.greenwhite.smartup5_trade.m_deal.builder;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPhotoModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhotoForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhotoModule;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderPhoto {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealPhoto> initial;

    public BuilderPhoto(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    public MyArray<DealPhoto> getInitial() {
        DealPhotoModule module = dealRef.findDealModule(this.module.id);
        if (module != null) {
            return module.photos;
        }
        return MyArray.emptyArray();
    }

    private VDealPhotoForm makeForm() {
        MyArray<PhotoType> photoTypes = dealRef.getPhotoType();
        if (photoTypes.isEmpty()) {
            return null;
        }
        final MyArray<SpinnerOption> options = photoTypes.map(new MyMapper<PhotoType, SpinnerOption>() {
            @Override
            public SpinnerOption apply(PhotoType photoType) {
                return new SpinnerOption(photoType.typeId, photoType.name, photoType);
            }
        });
        MyArray<VDealPhoto> vDealPhotos = initial.map(new MyMapper<DealPhoto, VDealPhoto>() {
            @Override
            public VDealPhoto apply(DealPhoto d) {
                SpinnerOption option = options.find(String.valueOf(d.typeId), SpinnerOption.KEY_ADAPTER);
                if (option != null) {
                    ValueSpinner photoType = new ValueSpinner(options, option);
                    return new VDealPhoto(d.sha, photoType, new ValueString(200, d.note), d.date, d.latLng);
                }
                return null;
            }
        }).filterNotNull();
        return new VDealPhotoForm(module, new ValueArray<>(vDealPhotos), dealRef);
    }

    public VDealPhotoModule build() {
        return new VDealPhotoModule(module, makeForm());
    }
}
