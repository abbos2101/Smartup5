package uz.greenwhite.smartup5_trade.m_debtor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
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
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.DebtorApi;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.data.PrepaymentData;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepaymentCurrency;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepaymentPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class PrepaymentFragment extends MoldContentRecyclerFragment<VPrepaymentCurrency> {

    public static void open(ArgDebtor arg) {
        Mold.openContent(PrepaymentFragment.class, Mold.parcelableArgument(arg, ArgDebtor.UZUM_ADAPTER));
    }

    public ArgDebtor getArgDebtor() {
        return Mold.parcelableArgument(this, ArgDebtor.UZUM_ADAPTER);
    }

    private PrepaymentData data;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArgDebtor arg = getArgDebtor();
        data = Mold.getData(getActivity());
        if (data == null) {
            data = new PrepaymentData(arg.getScope(), arg);
            data.vDebtor.readyToChange();
            Mold.setData(getActivity(), data);
        }

        if (data.vDebtor.entryState.isSaved() || data.vDebtor.entryState.isNotSaved()) {
            addMenu(R.drawable.ic_save_black_24dp, R.string.save, new Command() {
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

        if (data.hasError()) {
            ViewSetup vs = setHeader(R.layout.debtor_header);

            TextView tvError = vs.textView(R.id.tv_error);
            tvError.setVisibility(data.hasError() ? View.VISIBLE : View.GONE);

            vs.id(R.id.tv_room_name).setVisibility(View.GONE);
            vs.id(R.id.tv_agent_name).setVisibility(View.GONE);
            vs.id(R.id.tv_ex_name).setVisibility(View.GONE);
            vs.id(R.id.ll_contract).setVisibility(View.GONE);
            tvError.setText(data.vDebtor.entryState.getErrorText());
        }

        Outlet outlet = arg.getOutlet();
        Mold.setTitle(getActivity(), outlet.name);
        Mold.setSubtitle(getActivity(), DS.getString(R.string.prepayment));
        setListItems(data.vDebtor.currencies.getItems());
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
            Debtor debtor = data.vDebtor.convert();
            DebtorApi.saveDeal(arg.getScope(), debtor, ready);
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
            DebtorApi.debtorMakeEdit(scope, data.vDebtor.entryId);
            PrepaymentFragment.open(getArgDebtor());
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
    protected void adapterPopulate(ViewSetup vsItem, final VPrepaymentCurrency item) {
        vsItem.textView(R.id.tv_title).setText(item.currency.getName());

        ViewGroup vg = vsItem.viewGroup(R.id.ll_row);
        vg.removeAllViews();

        FragmentActivity activity = getActivity();
        for (final VPrepaymentPayment payment : item.payments.getItems()) {
            final ViewSetup vs = new ViewSetup(activity, R.layout.debtor_row_item);

            vs.textView(R.id.tv_title).setText(payment.tvTitle());
            vs.bind(R.id.et_amount, payment.amount);
            vs.id(R.id.et_amount).setEnabled(data.hasEdit());
            vg.addView(vs.view);

            vs.id(R.id.btn_all).setVisibility(View.GONE);

            if (data.hasEdit()) {
                vs.id(R.id.btn_clear).setVisibility(View.VISIBLE);
                vs.id(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vs.editText(R.id.et_amount).setText("");
                    }
                });
            } else {
                vs.id(R.id.btn_clear).setVisibility(View.GONE);
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
        }
    }
}
