package uz.greenwhite.smartup5_trade.common;

import android.content.Context;
import android.view.View;
import android.widget.Filter;
import android.widget.TextView;

import uz.greenwhite.lib.collection.MyAdapter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.Bank;

public class CompleteAdapterBank extends MyAdapter<Bank, CompleteAdapterBank.ViewHolder> {

    public CompleteAdapterBank(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return android.R.layout.simple_list_item_1;
    }

    @Override
    public CompleteAdapterBank.ViewHolder makeHolder(View view) {
        CompleteAdapterBank.ViewHolder h = new CompleteAdapterBank.ViewHolder();
        h.name = UI.id(view, android.R.id.text1);
        return h;
    }

    @Override
    public void populate(ViewHolder viewHolder, Bank bank) {
        viewHolder.name.setText(bank.mfo + " " + bank.name);
    }

    static class ViewHolder {
        TextView name;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    public Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(final CharSequence text) {
            FilterResults result = new FilterResults();
            try {
                MyArray ex = items.filter(new MyPredicate<Bank>() {
                    @Override
                    public boolean apply(Bank p) {
                        return CharSequenceUtil.containsIgnoreCase(p.name, text) ||
                                CharSequenceUtil.containsIgnoreCase(p.mfo, text);
                    }
                });
                if (ex.isEmpty()) {
                    ex = ex.append(Bank.DEFAULT);
                }
                result.values = ex;
                result.count = ex.size();
            } catch (Exception var4) {
                result.values = var4;
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values instanceof Exception) {
                throw new AppError((Throwable) results.values);
            } else {
                filteredItems = (MyArray) results.values;
                notifyDataSetChanged();
            }
        }
    };
}

