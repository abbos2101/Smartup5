package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturn;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnForm;

public class ReturnFragment extends DealFormRecyclerFragment<VDealReturn> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.deal_products);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealReturn item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        setHeader(R.layout.deal_return_header);

        setEmptyText(R.string.list_is_empty);
        reloadContent();
    }

    @Override
    public void reloadContent() {
        VDealReturnForm form = DealUtil.getDealForm(this);
        setListItems(form.returns.getItems().sort(new Comparator<VDealReturn>() {
            @Override
            public int compare(VDealReturn l, VDealReturn r) {
                int compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                }
                return compare;
            }
        }));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_return;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealReturn item) {
        vsItem.textView(R.id.tv_title).setText(item.product.name);

        vsItem.bind(R.id.et_quantity, item.quantity);
        vsItem.bind(R.id.et_price, item.price);
        vsItem.bind(R.id.et_expiry_date, item.expiryDate);
        vsItem.bind(R.id.et_card_code, item.cardCode);

        vsItem.id(R.id.iv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VDealReturnForm form = DealUtil.getDealForm(ReturnFragment.this);
                form.copy(item);
                reloadContent();
            }
        });

        ModelChange onChange = new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = item.getError();
                TextView tvError = vsItem.textView(R.id.et_error);

                if (error.isError()) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(error.getErrorMessage());

                } else {
                    tvError.setText("");
                    tvError.setVisibility(View.GONE);
                }
            }
        };

        onChange.onChange();

        vsItem.model(R.id.et_quantity).add(onChange);
        vsItem.model(R.id.et_price).add(onChange);

        vsItem.makeDatePicker(R.id.et_expiry_date, true);
    }
}
