package uz.greenwhite.smartup5_trade.common;


import android.content.Context;
import android.util.AttributeSet;

import java.util.Comparator;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.widget.chip.Chip;
import uz.greenwhite.lib.widget.chip.ChipInterface;
import uz.greenwhite.lib.widget.chip.ChipsInput;


public class MyChipsInput extends ChipsInput {

    public MyChipsInput(Context context) {
        super(context);
    }

    public MyChipsInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unchecked")
    public MyArray<Chip> getSelectedItems() {
        return MyArray.from((List<Chip>) getSelectedChipList());
    }

    public void setSelectedAll(boolean selected) {
        List<? extends ChipInterface> filterableList = getFilterableList();
        for (ChipInterface v : filterableList) {
            if (selected) addChip(v);
            else removeChip(v);
        }
    }

    public void setItems(MyArray<Chip> items) {
        items.checkUniqueness(KEY_ADAPTER);

        setFilterableList(items.sort(new Comparator<Chip>() {
            @Override
            public int compare(Chip l, Chip r) {
                return CharSequenceUtil.compareToIgnoreCase(l.getLabel(), r.getLabel());
            }
        }).asList());
    }

    public void setItemsByUnion(MyArray<Chip> items) {
        setItems(getSelectedItems().union(items, KEY_ADAPTER));
    }

    @SuppressWarnings("unchecked")
    public MyArray<String> getSelectedIds() {
        return getSelectedItems().map(KEY_ADAPTER);
    }

    private static final MyMapper<Chip, String> KEY_ADAPTER = new MyMapper<Chip, String>() {
        @Override
        public String apply(Chip chip) {
            return (String) chip.getId();
        }
    };
}