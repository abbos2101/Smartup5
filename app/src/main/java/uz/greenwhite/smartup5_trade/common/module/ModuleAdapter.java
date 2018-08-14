package uz.greenwhite.smartup5_trade.common.module;// 03.10.2016

import android.content.Context;
import android.graphics.drawable.Drawable;
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

public class ModuleAdapter extends MyRecyclerAdapter<VModule> {

    private final OnItemClickListener<VForm> onClick;
    private final Drawable arrowDown, arrowUp;
    private String formCode;
    private final ArrayList<String> openingModule;

    private MyPredicate<VModule> modulePredicate = null;
    private Filter.FilterListener filterListener = null;

    public ModuleAdapter(Context context, OnItemClickListener<VForm> onClick) {
        super(context);
        this.onClick = onClick;
        this.arrowDown = MoldApi.changeDrawableColor(R.drawable.ic_keyboard_arrow_down_black_24dp, R.color.light_silver);
        this.arrowUp = MoldApi.changeDrawableColor(R.drawable.ic_keyboard_arrow_up_black_24dp, R.color.light_silver);
        this.openingModule = new ArrayList<>();
    }

    public ModuleAdapter(ModuleIndexFragment index, OnItemClickListener<VForm> onClick) {
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
    protected void populate(ViewSetup vsItem, final VModule item) {
        final ImageView iconArrow = vsItem.imageView(R.id.iv_icon_arrow);
        final ViewGroup llForm = vsItem.viewGroup(R.id.ll_forms);
        final ViewGroup llModule = vsItem.viewGroup(R.id.ll_module);

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

    private boolean hasFocusForm(VModule module) {
        MyArray<? extends VForm> dealForms = module.getModuleForms();
        for (VForm f : dealForms) {
            if (f.code.equals(formCode)) return true;
        }
        return false;
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

        int color = hasFocusForm(item) ? R.color.colorAccent :
                (error.isError() ? R.color.red : (item.hasValue() ? R.color.green : R.color.dark_silver));

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
        tvTitle.setTextColor(MoldApi.getColor(color));
    }

    public void notifyChanged() {
        setPredicateOthers(this.modulePredicate);
        if (filterListener != null) filter(filterListener);
        else filter();
        this.notifyDataSetChanged();
    }

    public void setModulePredicate(MyPredicate<VModule> modulePredicate) {
        this.modulePredicate = modulePredicate;
    }

    public void setFilterListener(Filter.FilterListener filterListener) {
        this.filterListener = filterListener;
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
