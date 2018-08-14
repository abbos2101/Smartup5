package uz.greenwhite.smartup5_trade.m_duty;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_duty.bean.PriceRow;
import uz.greenwhite.smartup5_trade.m_duty.filter.PriceFilter;
import uz.greenwhite.smartup5_trade.m_duty.filter.PriceFilterBuilder;
import uz.greenwhite.smartup5_trade.m_duty.filter.PriceFilterValue;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;

public class PriceFragment extends MoldContentRecyclerFragment<PriceRow> {

    public static void open(ArgSession arg) {
        Mold.openContent(PriceFragment.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    public PriceFilter filter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.price);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(PriceRow item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        addSubMenu(DS.getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });

        setEmptyText(getString(R.string.list_is_empty));

        ScopeUtil.execute(jobMate, getArgSession(), new OnScopeReadyCallback<Tuple2>() {
            @Override
            public Tuple2 onScopeReady(Scope scope) {
                MyArray<PriceRow> priceRows = DutyUtil.getPriceRows(scope);
                MyArray<Product> products = scope.ref.getProducts();
                MyArray<ProductGroup> productGroups = scope.ref.getProductGroups();
                MyArray<ProductType> productTypes = scope.ref.getProductTypes();
                PriceFilter filter = new PriceFilterBuilder(PriceFilterValue.makeDefault(),
                        products, productGroups, productTypes).build();
                return new Tuple2(priceRows, filter);
            }

            @Override
            public void onDone(Tuple2 val) {
                setListItems((MyArray<PriceRow>) val.first);
                filter = (PriceFilter) val.second;
                setListFilter();
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(throwable).message).show();
            }
        });
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, PriceRow item) {
        ProductInfoFragment.open(new ArgProduct(getArgSession(), item.product.id));
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    public void setListFilter() {
        if (filter != null && adapter != null) {
            adapter.predicateOthers = filter.getPredicate();
            adapter.filter();
        }
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new PriceTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_product_price;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, PriceRow item) {
        vsItem.textView(R.id.product_name).setText(item.product.name);
        vsItem.textView(R.id.price_name).setText(item.priceName);
        vsItem.textView(R.id.product_price).setText(item.price);

        Bitmap photo = item.getProductImage(getArgSession());
        if (photo == null) {
            vsItem.imageView(R.id.iv_photo).setImageResource(R.drawable.display_photo);
        } else {
            vsItem.imageView(R.id.iv_photo).setImageBitmap(photo);
        }
    }
}
