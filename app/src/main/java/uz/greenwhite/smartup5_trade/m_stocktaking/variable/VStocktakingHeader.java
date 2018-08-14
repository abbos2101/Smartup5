package uz.greenwhite.smartup5_trade.m_stocktaking.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHeader;

public class VStocktakingHeader extends VariableLike {

    public final StocktakingHeader header;
    public final ValueString vDate;
    public final ValueString vNumber;
    public final ValueSpinner vCurrency;
    public final ValueString vNote;

    public VStocktakingHeader(StocktakingHeader header,
                              ValueSpinner currency) {
        this.header = header;
        this.vDate = new ValueString(20, header.date);
        this.vNumber = new ValueString(100, header.number);
        this.vCurrency = currency;
        this.vNote = new ValueString(200, header.note);
    }

    public StocktakingHeader toValue() {
        return new StocktakingHeader(vDate.getText(),
                vNumber.getText(),
                vCurrency.getValue().code,
                vNote.getText());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(vDate, vNumber, vCurrency, vNote).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        if (vDate.isEmpty() || vNumber.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }

        return ErrorResult.NONE;
    }
}
