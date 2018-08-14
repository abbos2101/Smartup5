package uz.greenwhite.smartup5_trade.m_debtor.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.DebtorApi;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.data.DebtorData;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtorCurrency;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtorPayment;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;
import uz.greenwhite.smartup5_trade.m_report.ui.ReportViewFragment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class DebtorFragment extends MoldContentRecyclerFragment<VDebtorCurrency> {

    public static void open(ArgDebtor arg) {
        Mold.openContent(DebtorFragment.class, Mold.parcelableArgument(arg, ArgDebtor.UZUM_ADAPTER));
    }

    public ArgDebtor getArgDebtor() {
        return Mold.parcelableArgument(this, ArgDebtor.UZUM_ADAPTER);
    }

    private DebtorData data;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArgDebtor arg = getArgDebtor();
        data = Mold.getData(getActivity());
        if (data == null) {
            data = new DebtorData(arg.getScope(), arg);
            data.vDebtor.readyToChange();
            Mold.setData(getActivity(), data);
        }

        addMenu(R.drawable.ic_list_black_24dp, R.string.rep_title_003, new Command() {
            @Override
            public void apply() {
                MyArray<String> d = MyArray.from(arg.filialId, arg.outletId, arg.dealId);
                ReportViewFragment.open(new ArgReport(getArgDebtor(), RT.FMCG_REP_005, d));
            }
        });

        if (data.vDebtor.entryState.isSaved() || data.vDebtor.entryState.isNotSaved()) {
            addMenu(R.drawable.ic_done_black_24dp, R.string.save, new Command() {
                @Override
                public void apply() {
                    saveDebtor();
                }
            });
        } else if (data.vDebtor.entryState.isReady()) {
            Mold.makeFloatAction(getActivity(), R.drawable.ic_edit_black_24dp)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editDebtor();
                        }
                    });
        }

        ViewSetup vs = setHeader(R.layout.debtor_header);
        if (TextUtils.isEmpty(data.vDebtor.info.roomName)) {
            vs.textView(R.id.tv_room_name).setVisibility(View.GONE);
        }
        vs.textView(R.id.tv_room_name).setText(
                DS.getString(R.string.outlet_info_room, data.vDebtor.info.roomName));
        vs.textView(R.id.tv_agent_name).setText(
                DS.getString(R.string.agent, data.vDebtor.info.getAgentName()));
        vs.textView(R.id.tv_ex_name).setText(
                DS.getString(R.string.expeditor, data.vDebtor.info.getExpeditorName()));

        if (!TextUtils.isEmpty(data.vDebtor.info.dealDeliveryDate)) {
            vs.textView(R.id.tv_delivery_date).setVisibility(View.VISIBLE);
            vs.textView(R.id.tv_delivery_date).setText(
                    DS.getString(R.string.delivery_date, data.vDebtor.info.dealDeliveryDate));
        }

        if (!TextUtils.isEmpty(data.vDebtor.info.consignDate)) {
            vs.textView(R.id.tv_consign_date).setVisibility(View.VISIBLE);
            vs.textView(R.id.tv_consign_date).setText(
                    DS.getString(R.string.consign_date, data.vDebtor.info.consignDate));
        }

        if (data.vDebtor.contract != null) {
            vs.id(R.id.ll_contract).setVisibility(View.VISIBLE);
            vs.textView(R.id.tv_contract).setText(data.vDebtor.contract.contractNumber);
        }

        TextView tvError = vs.textView(R.id.tv_error);
        tvError.setVisibility(data.hasError() ? View.VISIBLE : View.GONE);
        if (data.hasError()) {
            tvError.setText(data.vDebtor.entryState.getErrorText());
        }

        Outlet outlet = arg.getOutlet();
        Mold.setTitle(getActivity(), outlet.name);
        setListItems(MyArray.from(data.vDebtor.currency));
    }

    private void saveDebtor() {
        try {
            ErrorResult error = data.vDebtor.getError();
            if (error.isError()) {
                UI.alertError(getActivity(), error.getErrorMessage());
                return;
            }
            save(true);
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    private void save(boolean ready) {
        try {
            ArgDebtor arg = getArgDebtor();
            if (data.vDebtor.hasValue()) {
                Debtor debtor = data.vDebtor.convert();
                DebtorApi.saveDeal(arg.getScope(), debtor, ready);
            } else if (data.vDebtor.entryState.isSaved()) {
                DebtorApi.debtorDelete(arg.getScope(), data.vDebtor.info.localId);
            }
            getActivity().finish();
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    private void editDebtor() {
        try {
            ArgDebtor arg = getArgDebtor();
            Scope scope = arg.getScope();
            DebtorApi.debtorMakeEdit(scope, data.vDebtor.info.localId);
            DebtorFragment.open(getArgDebtor());
            getActivity().finish();
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (data != null && data.vDebtor.modified() && data.hasEdit()) {
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

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.debtor_row_header;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, final VDebtorCurrency item) {
        vsItem.textView(R.id.tv_title).setText(item.currency.getName());

        ViewGroup vg = vsItem.viewGroup(R.id.ll_row);
        vg.removeAllViews();

        FragmentActivity activity = getActivity();
        ViewSetup vs = prepareRow(activity, null, vg, item, item.debtPayment, false);
        if (item.consignPayment != null) {
            prepareRow(activity, vs, vg, item, item.consignPayment, true);
        }
    }

    public ViewSetup prepareRow(Activity activity,
                                final ViewSetup vsOld,
                                final ViewGroup vg,
                                final VDebtorCurrency item,
                                final VDebtorPayment payment,
                                final boolean consign) {
        final ViewSetup vs = new ViewSetup(activity, R.layout.debtor_row_item);

        vs.textView(R.id.tv_title).setText(payment.tvTitle());
        vs.bind(R.id.et_amount, payment.amount);
        vs.id(R.id.et_amount).setEnabled(data.hasEdit());
        vg.addView(vs.view);

        if (consign && data.hasEdit()) {
            vsOld.model(R.id.et_amount).add(new ModelChange() {
                @Override
                public void onChange() {
                    if (item.debtPayment.amount.getValue() != null &&
                            item.debtPayment.amount.getValue().compareTo(item.debtPayment.debtAmount) == 0) {
                        vs.id(R.id.et_amount).setEnabled(true);
                        vs.id(R.id.btn_clear).setEnabled(true);
                        vs.id(R.id.btn_all).setEnabled(true);

                        vs.textView(R.id.tv_message).setVisibility(View.GONE);
                    } else {
                        vs.id(R.id.et_amount).setEnabled(false);
                        vs.editText(R.id.et_amount).setText("");
                        vs.id(R.id.btn_clear).setEnabled(false);
                        vs.id(R.id.btn_all).setEnabled(false);

                        vs.textView(R.id.tv_message).setVisibility(View.VISIBLE);
                        vs.textView(R.id.tv_message).setText(UI.html().c("#ED7822")
                                .v(DS.getString(R.string.debtor_before_necessary_to_repay_the_current_debt)).c().html());
                    }
                }
            }).notifyListeners();
        }

        if (data.hasEdit()) {
            vs.id(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vs.editText(R.id.et_amount).setText("");
                }
            });

            vs.id(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vs.editText(R.id.et_amount).setText(payment.debtAmount.toPlainString());
                }
            });
        }

        vs.model(R.id.et_amount).add(new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = item.getError();
                TextView tvError = vs.textView(R.id.tv_error);
                if (error.isError()) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(error.getErrorMessage());
                } else {
                    tvError.setVisibility(View.GONE);
                }
            }
        }).notifyListeners();
        return vs;
    }
}
