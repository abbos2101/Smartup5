package uz.greenwhite.smartup5_trade.m_incoming.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHeader;

public class VIncomingHeader extends VariableLike {

    public final IncomingHeader header;
    public final ValueString incomingDate;
    public final ValueString incomingNumber;
    public final ValueSpinner currency;
    public final ValueSpinner providerPerson;
    public final ValueString note;

    public VIncomingHeader(IncomingHeader header,
                           ValueSpinner currency,
                           ValueSpinner providerPerson) {
        this.header = header;
        this.incomingDate = new ValueString(20, header.incomingDate);
        this.incomingNumber = new ValueString(100, header.incomingNumber);
        this.currency = currency;
        this.providerPerson = providerPerson;
        this.note = new ValueString(200, header.note);
    }

    public IncomingHeader toValue() {
        return new IncomingHeader(incomingDate.getText(),
                incomingNumber.getText(),
                currency.getValue().code,
                providerPerson.getValue().code,
                note.getText());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(incomingDate, incomingNumber, currency, providerPerson, note).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        if (incomingDate.isEmpty() ||
                incomingNumber.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }

        return ErrorResult.NONE;
    }
}
