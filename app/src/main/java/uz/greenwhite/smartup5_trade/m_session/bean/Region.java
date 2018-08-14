package uz.greenwhite.smartup5_trade.m_session.bean;// 20.12.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Region {

    public final String regionId;
    public final String name;
    public final String parentId;

    public Region(String regionId,
                  String name,
                  String parentId) {
        this.regionId = regionId;
        this.name = name;
        this.parentId = parentId;
    }

    public static final Region DEFAULT = new Region("", "", "");

    public static final MyMapper<Region, String> KEY_ADAPTER = new MyMapper<Region, String>() {
        @Override
        public String apply(Region region) {
            return region.regionId;
        }
    };

    public static final UzumAdapter<Region> UZUM_ADAPTER = new UzumAdapter<Region>() {
        @Override
        public Region read(UzumReader in) {
            return new Region(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Region val) {
            out.write(val.regionId);
            out.write(val.name);
            out.write(val.parentId);
        }
    };
}
