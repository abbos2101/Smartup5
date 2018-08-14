package uz.greenwhite.smartup5_trade.m_deal_history.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class HDeal {

    public final String filialId;
    public final String roomId;
    public final String personId;
    public final String state;
    public final HDealHeader header;
    public final MyArray<HDealModule> modules;

    public HDeal(String filialId,
                 String roomId,
                 String personId,
                 String state,
                 HDealHeader header,
                 MyArray<HDealModule> modules) {
        this.filialId = filialId;
        this.roomId = roomId;
        this.personId = personId;
        this.state = state;
        this.header = header;
        this.modules = MyArray.nvl(modules).filterNotNull();
    }

    public static final UzumAdapter<HDeal> UZUM_ADAPTER = new UzumAdapter<HDeal>() {
        @Override
        public HDeal read(UzumReader in) {
            return new HDeal(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readValue(HDealHeader.UZUM_ADAPTER),
                    in.readArray(HDealModule.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter uzumWriter, HDeal hDeal) {
            uzumWriter.write(hDeal.filialId);
            uzumWriter.write(hDeal.roomId);
            uzumWriter.write(hDeal.personId);
            uzumWriter.write(hDeal.state);
            uzumWriter.write(hDeal.header, HDealHeader.UZUM_ADAPTER);
            uzumWriter.write(hDeal.modules, HDealModule.UZUM_ADAPTER);
        }
    };
}
