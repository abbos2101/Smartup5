package uz.greenwhite.smartup5_trade.m_deal.variable.returns;// 06.10.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class VDealReturnForm extends VDealForm {

    public final Warehouse warehouse;
    public final ValueArray<VDealReturn> returns;

    public VDealReturnForm(VisitModule module, Warehouse warehouse, ValueArray<VDealReturn> returns) {
        super(module, "" + module.id + ":" + warehouse.id);
        this.warehouse = warehouse;
        this.returns = returns;
    }

    public BigDecimal getTotalSum() {
        return returns.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealReturn>() {
            @Override
            public BigDecimal apply(BigDecimal result, VDealReturn val) {
                if (val.quantity.nonZero()) {
                    BigDecimal r = val.quantity.getQuantity().multiply(val.price.getQuantity());
                    return result.add(r);
                }
                return result;
            }
        });
    }

    public void copy(VDealReturn item) {
        returns.append(new VDealReturn(item.product,
                item.quantity.getQuantity(),
                item.price.getQuantity(),
                item.expiryDate.getText(),
                item.cardCode.getText()));
    }

    @Override
    public CharSequence getTitle() {
        return warehouse.name;
    }

    @Override
    public CharSequence getDetail() {
        ShortHtml html = UI.html().v(DS.getString(R.string.deal_form_currency)).v(DS.getString(R.string.basic));
        ErrorResult error = returns.getError();
        if (error.isError()) {
            html.br().fRed().v(error.getErrorMessage()).fRed();
        }
        return html.html();
    }

    @Override
    public boolean hasValue() {
        for (VDealReturn r : returns.getItems()) {
            if (r.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return returns.getItems().toSuper();
    }
}
