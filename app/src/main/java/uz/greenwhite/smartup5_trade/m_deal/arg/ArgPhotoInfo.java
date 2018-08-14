package uz.greenwhite.smartup5_trade.m_deal.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgPhotoInfo extends ArgDeal {

    public final String formCode, photoSha;

    public ArgPhotoInfo(ArgDeal arg, String formCode, String photoSha) {
        super(arg, arg.roomId, arg.dealId, arg.location, arg.accuracy, arg.type);
        this.formCode = formCode;
        this.photoSha = photoSha;
    }

    public ArgPhotoInfo(UzumReader in) {
        super(in);
        this.formCode = in.readString();
        this.photoSha = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.formCode);
        w.write(this.photoSha);
    }

    public static final UzumAdapter<ArgPhotoInfo> UZUM_ADAPTER = new UzumAdapter<ArgPhotoInfo>() {
        @Override
        public ArgPhotoInfo read(UzumReader in) {
            return new ArgPhotoInfo(in);
        }

        @Override
        public void write(UzumWriter out, ArgPhotoInfo val) {
            val.write(out);
        }
    };
}
