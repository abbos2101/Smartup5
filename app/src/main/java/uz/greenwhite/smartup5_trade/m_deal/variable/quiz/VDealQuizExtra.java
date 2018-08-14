package uz.greenwhite.smartup5_trade.m_deal.variable.quiz;



import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.TextValue;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.VariableUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;


public class VDealQuizExtra implements TextValue {

    public final ValueSpinner spinner;
    public final ValueString extra;

    public VDealQuizExtra(MyArray<SpinnerOption> options, ValueString extra) {
        this.spinner = new ValueSpinner(options.append(EXTRA_OPTION));
        this.extra = extra;
    }

    private static final SpinnerOption EXTRA_OPTION = new SpinnerOption("", DS.getString(R.string.deal_extra));

    @Override
    public String getText() {
        String text = spinner.getText();
        if (text.length() != 0) {
            return text;
        }
        return extra.getText();
    }

    @Override
    public void setText(String text) {
        SpinnerOption option = spinner.options.find(text, SpinnerOption.KEY_ADAPTER);
        if (option != null) {
            spinner.setValue(option);
            extra.setValue("");
        } else {
            spinner.setValue(EXTRA_OPTION);
            extra.setValue(text);
        }
    }

    @Override
    public void readyToChange() {
        VariableUtil.readyToChange(spinner, extra);
    }

    @Override
    public boolean modified() {
        return VariableUtil.modified(spinner, extra);
    }

    @Override
    public ErrorResult getError() {
        return VariableUtil.getError(spinner, extra);
    }
}