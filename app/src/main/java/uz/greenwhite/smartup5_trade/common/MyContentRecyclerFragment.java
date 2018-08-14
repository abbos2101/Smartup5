package uz.greenwhite.smartup5_trade.common;

import android.view.View;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldSearchQuery;
import uz.greenwhite.lib.view_setup.ViewSetup;

public abstract class MyContentRecyclerFragment<E> extends MoldContentRecyclerFragment<E> {

    private static final Object DEFAULT_ITEM = new Object();

    protected abstract void adapterPopulateItem(ViewSetup vsItem, E item);

    private int footerHeight = 0;
    private int defaultHeight = 0;

    public void setFooterHeight(int footerHeight) {
        if (footerHeight < 0) footerHeight = 0;
        this.footerHeight = footerHeight;
    }

    @Override
    public void setSearchMenu(int iconResId, final MoldSearchQuery searchQuery) {
        super.setSearchMenu(iconResId, new MoldSearchListQuery() {
            @Override
            public boolean filter(E item, String text) {
                return item == DEFAULT_ITEM ||
                        ((MoldSearchListQuery) searchQuery).filter(item, text);
            }
        });
    }

    @Override
    public void setSearchMenu(final MoldSearchQuery searchQuery) {
        super.setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(E item, String text) {
                return item == DEFAULT_ITEM ||
                        ((MoldSearchListQuery) searchQuery).filter(item, text);
            }
        });
    }

    @Override
    public void setListItems(MyArray<E> items) {
        if (items.nonEmpty()) {
            items = items.append((E) DEFAULT_ITEM);
        }
        super.setListItems(items);
    }

    @Override
    public MyArray<E> getListFilteredItems() {
        return super.getListFilteredItems().filter(new MyPredicate<E>() {
            @Override
            public boolean apply(E e) {
                return e != DEFAULT_ITEM;
            }
        });
    }

    @Override
    public MyArray<E> getListItems() {
        return super.getListItems().filter(new MyPredicate<E>() {
            @Override
            public boolean apply(E e) {
                return e != DEFAULT_ITEM;
            }
        });
    }

    public void setFilterPredicate(final MyPredicate<E> predicate) {
        if (predicate != null) {
            adapter.predicateOthers = new MyPredicate<E>() {
                @Override
                public boolean apply(E e) {
                    return e == DEFAULT_ITEM || predicate.apply(e);
                }
            };
        } else {
            adapter.predicateOthers = null;
        }
        adapter.filter();
    }

    @Override
    protected final void adapterPopulate(ViewSetup vsItem, E item) {
        if (item != DEFAULT_ITEM) {
            if (defaultHeight == 0) {
                defaultHeight = vsItem.view.getHeight();
            }
            vsItem.view.setMinimumHeight(defaultHeight);
            vsItem.view.setVisibility(View.VISIBLE);
            adapterPopulateItem(vsItem, item);
        } else {
            vsItem.view.setMinimumHeight(footerHeight);
            vsItem.view.setVisibility(View.INVISIBLE);
        }
    }
}
