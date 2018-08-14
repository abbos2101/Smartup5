package uz.greenwhite.smartup5_trade.m_deal.ui.adapter;// 03.10.2016

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyRecyclerAdapter;
import uz.greenwhite.lib.mold.MoldApi;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.OnItemClickListener;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealModuleFragment;

public class DealModuleAdapter extends MyRecyclerAdapter<VModule> {

    private final OnItemClickListener<VForm> onClick;
    private final Drawable arrowDown, arrowUp;
    private String formCode;
    public final ArrayList<String> openingModule;

    private MyPredicate<VModule> modulePredicate = null;
    private Filter.FilterListener filterListener = null;

    public DealModuleAdapter(Context context, OnItemClickListener<VForm> onClick) {
        super(context);
        this.onClick = onClick;
        this.arrowDown = MoldApi.changeDrawableColor(R.drawable.ic_keyboard_arrow_down_black_24dp, R.color.light_silver);
        this.arrowUp = MoldApi.changeDrawableColor(R.drawable.ic_keyboard_arrow_up_black_24dp, R.color.light_silver);
        this.openingModule = new ArrayList<>();
    }

    public DealModuleAdapter(DealModuleFragment index, OnItemClickListener<VForm> onClick) {
        this(index.getActivity(), onClick);
    }


    public void showForm(VForm form) {
        onClickItem(form);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.z_module_index_row;
    }

    @Override
    public void populate(ViewSetup vsItem, final VModule item) {
        final ImageView iconArrow = vsItem.imageView(R.id.iv_icon_arrow);
        final ViewGroup llForm = vsItem.viewGroup(R.id.ll_forms);
        final ViewGroup llModule = vsItem.viewGroup(R.id.ll_module);

        vsItem.id(R.id.ll_module_notify).setVisibility(View.GONE);
        String notifyCount = item.getNotifyCount();
        if (!TextUtils.isEmpty(notifyCount) && !"0".equals(notifyCount)) {
            vsItem.id(R.id.ll_module_notify).setVisibility(View.VISIBLE);
            vsItem.textView(R.id.tv_module_notify).setText(notifyCount);
        }

        int moduleIcon = item.getIconResId();
        vsItem.id(R.id.iv_icon).setVisibility(moduleIcon != 0 ? View.VISIBLE : View.INVISIBLE);

        changeFocusModule(vsItem, item, moduleIcon);

        llForm.removeAllViews();

        final MyArray<? extends VForm> forms = item.getModuleForms();

        if (forms.size() > 1) {
            boolean opening = openingModule.contains("" + item.getModuleId());
            llForm.setVisibility(opening ? View.VISIBLE : View.GONE);
            iconArrow.setImageDrawable(opening ? arrowUp : arrowDown);
            iconArrow.setVisibility(View.VISIBLE);

            for (final VForm f : forms) {
                final ViewSetup vs = new ViewSetup(context, R.layout.z_module_index_form_row);
                vs.textView(R.id.tv_title).setText(UI.html().b().v(f.getTitle()).b().html());
                vs.textView(R.id.tv_detail).setText(f.getDetail());

                changeFocusForm(vs, f);

                llForm.addView(vs.view);
                vs.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickItem(f);
                    }
                });
            }
            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean visible = llForm.getVisibility() == View.VISIBLE;
                    iconArrow.setImageDrawable(visible ? arrowDown : arrowUp);
                    if (visible) {
                        openingModule.remove("" + item.getModuleId());
                        collapse(llForm);
                    } else {
                        openingModule.add("" + item.getModuleId());
                        expand(llForm);
                    }
                }
            };
            iconArrow.setOnClickListener(onClick);
            llModule.setOnClickListener(onClick);
        } else {
            iconArrow.setVisibility(View.INVISIBLE);
            llModule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItem(forms.get(0));
                }
            });
        }
    }

    private void changeFocusForm(ViewSetup vsForm, VForm form) {
        if (form.code.equals(this.formCode)) {
            vsForm.id(R.id.ll_form_row).setBackgroundColor(MoldApi.getColor(R.color.click_light_silver));
        } else {
            vsForm.id(R.id.ll_form_row).setBackgroundColor(MoldApi.getColor(R.color.background));
        }
    }

    @SuppressWarnings("ResourceAsColor")
    private void changeFocusModule(ViewSetup vsItem, VModule item, int moduleIcon) {
        ErrorResult error = item.getError();
        CharSequence title = item.getTitle();

        int color = error.isError() ? R.color.red : (item.hasValue() ? R.color.green : R.color.colorAccent);

        if (moduleIcon != 0) {
            vsItem.imageView(R.id.iv_icon).setImageDrawable(UI.changeDrawableColor(context, moduleIcon, color));
        }

        if (item.isMandatory()) {
            vsItem.id(R.id.iv_mandatory).setVisibility(View.VISIBLE);
            vsItem.imageView(R.id.iv_mandatory).setImageDrawable(UI.changeDrawableColor(context, R.drawable.gwslib_mandatory, color));
        } else {
            vsItem.id(R.id.iv_mandatory).setVisibility(View.GONE);
        }

        TextView tvTitle = vsItem.textView(R.id.tv_title);
        tvTitle.setText(title);
        if (R.color.colorAccent == color) {
            tvTitle.setTextColor(DS.getColor(R.color.dark_silver));
        } else {
            tvTitle.setTextColor(DS.getColor(color));
        }
    }

    public void notifyChanged() {
        setPredicateOthers(this.modulePredicate);
        if (filterListener != null) filter(filterListener);
        else filter();
        this.notifyDataSetChanged();
    }

    private void onClickItem(VForm form) {
        formCode = form.code;
        if (onClick != null) onClick.onListItemClick(form);
        notifyDataSetChanged();
    }

    public static void expand(final View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void collapse(final View v) {
        v.setVisibility(View.GONE);
    }
}
