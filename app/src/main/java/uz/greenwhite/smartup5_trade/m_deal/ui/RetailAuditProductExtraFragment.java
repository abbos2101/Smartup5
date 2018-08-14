package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgRetailAuditProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;

public class RetailAuditProductExtraFragment extends DealFormContentFragment {

    public ArgRetailAuditProduct getArgRetailAuditProduct() {
        return Mold.parcelableArgument(this, ArgRetailAuditProduct.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_retail_audit_product_extra);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VDealRetailAuditForm form = DealUtil.getDealForm(this);

        Mold.setTitle(getActivity(), form.retailAuditSet.name);

        ArgRetailAuditProduct arg = getArgRetailAuditProduct();
        VDealRetailAudit vDealRetailAudit = form.retailAudits.getItems().find(arg.productId, VDealRetailAudit.KEY_ADAPTER);

        vsRoot.textView(R.id.tv_product_name).setText(vDealRetailAudit.product.name);

        vsRoot.bind(R.id.et_face_tray, vDealRetailAudit.extFaceTrayQuant);
        vsRoot.bind(R.id.et_face_fridge, vDealRetailAudit.extFaceFridgeQuant);
        vsRoot.bind(R.id.et_face_other, vDealRetailAudit.extFaceOtherQuant);
        vsRoot.bind(R.id.et_face_quant, vDealRetailAudit.faceQuant);

        vsRoot.id(R.id.btn_tray_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_face_tray), vDealRetailAudit.extFaceTrayQuant));
        vsRoot.id(R.id.btn_fridge_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_face_fridge), vDealRetailAudit.extFaceFridgeQuant));
        vsRoot.id(R.id.btn_other_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_face_other), vDealRetailAudit.extFaceOtherQuant));
        vsRoot.id(R.id.btn_face_quant_minus).setOnClickListener(makeMinusBtn(vsRoot.editText(R.id.et_face_quant), vDealRetailAudit.faceQuant));

        vsRoot.id(R.id.btn_tray_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_face_tray), vDealRetailAudit.extFaceTrayQuant));
        vsRoot.id(R.id.btn_fridge_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_face_fridge), vDealRetailAudit.extFaceFridgeQuant));
        vsRoot.id(R.id.btn_other_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_face_other), vDealRetailAudit.extFaceOtherQuant));
        vsRoot.id(R.id.btn_face_quant_plus).setOnClickListener(makePlusBtn(vsRoot.editText(R.id.et_face_quant), vDealRetailAudit.faceQuant));

        makeError(R.id.et_face_tray, R.id.tv_ext_tray_error, vDealRetailAudit.extFaceTrayQuant);
        makeError(R.id.et_face_fridge, R.id.tv_ext_fridge_error, vDealRetailAudit.extFaceFridgeQuant);
        makeError(R.id.et_face_other, R.id.tv_ext_others_error, vDealRetailAudit.extFaceOtherQuant);
        makeError(R.id.et_face_quant, R.id.tv_face_quant_error, vDealRetailAudit.faceQuant);
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

}
