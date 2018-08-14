package uz.greenwhite.smartup5_trade.m_presentation.common;


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
import uz.greenwhite.smartup5_trade.m_presentation.bean.VPrPlanSpeciality;

public class SpecialtyAutoComplete extends TokenCompleteTextView<VPrPlanSpeciality> {

    private List<VPrPlanSpeciality> items = new ArrayList<>();

    public SpecialtyAutoComplete(Context context) {
        super(context);
    }

    public SpecialtyAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpecialtyAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(VPrPlanSpeciality object) {
        TextView tv = (TextView) LayoutInflater.from(getContext())
                .inflate(uz.greenwhite.smartup.anor.R.layout.token_view, (ViewGroup) getParent(), false);
        tv.setText(object.toString());
        return tv;
    }

    public void initAdapter() {
        FilteredArrayAdapter<VPrPlanSpeciality> adapter = new FilteredArrayAdapter<VPrPlanSpeciality>(getContext(), android.R.layout.simple_spinner_dropdown_item, items) {
            @Override
            protected boolean keepObject(VPrPlanSpeciality obj, String mask) {
                return TextUtils.isEmpty(mask) || CharSequenceUtil.containsIgnoreCase(obj.specialty.name, mask);
            }
        };
        super.setAdapter(adapter);
    }

    public void setItems(MyArray<VPrPlanSpeciality> items) {
        this.items = items.asList();
    }

    @Override
    protected VPrPlanSpeciality defaultObject(String completionText) {
        return null;
    }

}
