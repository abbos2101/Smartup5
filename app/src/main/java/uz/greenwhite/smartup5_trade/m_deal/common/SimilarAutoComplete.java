package uz.greenwhite.smartup5_trade.m_deal.common;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.smartup.anor.common.autocomplete.FilteredArrayAdapter;
import uz.greenwhite.smartup.anor.common.autocomplete.TokenCompleteTextView;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class SimilarAutoComplete extends TokenCompleteTextView<Product> {

    private List<Product> items = new ArrayList<>();

    public SimilarAutoComplete(Context context) {
        super(context);
    }

    public SimilarAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimilarAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Product object) {
        TextView tv = (TextView) LayoutInflater.from(getContext())
                .inflate(uz.greenwhite.smartup.anor.R.layout.token_view, (ViewGroup) getParent(), false);
        tv.setText(object.name);
        return tv;
    }

    public void initAdapter() {
        FilteredArrayAdapter<Product> adapter = new FilteredArrayAdapter<Product>(getContext(), android.R.layout.simple_spinner_dropdown_item, items) {
            @Override
            protected boolean keepObject(Product obj, String mask) {
                return TextUtils.isEmpty(mask) || CharSequenceUtil.containsIgnoreCase(obj.name, mask);
            }
        };
        super.setAdapter(adapter);
    }

    public void setItems(MyArray<Product> items) {
        this.items = items.asList();
    }

    @Override
    protected Product defaultObject(String completionText) {
        return null;
    }

}
