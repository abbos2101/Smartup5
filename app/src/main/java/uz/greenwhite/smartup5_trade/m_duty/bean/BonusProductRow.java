package uz.greenwhite.smartup5_trade.m_duty.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class BonusProductRow {

    public final BonusProduct bonusProduct;
    public final Product product;

    public BonusProductRow(BonusProduct bonusProduct, Product product) {
        this.bonusProduct = bonusProduct;
        this.product = product;
    }

    public CharSequence getDetail() {
        ShortHtml html = UI.html().v(DS.getString(R.string.duty_action_bonus_product_detail, bonusProduct.bonusValue.toPlainString()));
        if (bonusProduct.maxValue != null && bonusProduct.maxValue.compareTo(BigDecimal.ZERO) != 0) {
            html.v(DS.getString(R.string.duty_action_bonus_product_detail_max, bonusProduct.maxValue.toPlainString()));
        }
        return html.html();
    }
}
