package uz.greenwhite.smartup5_trade.common.predicate;

import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductFilterValue {

    public final GroupFilterValue groupFilter;
    public final boolean hasBarcode;
    public final boolean hasPhoto;
    public final boolean hasFile;
    public final boolean mml;

    public ProductFilterValue(GroupFilterValue groupFilter,
                              boolean hasBarcode,
                              boolean hasPhoto,
                              boolean hasFile,
                              boolean mml) {
        this.groupFilter = GroupFilterValue.nvl(groupFilter);
        this.hasBarcode = hasBarcode;
        this.hasPhoto = hasPhoto;
        this.hasFile = hasFile;
        this.mml = mml;
    }

    public static final UzumAdapter<ProductFilterValue> UZUM_ADAPTER = new UzumAdapter<ProductFilterValue>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public ProductFilterValue read(UzumReader in) {
            return new ProductFilterValue(in.readValue(GroupFilterValue.UZUM_ADAPTER),
                    in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean());
        }

        @Override
        public void write(UzumWriter out, ProductFilterValue val) {
            out.write(val.groupFilter, GroupFilterValue.UZUM_ADAPTER);
            out.write(val.hasBarcode);
            out.write(val.hasPhoto);
            out.write(val.hasFile);
            out.write(val.mml);
        }
    };
}
