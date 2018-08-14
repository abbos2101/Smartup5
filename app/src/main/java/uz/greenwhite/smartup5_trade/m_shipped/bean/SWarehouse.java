package uz.greenwhite.smartup5_trade.m_shipped.bean;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SWarehouse {

    public final MyArray<SProduct> products;

    public SWarehouse(MyArray<SProduct> products) {
        this.products = products;
    }

    public static final UzumAdapter<SWarehouse> UZUM_ADAPTER = new UzumAdapter<SWarehouse>() {
        @Override
        public SWarehouse read(UzumReader in) {
            List<SProduct> result = new ArrayList<>();
            while (!in.isEOF()) {
                SProduct sProduct = in.readValue(SProduct.UZUM_ADAPTER);
                if (sProduct == null) {
                    break;
                }
                result.add(sProduct);
            }
            return new SWarehouse(MyArray.from(result));
        }

        @Override
        public void write(UzumWriter out, SWarehouse val) {
            for (SProduct p : val.products) {
                out.write(p, SProduct.UZUM_ADAPTER);
            }
        }
    };
}
