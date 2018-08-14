package uz.greenwhite.smartup5_trade.m_tracking.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class TrackingUser {

    public final String id;
    public final String name;
    public final String photoSha;
    public final String role;
    public final String managerName;

    public TrackingUser(String id,
                        String name,
                        String photoSha,
                        String role,
                        String managerName) {
        this.id = id;
        this.name = name;
        this.photoSha = photoSha;
        this.role = role;
        this.managerName = managerName;
    }

    public CharSequence getDetail() {
        return UI.html()
                .v(DS.getString(R.string.tracking_role, role)).br()
                .v(DS.getString(R.string.tracking_manager, managerName)).html();
    }

    public static final MyMapper<TrackingUser, String> KEY_ADAPTER = new MyMapper<TrackingUser, String>() {
        @Override
        public String apply(TrackingUser val) {
            return val.id;
        }
    };

    public static final UzumAdapter<TrackingUser> UZUM_ADAPTER = new UzumAdapter<TrackingUser>() {
        @Override
        public TrackingUser read(UzumReader in) {
            return new TrackingUser(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(),in.readString());
        }

        @Override
        public void write(UzumWriter out, TrackingUser val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.photoSha);
            out.write(val.role);
            out.write(val.managerName);
        }
    };
}
