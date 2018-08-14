package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonMemo {

    public final String outletId;
    public final MyArray<Memo> memos;

    public PersonMemo(String outletId, MyArray<Memo> memos) {
        this.outletId = outletId;
        this.memos = MyArray.nvl(memos);
    }

    public static PersonMemo makeDefault(String outletId) {
        return new PersonMemo(outletId, null);
    }

    public static final MyMapper<PersonMemo, String> KEY_ADAPTER = new MyMapper<PersonMemo, String>() {
        @Override
        public String apply(PersonMemo val) {
            return val.outletId;
        }
    };

    public static final UzumAdapter<PersonMemo> UZUM_ADAPTER = new UzumAdapter<PersonMemo>() {
        @Override
        public PersonMemo read(UzumReader in) {
            return new PersonMemo(in.readString(), in.readArray(Memo.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonMemo val) {
            out.write(val.outletId);
            out.write(val.memos, Memo.UZUM_ADAPTER);
        }
    };
}
