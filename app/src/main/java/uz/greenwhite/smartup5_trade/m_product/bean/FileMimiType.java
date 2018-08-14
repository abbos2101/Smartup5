package uz.greenwhite.smartup5_trade.m_product.bean;// 30.08.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class FileMimiType {

    public final String sha;
    public final String mimiType;

    public FileMimiType(String sha, String mimiType) {
        this.sha = sha;
        this.mimiType = mimiType;
    }

    public static final MyMapper<FileMimiType, String> KEY_ADAPTER = new MyMapper<FileMimiType, String>() {
        @Override
        public String apply(FileMimiType val) {
            return val.sha;
        }
    };

    public static final UzumAdapter<FileMimiType> UZUM_ADAPTER = new UzumAdapter<FileMimiType>() {
        @Override
        public FileMimiType read(UzumReader in) {
            return new FileMimiType(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, FileMimiType val) {
            out.write(val.sha);
            out.write(val.mimiType);
        }
    };
}
