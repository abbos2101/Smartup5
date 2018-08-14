package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;// 05.11.2016

import java.math.BigDecimal;

public class ProductPriceRow {

    public final String priceName;
    private BigDecimal count;
    private BigDecimal totalSum;

    public ProductPriceRow(String priceName) {
        this.priceName = priceName;
        this.count = BigDecimal.ZERO;
        this.totalSum = BigDecimal.ZERO;
    }

    public void setCount(BigDecimal count) {
        this.count = this.count.add(count);
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = this.totalSum.add(totalSum);
    }

    public BigDecimal getCount() {
        return count;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }
}
