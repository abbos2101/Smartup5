package uz.greenwhite.smartup5_trade.m_shipped.ui;// 08.09.2016

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.error.UserError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealModuleFragment;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedApi;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.arg.ArgSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class SDealIndexFragment extends DealModuleFragment<VDealForm> {

    public static void open(ArgSDeal arg) {
        Mold.openContent(SDealIndexFragment.class, Mold.parcelableArgument(arg, ArgSDeal.UZUM_ADAPTER));
    }

    public ArgSDeal getArgSDeal() {
        return Mold.parcelableArgument(this, ArgSDeal.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Mold.setTitle(getActivity(), R.string.shipped);
        final ArgSDeal arg = getArgSDeal();
        final SDealData data = Mold.getData(getActivity());
        if (data == null) {
            ScopeUtil.execute(jobMate, arg, new OnScopeReadyCallback<SDealData>() {
                @Override
                public SDealData onScopeReady(Scope scope) {
                    SDealData data = new SDealData(scope, arg.getSDealHolder(scope), arg.dealId);
                    data.vDeal.readyToChange();
                    data.formCode = data.vDeal.getFirstModuleFormCode();
                    return data;
                }

                @Override
                public void onDone(SDealData sDealData) {
                    Mold.setData(getActivity(), sDealData);
                    sDealData.vDeal.prepareOrderForm();
                    setListItems(sDealData.vDeal.modules.getItems());
                    makeFooter();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                }
            });
        } else {
            data.vDeal.prepareOrderForm();
            setListItems(data.vDeal.modules.getItems());
            makeFooter();
        }
    }

    private void makeFooter() {
        SDealData data = Mold.getData(getActivity());
        if (data == null) {
            return;
        }
        ViewSetup vsFooter = setFooter(R.layout.z_deal_module_footer);

        Button cSave = vsFooter.id(R.id.save);
        Button cMakeEditable = vsFooter.id(R.id.make_editable);
        Button cComplete = vsFooter.id(R.id.complete);

        cSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDealTry(false);
            }
        });
        cMakeEditable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable();
            }
        });
        cComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDealReady();
            }
        });

        switch (data.vDeal.sDealRef.holder.entryState.state) {
            case EntryState.NOT_SAVED:
            case EntryState.SAVED:
                cSave.setVisibility(View.VISIBLE);
                cComplete.setVisibility(View.VISIBLE);
                break;
            case EntryState.READY:
                cMakeEditable.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onListItemClick(VForm vForm) {
        SDealData sdealData = Mold.getData(getActivity());
        VSDealForm form = (VSDealForm) vForm;
        MoldContentFragment content = make(form);
        Mold.addContent(getActivity(), content, form.getTitle());
        sdealData.formCode = form.code;
    }

    public MoldContentFragment make(@NonNull VSDealForm form) {
        String formCode = form.code;
        switch (form.getFormId()) {

            case VisitModule.M_ORDER:
                return ShippedUtil.newInstance(SOrderFragment.class, formCode);

            case VisitModule.M_PAYMENT:
                return ShippedUtil.newInstance(SPaymentFragment.class, formCode);

            case VisitModule.M_ERROR:
                return ShippedUtil.newInstance(SErrorFragment.class, formCode);

            case VisitModule.M_INFO:
                return ShippedUtil.newInstance(getArgSDeal(), SOutletInfoFragment.class, formCode);

            case VisitModule.M_ATTACH:
                return ShippedUtil.newInstance(SAttachFragment.class, formCode);

            case VisitModule.M_REASON:
                return ShippedUtil.newInstance(SReturnReasonFragment.class, formCode);

            case VisitModule.M_NOTE:
                return ShippedUtil.newInstance(getArgSDeal(), SDealNoteFragment.class, formCode);

            case VisitModule.M_OVERLOAD:
                return ShippedUtil.newInstance(getArgSDeal(), SOverloadFragment.class, formCode);

            default:
                throw AppError.Unsupported();
        }
    }

    @Override
    public boolean onBackPressed() {
        SDealData data = Mold.getData(getActivity());
        if (data != null && data.vDeal.modified() && data.hasEdit()) {
            UI.dialog()
                    .title(R.string.warning)
                    .message(R.string.exit_dont_save)
                    .negative(R.string.no, Util.NOOP)
                    .positive(R.string.yes, new Command() {
                        @Override
                        public void apply() {
                            getActivity().finish();
                        }
                    }).show(getActivity());
            return true;
        }
        return super.onBackPressed();
    }

    //--------------------------------------------------------------------------------------------------

    private void makeEditable() {
        ArgSDeal arg = getArgSDeal();
        SDealData data = Mold.getData(getActivity());
        ShippedApi.dealMakeEdit(arg.getScope(), data.vDeal.sDealRef.holder);
        open(arg);
        getActivity().finish();
    }

    private void saveDealReady() {
        try {
            SDealData data = Mold.getData(getActivity());
            ErrorResult error = data.vDeal.getError();
            if (error.isError()) {
                throw new UserError(error.getErrorMessage());
            }
            UI.confirm(getActivity(),
                    getString(R.string.save),
                    getString(R.string.sdeal_prepare), new Command() {
                        @Override
                        public void apply() {
                            saveDealTry(true);
                        }
                    });
        } catch (Exception ex) {
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
        }
    }

    private void saveDealTry(boolean ready) {
        try {
            saveDeal(ready);
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorUtil.saveThrowable(ex);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
        }
    }

    private void saveDeal(boolean ready) {
        ArgSDeal arg = getArgSDeal();
        SDealData data = Mold.getData(getActivity());
        SDeal sDeal = data.vDeal.convertToSDeal();
        VSDeal vDeal = data.vDeal;
        ShippedApi.saveDeal(arg.getScope(), vDeal.sDealHolder.entryId, sDeal, ready);
        getActivity().finish();
    }
}
