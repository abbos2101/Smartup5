package uz.greenwhite.smartup5_trade.common.predicate;

import android.util.SparseBooleanArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.filter.GroupFilterBuilder;
import uz.greenwhite.lib.filter.GroupFilterRef;
import uz.greenwhite.lib.filter.GroupFilterStrategy;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductFile;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductPhoto;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;

public class ProductFilterBuilder {

    final ProductFilterValue value;
    final MyArray<Product> items;
    final MyArray<ProductGroup> productGroups;
    final MyArray<ProductType> productTypes;
    final MyArray<ProductBarcode> productBarcodes;
    final MyArray<ProductPhoto> productPhotos;
    final MyArray<ProductFile> productFiles;
    final MyArray<String> mmlProductIds;

    public ProductFilterBuilder(ProductFilterValue value,
                                MyArray<Product> items,
                                MyArray<ProductGroup> productGroups,
                                MyArray<ProductType> productTypes,
                                MyArray<ProductBarcode> productBarcodes,
                                MyArray<ProductPhoto> productPhotos,
                                MyArray<ProductFile> productFiles,
                                MyArray<String> mmlProductIds) {
        this.value = value;
        this.items = items;
        this.productGroups = productGroups;
        this.productTypes = productTypes;
        this.productBarcodes = productBarcodes;
        this.productPhotos = productPhotos;
        this.productFiles = productFiles;
        this.mmlProductIds = mmlProductIds;
    }

    public ProductFilter build() {
        GroupFilter<Product> groupFilter = buildGroupFilter();
        FilterBoolean hasBarcode = makeBarcode();
        FilterBoolean hasPhoto = makePhoto();
        FilterBoolean hasFile = makeFile();
        FilterBoolean mml = makeMml();

        return new ProductFilter(groupFilter, hasBarcode, hasPhoto, hasFile, mml);
    }

    private GroupFilter<Product> buildGroupFilter() {
        GroupFilterStrategy<Product> adapter = getGroupFilterStrategy();
        GroupFilterBuilder<Product> builder = new GroupFilterBuilder<>(value.groupFilter, adapter, items);
        return builder.build();
    }

    private GroupFilterStrategy<Product> getGroupFilterStrategy() {
        return new GroupFilterStrategy<Product>() {
            @Override
            public boolean contains(Product item, int groupId, Integer typeId) {
                ProductGroupValue pgv = item.groups.find(String.valueOf(groupId), ProductGroupValue.KEY_ADAPTER);
                if (typeId != null) {
                    return pgv != null && pgv.typeId.equals(String.valueOf(typeId));
                } else {
                    return pgv == null;
                }
            }

            @Override
            public MyArray<GroupFilterRef> getGroups() {
                return productGroups.map(new MyMapper<ProductGroup, GroupFilterRef>() {
                    @Override
                    public GroupFilterRef apply(ProductGroup g) {
                        return new GroupFilterRef(Integer.parseInt(g.groupId), g.name);
                    }
                });
            }

            @Override
            public MyArray<SpinnerOption> makeOptions(final int groupId) {
                final SparseBooleanArray typeIds = items.reduce(new SparseBooleanArray(), new MyReducer<SparseBooleanArray, Product>() {
                    @Override
                    public SparseBooleanArray apply(SparseBooleanArray acc, Product product) {
                        for (ProductGroupValue v : product.groups) {
                            if (v.groupId.equals(String.valueOf(groupId))) {
                                acc.put(Integer.parseInt(v.typeId), true);
                                break;
                            }
                        }
                        return acc;
                    }
                });
                return productTypes
                        .filter(new MyPredicate<ProductType>() {
                            @Override
                            public boolean apply(ProductType pt) {
                                return pt.groupId.equals(String.valueOf(groupId)) &&
                                        typeIds.get(Integer.parseInt(pt.typeId), false);
                            }
                        })
                        .map(new MyMapper<ProductType, SpinnerOption>() {
                            @Override
                            public SpinnerOption apply(ProductType pt) {
                                return new SpinnerOption(pt.typeId, pt.name);
                            }
                        });
            }
        };
    }

    private FilterBoolean makeBarcode() {
        SparseBooleanArray productIds = new SparseBooleanArray();
        for (ProductBarcode b : productBarcodes) {
            productIds.put(Integer.parseInt(b.productId), b.barcodes.nonEmpty());
        }
        return new FilterBoolean(DS.getString(R.string.deal_filter_has_barcode),
                productIds, new ValueBoolean(value.hasBarcode));
    }

    private FilterBoolean makePhoto() {
        SparseBooleanArray productIds = new SparseBooleanArray();
        for (ProductPhoto p : productPhotos) {
            productIds.put(Integer.parseInt(p.productId), p.photos.nonEmpty());
        }
        return new FilterBoolean(DS.getString(R.string.deal_filter_has_photo),
                productIds, new ValueBoolean(value.hasPhoto));
    }

    private FilterBoolean makeFile() {
        SparseBooleanArray productIds = new SparseBooleanArray();
        for (ProductFile f : productFiles) {
            productIds.put(Integer.parseInt(f.productId), f.files.nonEmpty());
        }
        return new FilterBoolean(DS.getString(R.string.deal_filter_has_file),
                productIds, new ValueBoolean(value.hasFile));
    }

    private FilterBoolean makeMml() {
        SparseBooleanArray productIds = new SparseBooleanArray();
        for (String productId : mmlProductIds) {
            productIds.put(Integer.parseInt(productId), true);
        }
        return new FilterBoolean(DS.getString(R.string.deal_filter_mml),
                productIds, new ValueBoolean(value.mml));
    }
}
