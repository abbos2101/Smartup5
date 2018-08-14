package uz.greenwhite.smartup5_trade.common.predicate;

import android.util.SparseBooleanArray;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class ProductFilter {

    public final GroupFilter<Product> groupFilter;
    public final FilterBoolean hasBarcode;
    public final FilterBoolean hasPhoto;
    public final FilterBoolean hasFile;
    public final FilterBoolean mml;

    public ProductFilter(GroupFilter<Product> groupFilter,
                         FilterBoolean hasBarcode,
                         FilterBoolean hasPhoto,
                         FilterBoolean hasFile,
                         FilterBoolean mml) {
        this.groupFilter = groupFilter;
        this.hasBarcode = hasBarcode;
        this.hasPhoto = hasPhoto;
        this.hasFile = hasFile;
        this.mml = mml;
    }

    public ProductFilterValue toValue() {
        return new ProductFilterValue(GroupFilter.getValue(groupFilter),
                FilterBoolean.getValue(hasBarcode),
                FilterBoolean.getValue(hasPhoto),
                FilterBoolean.getValue(hasFile),
                FilterBoolean.getValue(mml));
    }

    public MyPredicate<Product> getPredicate() {
        MyPredicate<Product> predicate = MyPredicate.True();
        predicate = predicate.and(groupFilter.getPredicate());
        predicate = predicate.and(getHasBarcodePredicate());
        predicate = predicate.and(getHasPhotoPredicate());
        predicate = predicate.and(getHasFilePredicate());
        predicate = predicate.and(getMmlPredicate());
        return predicate;
    }

    private MyPredicate<Product> getHasBarcodePredicate() {
        if (hasBarcode.value.getValue()) {
            final SparseBooleanArray productIds = (SparseBooleanArray) hasBarcode.tag;
            return new MyPredicate<Product>() {
                @Override
                public boolean apply(Product product) {
                    return productIds.get(Integer.parseInt(product.id), false);
                }
            };
        }
        return null;
    }

    private MyPredicate<Product> getHasPhotoPredicate() {
        if (hasPhoto.value.getValue()) {
            final SparseBooleanArray productIds = (SparseBooleanArray) hasPhoto.tag;
            return new MyPredicate<Product>() {
                @Override
                public boolean apply(Product product) {
                    return productIds.get(Integer.parseInt(product.id), false);
                }
            };
        }
        return null;
    }

    private MyPredicate<Product> getHasFilePredicate() {
        if (hasFile.value.getValue()) {
            final SparseBooleanArray productIds = (SparseBooleanArray) hasFile.tag;
            return new MyPredicate<Product>() {
                @Override
                public boolean apply(Product product) {
                    return productIds.get(Integer.parseInt(product.id), false);
                }
            };
        }
        return null;
    }

    private MyPredicate<Product> getMmlPredicate(){
        if (mml.value.getValue()) {
            final SparseBooleanArray productIds = (SparseBooleanArray) mml.tag;
            return new MyPredicate<Product>() {
                @Override
                public boolean apply(Product product) {
                    return productIds.get(Integer.parseInt(product.id), false);
                }
            };
        }
        return null;
    }
}
