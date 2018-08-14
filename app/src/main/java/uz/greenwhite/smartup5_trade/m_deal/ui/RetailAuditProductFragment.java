package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;

import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgRetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;

public class RetailAuditProductFragment extends DealFormContentFragment implements View.OnClickListener {

    public ArgRetailAuditProduct getArgRetailAuditProduct() {
        return Mold.parcelableArgument(this, ArgRetailAuditProduct.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_retail_audit_product);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VDealRetailAuditForm form = DealUtil.getDealForm(this);

        Mold.setTitle(getActivity(), form.retailAuditSet.name);

        ArgRetailAuditProduct arg = getArgRetailAuditProduct();
        VDealRetailAudit vDealRetailAudit = form.retailAudits.getItems().find(arg.productId, VDealRetailAudit.KEY_ADAPTER);

        vsRoot.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
        if (vDealRetailAudit.productPhoto != null && vDealRetailAudit.productPhoto.photos.nonEmpty()) {
            jobMate.execute(new FetchImageJob(arg.accountId, vDealRetailAudit.productPhoto.photos.get(0).fileSha))
                    .always(new Promise.OnAlways<Bitmap>() {
                        @Override
                        public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                            if (resolved) {
                                if (result != null) {
                                    vsRoot.imageView(R.id.iv_product_photo).setImageBitmap(result);
                                } else {
                                    vsRoot.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
                                }
                            } else {
                                vsRoot.imageView(R.id.iv_product_photo).setImageResource(R.drawable.display_photo);
                                if (error != null) error.printStackTrace();
                            }
                        }
                    });
        }

        vsRoot.textView(R.id.tv_product_name).setText(vDealRetailAudit.product.name);

        vsRoot.bind(R.id.et_sold_price, vDealRetailAudit.soldPrice);
        vsRoot.bind(R.id.et_input_price, vDealRetailAudit.inputPrice);
        vsRoot.bind(R.id.et_income_quant, vDealRetailAudit.incomeQuant);
        vsRoot.bind(R.id.et_sold_quant, vDealRetailAudit.soldQuant);
        vsRoot.bind(R.id.et_shelf_share, vDealRetailAudit.shelfShare);
        vsRoot.bind(R.id.cb_shelf_hight, vDealRetailAudit.shelfHight);
        vsRoot.bind(R.id.cb_shelf_medium, vDealRetailAudit.shelfMedium);
        vsRoot.bind(R.id.cb_shelf_low, vDealRetailAudit.shelfLow);

        vsRoot.id(R.id.btn_sold_price_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_sold_price), vDealRetailAudit.soldPrice));
        vsRoot.id(R.id.btn_input_price_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_input_price), vDealRetailAudit.inputPrice));
        vsRoot.id(R.id.btn_income_quant_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_income_quant), vDealRetailAudit.incomeQuant));
        vsRoot.id(R.id.btn_sold_quant_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_sold_quant), vDealRetailAudit.soldQuant));
        vsRoot.id(R.id.btn_shelf_share_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_shelf_share), vDealRetailAudit.shelfShare));

        vsRoot.id(R.id.btn_sold_price_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_sold_price), vDealRetailAudit.soldPrice));
        vsRoot.id(R.id.btn_input_price_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_input_price), vDealRetailAudit.inputPrice));
        vsRoot.id(R.id.btn_income_quant_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_income_quant), vDealRetailAudit.incomeQuant));
        vsRoot.id(R.id.btn_sold_quant_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_sold_quant), vDealRetailAudit.soldQuant));
        vsRoot.id(R.id.btn_shelf_share_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_shelf_share), vDealRetailAudit.shelfShare));

        vsRoot.id(R.id.miv_extra_face_change).setOnClickListener(this);

        makeError(R.id.et_sold_price, R.id.tv_sold_price_error, vDealRetailAudit.soldPrice);
        makeError(R.id.et_input_price, R.id.tv_input_price_error, vDealRetailAudit.inputPrice);
        makeError(R.id.et_income_quant, R.id.tv_income_quant_error, vDealRetailAudit.incomeQuant);
        makeError(R.id.et_sold_quant, R.id.tv_sold_quant_error, vDealRetailAudit.soldQuant);
        makeError(R.id.et_shelf_share, R.id.tv_shelf_share_error, vDealRetailAudit.shelfShare);
    }

    @Override
    public void onStart() {
        super.onStart();
        VDealRetailAuditForm form = DealUtil.getDealForm(this);
        ArgRetailAuditProduct arg = getArgRetailAuditProduct();
        VDealRetailAudit vDealRetailAudit = form.retailAudits.getItems()
                .find(arg.productId, VDealRetailAudit.KEY_ADAPTER);

        if (vDealRetailAudit.hasExtraFacingWithoutOthers()) {
            final EditText et = vsRoot.editText(R.id.et_stock_quant);
            et.setOnClickListener(null);
            et.setOnLongClickListener(null);
            et.setKeyListener(null);
            et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        onClick(et);
                    }
                    return false;
                }
            });
            vsRoot.id(R.id.btn_stock_quant_plus).setOnClickListener(this);
            vsRoot.id(R.id.btn_stock_quant_minus).setOnClickListener(this);

            vsRoot.editText(R.id.et_stock_quant).setText(vDealRetailAudit.getExtraFacingQuantity().toPlainString());

        } else {
            vsRoot.bind(R.id.et_stock_quant, vDealRetailAudit.extFaceOtherQuant);

            vsRoot.id(R.id.btn_stock_quant_plus).setOnClickListener(
                    makePlusBtn(vsRoot.editText(R.id.et_stock_quant), vDealRetailAudit.extFaceOtherQuant));

            vsRoot.id(R.id.btn_stock_quant_minus).setOnClickListener(
                    makeMinusBtn(vsRoot.editText(R.id.et_stock_quant), vDealRetailAudit.extFaceOtherQuant));

            makeError(R.id.et_stock_quant, R.id.tv_stock_quant_error, vDealRetailAudit.extFaceOtherQuant);
        }
    }

    private void makeError(int valueResId, final int errorResId, final Variable variable) {
        vsRoot.model(valueResId).add(new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = variable.getError();
                vsRoot.textView(errorResId).setText(error.getErrorMessage());
            }
        }).notifyListeners();
    }

    private View.OnClickListener makeMinusBtn(final EditText editText, final ValueBigDecimal value) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value.nonZero()) {
                    editText.setText(value.getQuantity().subtract(BigDecimal.ONE).toPlainString());
                }
            }
        };
    }

    private View.OnClickListener makePlusBtn(final EditText editText, final ValueBigDecimal value) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(value.getQuantity().add(BigDecimal.ONE).toPlainString());
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_stock_quant:
            case R.id.btn_stock_quant_plus:
            case R.id.btn_stock_quant_minus:
            case R.id.miv_extra_face_change:
                Mold.addContent(getActivity(), DealUtil.newInstance(getArgRetailAuditProduct(),
                        RetailAuditProductExtraFragment.class, DealUtil.getFormCode(this)));
                break;
        }
    }
}
