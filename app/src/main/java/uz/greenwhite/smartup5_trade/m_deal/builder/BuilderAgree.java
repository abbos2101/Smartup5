package uz.greenwhite.smartup5_trade.m_deal.builder;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.agree.DealAgree;
import uz.greenwhite.smartup5_trade.m_deal.bean.agree.DealAgreeModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgree;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgreeForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgreeModule;
import uz.greenwhite.smartup5_trade.m_outlet.bean.DoctorLastAgree;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SpecialityProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderAgree {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealAgree> initial;

    public BuilderAgree(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private MyArray<DealAgree> getInitial() {
        DealAgreeModule agreeModule = dealRef.findDealModule(module.id);
        if (agreeModule != null) {
            return agreeModule.agrees;
        }
        return MyArray.emptyArray();
    }

    private Set<String> getOutletTypeProducts() {
        OutletDoctor doctor = (OutletDoctor) dealRef.outlet;
        SpecialityProduct specialityProduct = dealRef.getSpecialityProduct(doctor.specialityId);
        if (specialityProduct != null) {
            return Utils.intersect(specialityProduct.productIds, dealRef.filial.productIds).asSet();
        }
        return new HashSet<>();
    }

    private MyArray<Product> getProducts() {
        MyArray<String> keys = initial.map(DealAgree.KEY_ADAPTER);

        final Set<String> otps = getOutletTypeProducts();
        keys = MyArray.from(otps).union(keys);

        MyArray<Product> result = keys.map(new MyMapper<String, Product>() {
            @Override
            public Product apply(String productId) {
                return dealRef.findProduct(productId);
            }
        });

        result.checkNotNull();

        result = result.sort(new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                return CharSequenceUtil.compareToIgnoreCase(lhs.name, rhs.name);
            }
        });

        return result;
    }

    private final MyArray<SpinnerOption> PERIOD_OPTIONS = MyArray.from(
            new SpinnerOption("D", DS.getString(R.string.deal_agree_period_day)),
            new SpinnerOption("W", DS.getString(R.string.deal_agree_period_week)),
            new SpinnerOption("M", DS.getString(R.string.deal_agree_period_month)),
            new SpinnerOption("Y", DS.getString(R.string.deal_agree_period_year))
    );

    private VDealAgreeForm makeAgreeForm() {
        if (!(dealRef.outlet instanceof OutletDoctor)) return null;
        final MyArray<Product> products = getProducts();

        final MyArray<DoctorLastAgree> lastAgrees = dealRef.getDoctorLastAgrees();
        MyArray<VDealAgree> agrees = products.map(new MyMapper<Product, VDealAgree>() {
            @Override
            public VDealAgree apply(Product product) {
                String oldCurValue = "";
                String oldNewValue = "";
                String oldPeriod = "";
                BigDecimal curValue = null;
                BigDecimal newValue = null;
                String period = "W";
                DealAgree dealAgree = initial.find(product.id, DealAgree.KEY_ADAPTER);
                if (dealAgree != null) {
                    oldCurValue = dealAgree.oldCurValue;
                    oldNewValue = dealAgree.oldNewValue;
                    if (!TextUtils.isEmpty(dealAgree.curValue)) {
                        curValue = new BigDecimal(dealAgree.curValue);
                    }
                    if (!TextUtils.isEmpty(dealAgree.newValue)) {
                        newValue = new BigDecimal(dealAgree.newValue);
                    }
                    period = dealAgree.period;
                }
                DoctorLastAgree lastAgree = lastAgrees.find(product.id, DoctorLastAgree.KEY_ADAPTER);
                if (lastAgree != null) {
                    if (!TextUtils.isEmpty(lastAgree.period)) {
                        SpinnerOption option = PERIOD_OPTIONS.find(lastAgree.period, SpinnerOption.KEY_ADAPTER);
                        oldPeriod = option == null ? "" : (String) option.name;
                    }
                    if (TextUtils.isEmpty(oldCurValue)) {
                        oldCurValue = lastAgree.curQuant;
                    }
                    if (TextUtils.isEmpty(oldNewValue)) {
                        oldNewValue = lastAgree.newQuant;
                    }
                }
                SpinnerOption value = PERIOD_OPTIONS.find(period, SpinnerOption.KEY_ADAPTER);
                if (value == null) {
                    throw AppError.NullPointer();
                }
                ValueSpinner spPeriod = new ValueSpinner(PERIOD_OPTIONS, value);

                CharSequence title = product.name;
                //TODO fix    PlanOCTProduct planOCTProduct = dealRef.planOCTProducts.find(product.id, PlanOCTProduct.KEY_ADAPTER);
                //  if (planOCTProduct != null) {
                //      title = product.name + " (План " + planOCTProduct.quantity + ")";
                //  }

                return new VDealAgree(product, title, oldCurValue, oldNewValue, oldPeriod, curValue, newValue, spPeriod);
            }
        });

        agrees = agrees.sort(new Comparator<VDealAgree>() {
            @Override
            public int compare(VDealAgree lhs, VDealAgree rhs) {
                return MyPredicate.compare(lhs.product.orderNo, rhs.product.orderNo);
            }
        });

        return new VDealAgreeForm(module, new ValueArray<>(agrees));
    }

    public VDealAgreeModule build() {
        return new VDealAgreeModule(module, makeAgreeForm());
    }

}
