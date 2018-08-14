package uz.greenwhite.smartup5_trade.m_person_edit.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgRegion extends ArgPerson {

    public final String regionId;

    public ArgRegion(ArgPerson arg, String regionId) {
        super(arg, arg.personId, arg.roomId, arg.personKind);
        this.regionId = regionId;
    }

    protected ArgRegion(UzumReader in) {
        super(in);
        this.regionId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.regionId);
    }

    public static final UzumAdapter<ArgRegion> UZUM_ADAPTER = new UzumAdapter<ArgRegion>() {
        @Override
        public ArgRegion read(UzumReader uzumReader) {
            return new ArgRegion(uzumReader);
        }

        @Override
        public void write(UzumWriter uzumWriter, ArgRegion argRegion) {
            argRegion.write(uzumWriter);
        }
    };
}
