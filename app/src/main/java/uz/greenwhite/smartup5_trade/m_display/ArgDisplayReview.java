package uz.greenwhite.smartup5_trade.m_display;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;

@SuppressWarnings("WeakerAccess")
public class ArgDisplayReview extends ArgOutlet {

    public final String inventoryId;
    public final String barcode;

    public ArgDisplayReview(ArgOutlet arg, String inventoryId, String barcode) {
        super(arg, arg.outletId);
        this.inventoryId = inventoryId;
        this.barcode = barcode;
    }

    protected ArgDisplayReview(UzumReader in) {
        super(in);
        this.inventoryId = in.readString();
        this.barcode = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.inventoryId);
        w.write(this.barcode);
    }

    public static final UzumAdapter<ArgDisplayReview> UZUM_ADAPTER = new UzumAdapter<ArgDisplayReview>() {
        @Override
        public ArgDisplayReview read(UzumReader in) {
            return new ArgDisplayReview(in);
        }

        @Override
        public void write(UzumWriter out, ArgDisplayReview val) {
            val.write(out);
        }
    };
}
