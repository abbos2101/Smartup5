package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgRetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_deal.filter.RetailAuditFilter;
import uz.greenwhite.smartup5_trade.m_deal.filter.RetailAuditFilterBuilder;
import uz.greenwhite.smartup5_trade.m_deal.filter.RetailAuditFilterValue;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductType;

public class RetailAuditFragment extends DealFormRecyclerFragment<VDealRetailAudit> {
    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VDealRetailAuditForm form = DealUtil.getDealForm(this);
        Mold.setTitle(getActivity(), form.retailAuditSet.name);
        setHasLongClick(true);
        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealRetailAudit item, String text) {
                return CharSequenceUtil.containsIgnoreCase(item.product.name, text);
            }
        });
        addSubMenu(getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });
        setListItems(form.retailAudits.getItems());
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, VDealRetailAudit item) {
        ArgRetailAuditProduct arg = new ArgRetailAuditProduct(getArgDeal(), item.product.id);
        Mold.addContent(getActivity(), DealUtil.newInstance(arg, RetailAuditProductFragment.class,
                DealUtil.getFormCode(this)));
    }

    @Override
    protected void onItemLongClick(RecyclerAdapter.ViewHolder holder, VDealRetailAudit item) {
        super.onItemLongClick(holder, item);

        //show details layout when long clicked
        View llExtra = holder.vsItem.id(R.id.ll_extra_content);
        if (llExtra.getVisibility() == View.GONE) {
            llExtra.setVisibility(View.VISIBLE);
        } else {
            llExtra.setVisibility(View.GONE);
        }

    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new RetailTuningFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        String formCode = DealUtil.getFormCode(Mold.getContentFragment(activity));
        DealData dealData = Mold.getData(activity);
        RetailAuditFilter filter =  dealData.filter.findRetailAudit(formCode);
        MyPredicate<VDealRetailAudit> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }
        setListFilter(predicate);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_retail_audit;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealRetailAudit item) {
        vsItem.textView(R.id.tv_product_name).setText(item.product.name);
        //check, if specified parameters have data, show details under the product row
        View llExtra = vsItem.id(R.id.ll_extra_content);
        if (!item.soldPrice.isEmpty() || !item.soldQuant.isEmpty() || !item.faceQuant.isEmpty()) {
            llExtra.setVisibility(View.VISIBLE);
        } else {
            llExtra.setVisibility(View.GONE);
        }

        vsItem.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
        if (item.productPhoto != null && item.productPhoto.photos.nonEmpty()) {
            jobMate.execute(new FetchImageJob(getArgDeal().accountId, item.productPhoto.photos.get(0).fileSha))
                    .always(new Promise.OnAlways<Bitmap>() {
                        @Override
                        public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                            if (resolved) {
                                if (result != null) {
                                    vsItem.imageView(R.id.iv_product_photo).setImageBitmap(result);
                                } else {
                                    vsItem.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
                                }
                            } else {
                                vsItem.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
                                if (error != null) error.printStackTrace();
                            }
                        }
                    });
        }

        vsItem.bind(R.id.et_sold_quant, item.soldQuant);
        vsItem.bind(R.id.et_sold_price, item.soldPrice);
        vsItem.bind(R.id.et_sold_facing, item.faceQuant);
    }
}
