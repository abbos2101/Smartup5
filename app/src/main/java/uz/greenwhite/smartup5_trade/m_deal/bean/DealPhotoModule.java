package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealPhotoModule extends DealModule {

    public final MyArray<DealPhoto> photos;

    public DealPhotoModule(MyArray<DealPhoto> orders) {
        super(VisitModule.M_PHOTO);
        this.photos = orders;
        this.photos.checkUniqueness(DealPhoto.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealPhotoModule(in.readArray(DealPhoto.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealPhotoModule v = (DealPhotoModule) val;
            out.write(v.photos, DealPhoto.UZUM_ADAPTER);
        }
    };
}
