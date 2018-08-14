package uz.greenwhite.smartup5_trade.m_shipped.variable;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.bean.ReturnReason;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class SDealRef {

    public final String accountId;
    public final String filialId;
    public final Filial filial;
    public final SDeal sDeal;
    public final SDealHolder holder;
    public final Outlet outlet;
    public final Setting setting;
    public final Scope scope;

    private final MyArray<Product> products;

    public SDealRef(Scope scope, SDealHolder holder, String dealId) {
        this.scope = scope;
        this.holder = holder;
        this.accountId = this.scope.accountId;
        this.filialId = holder.deal.filialId;
        this.filial = scope.ref.getFilial(filialId);
        this.sDeal = scope.ref.getSDeal(dealId);

        this.outlet = DSUtil.getOutlet(this.scope, holder.deal.outletId);
        this.setting = this.scope.ref.getSettingWithDefault();
        this.products = this.scope.ref.getProducts();
    }

    public Product findProduct(String productId) {
        return products.find(productId, Product.KEY_ADAPTER);
    }

    public Warehouse getWarehouse(String warehouseId) {
        return scope.ref.getWarehouse(warehouseId);
    }

    public PriceType getPriceType(String priceTypeId) {
        return scope.ref.getPriceType(priceTypeId);
    }

    public Currency getCurrency(String currencyId) {
        return scope.ref.getCurrency(currencyId);
    }

    public PaymentType getPaymentType(String paymentTypeId) {
        return scope.ref.getPaymentTypes(paymentTypeId);
    }

    public MyArray<ReturnReason> getReasons() {
        return scope.ref.getReturnReasons();
    }

    public MyArray<SDealHolder> getSDealHolders() {
        return scope.entry.getSDealHolders();
    }
}
