package uz.greenwhite.smartup5_trade.m_deal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.error.UserError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealData;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealAction;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverload;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuizExtra;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizOption;

public class DealUtil {

    public static MoldContentFragment newInstance(Class<? extends MoldContentFragment> cls, String formCode) {
        try {
            MoldContentFragment f = cls.newInstance();

            Bundle arg = new Bundle();
            arg.putString("form_code", formCode);
            f.setArguments(arg);

            return f;
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static MoldContentFragment newInstance(ArgDeal arg, Class<? extends MoldContentFragment> cls, String formCode) {
        try {
            MoldContentFragment f = cls.newInstance();

            Bundle bundle = Mold.parcelableArgument(arg, ArgDeal.UZUM_ADAPTER);
            bundle.putString("form_code", formCode);
            f.setArguments(bundle);

            return f;
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static String getFormCode(Fragment fragment) {
        return fragment.getArguments().getString("form_code");
    }

    public static <T extends VDealForm> T getDealForm(Fragment fragment) {
        DealData dealData = Mold.getData(fragment.getActivity());
        return dealData.vDeal.findForm(getFormCode(fragment));
    }

    public static <T extends VDealForm> T getDealForm(Activity activity, String formCode) {
        DealData dealData = Mold.getData(activity);
        return dealData.vDeal.findForm(formCode);
    }

    //----------------------------------------------------------------------------------------------

    public static boolean isEqualDealStartDate(Scope scope) {
        String device = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        Filial filial = scope.ref.getFilial(scope.filialId);
        if (TextUtils.isEmpty(filial.serverTime)) {
            return true;
        }
        String serverDate = DateUtil.convert(filial.serverTime, DateUtil.FORMAT_AS_DATE);
        boolean dateEqual = device.equals(serverDate);
        if (dateEqual) {
            MyArray<DealHolder> deals = DSUtil.getAllDeals(scope).sort(new Comparator<DealHolder>() {
                @Override
                public int compare(DealHolder l, DealHolder r) {
                    return CharSequenceUtil.compareToIgnoreCase(r.deal.header.begunOn, l.deal.header.begunOn);
                }
            });

            long st = DateUtil.parse(filial.serverTime).getTime();
            st = st - (5 * 60) * 1000; // 5 minute
            if (deals.nonEmpty()) {
                st = Math.max(st, DateUtil.parse(deals.get(0).deal.header.begunOn).getTime());
            }
            if ((System.currentTimeMillis() - st) < 0) {
                return false;
            }
        }
        return dateEqual;
    }

    public static boolean checkDealEndDate(Scope scope, Deal deal) {
        Filial filial = scope.ref.getFilial(scope.filialId);
        if (TextUtils.isEmpty(filial.serverTime)) {
            return true;
        }
        String serverTime = DateUtil.convert(filial.serverTime, DateUtil.FORMAT_AS_DATE);
        String endedOn = DateUtil.convert(deal.header.endedOn, DateUtil.FORMAT_AS_DATE);
        if (!serverTime.equals(endedOn)) {
            throw new UserError(DS.getString(R.string.error_server_and_begin_on_not_equal));
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------

    public static void addClearButton(final Activity activity, LinearLayout cnt, final MyArray<View> filters) {
        LayoutInflater inflater = LayoutInflater.from(cnt.getContext());
        Button button = (Button) inflater.inflate(uz.greenwhite.lib.R.layout.gwslib_filter_button, null);
        button.setText(R.string.deal_gift_tuning_cancel_all);
        cnt.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (View fv : filters) {
                    if (fv instanceof CompoundButton) {
                        ((CompoundButton) fv).setChecked(false);
                    } else if (fv instanceof Spinner) {
                        ((Spinner) fv).setSelection(0);
                    } else if (fv instanceof EditText) {
                        ((EditText) fv).setText("");
                    } else {
                        throw AppError.Unsupported();
                    }
                }
                Mold.closeTuningDrawer(activity);
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    public static MyPredicate<VDealPaymentCurrency> getPaymentCurrencyPredicate(VDeal vDeal) {
        vDeal.checkModuleForms();

        VDealOrderModule orderModule = (VDealOrderModule) vDeal.modules.getItems()
                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);

        VDealServiceModule serviceModule = (VDealServiceModule) vDeal.modules.getItems()
                .find(VisitModule.M_SERVICE, VDealModule.KEY_ADAPTER);

        VDealPaymentModule paymentModule = (VDealPaymentModule) vDeal.modules.getItems()
                .find(VisitModule.M_PAYMENT, VDealModule.KEY_ADAPTER);

        if ((orderModule == null && serviceModule == null) || paymentModule == null) {
            return MyPredicate.False();
        }
        final Set<String> ids = new HashSet<>();
        MyArray<String> pIds = MyArray.emptyArray();

        if (orderModule != null) {
            for (VDealOrderForm f : orderModule.orderForms.getItems()) {
                if (f.hasValue() && f.enable) {
                    ids.add(f.currency.currencyId);
                    pIds = f.priceType.paymentTypeIds.isEmpty() ? pIds :
                            (pIds.isEmpty() ? f.priceType.paymentTypeIds : Utils.intersect(pIds, f.priceType.paymentTypeIds));
                }
            }
        }

        if (serviceModule != null) {
            for (VDealServiceForm f : serviceModule.forms.getItems()) {
                if (f.hasValue() && f.enable) {
                    ids.add(f.currency.currencyId);
                    pIds = f.priceType.paymentTypeIds.isEmpty() ? pIds :
                            (pIds.isEmpty() ? f.priceType.paymentTypeIds : Utils.intersect(pIds, f.priceType.paymentTypeIds));
                }
            }
        }

        for (VDealPaymentCurrency p : paymentModule.form.payment.getItems()) {
            if (p.hasValue()) {
                ids.add(p.currency.currencyId);
            }
        }
        final MyArray<String> finalPIds = pIds;
        return new MyPredicate<VDealPaymentCurrency>() {
            @Override
            public boolean apply(VDealPaymentCurrency val) {
                boolean contains = ids.contains(val.currency.currencyId);
                if (contains) {
                    val.setPaymentIds(finalPIds);
                }
                return contains;
            }
        };
    }

    public static MyPredicate<VDealPaymentCurrency> getPaymentCurrencyPredicate(Fragment fragment) {
        DealData data = Mold.getData(fragment.getActivity());
        return getPaymentCurrencyPredicate(data.vDeal);
    }

    //----------------------------------------------------------------------------------------------

    public static void makeOverload(DealData data) {
        MyArray<VDealModule> items = data.vDeal.modules.getItems();
        VDealOrderModule orderModule = (VDealOrderModule) items.find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VOverloadModule overloadModule = (VOverloadModule) items.find(VisitModule.M_OVERLOAD, VDealModule.KEY_ADAPTER);
        if (overloadModule.form != null) {
            makeOverload(orderModule, overloadModule.form);
        }
    }

    public static void makeOverload(VDealOrderModule module, VOverloadForm overloadForm) {
        if (module == null) return;

        // Tuple3 first = ProductQuant, second = ProductAmount, third = ProductWeight
        Tuple3 productInfo = module.getProductInfoForOverload();
        for (VOverload overload : overloadForm.overloads.getItems()) {
            overload.suitableConditions(
                    (HashMap<Pair<String, String>, HashMap<String, BigDecimal>>) productInfo.first,  // ProductQuant
                    (HashMap<Pair<String, String>, HashMap<String, BigDecimal>>) productInfo.second, // ProductAmount
                    (HashMap<Pair<String, String>, HashMap<String, BigDecimal>>) productInfo.third); // ProductWeight
        }
    }

    //----------------------------------------------------------------------------------------------

    public static MyArray<VDealQuiz> filterQuizChild(VDealQuiz vDealQuiz) {
        MyPredicate<VDealQuiz> predicate = MyPredicate.False();

        switch (vDealQuiz.quiz.quizType) {
            case Quiz.BOOLEAN:
                final boolean value = ((ValueBoolean) vDealQuiz.answer).getValue();
                predicate = new MyPredicate<VDealQuiz>() {
                    @Override
                    public boolean apply(VDealQuiz item) {
                        return (value && "1".equals(item.optionId) || !value && "0".equals(item.optionId));
                    }
                };
                break;

            case Quiz.NUMBER:
                final boolean hasValue = vDealQuiz.hasValue();
                predicate = new MyPredicate<VDealQuiz>() {
                    @Override
                    public boolean apply(VDealQuiz item) {
                        return hasValue && "0".equals(item.optionId);
                    }
                };
                break;

            case Quiz.SELECT:
            case Quiz.SELECT_WITH_CODE:
            case Quiz.AUTOCOMPLETE:
                final SpinnerOption option;
                if (vDealQuiz.quiz.extraOption) {
                    option = ((VDealQuizExtra) vDealQuiz.answer).spinner.getValue();
                } else {
                    option = ((ValueSpinner) vDealQuiz.answer).getValue();
                }
                predicate = new MyPredicate<VDealQuiz>() {
                    @Override
                    public boolean apply(VDealQuiz item) {
                        return option.tag != null && ((QuizOption) option.tag).optionId.equals(item.optionId);
                    }
                };
                break;
        }
        return vDealQuiz.childQuizes.getItems().filter(predicate);
    }

    //----------------------------------------------------------------------------------------------

    public static void makeAction(DealData data) {
        MyArray<VDealModule> items = data.vDeal.modules.getItems();
        VDealOrderModule orderModule = (VDealOrderModule) items.find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VDealActionModule actionModule = (VDealActionModule) items.find(VisitModule.M_ACTION, VDealModule.KEY_ADAPTER);
        if (orderModule != null && actionModule != null) {
            makeAction(orderModule, actionModule.form);
        }
    }

    public static void makeAction(VDealOrderModule module, VDealActionForm actionForm) {
        if (module == null) return;

        // Tuple3 first = ProductQuant, second = ProductAmount, third = ProductWeight
        Tuple3 productInfo = module.getProductInfoForAction();
        for (VDealAction action : actionForm.actions.getItems()) {
            action.suitableConditions(
                    (HashMap<String, BigDecimal>) productInfo.first,  // ProductQuant
                    (HashMap<String, BigDecimal>) productInfo.second, // ProductAmount
                    (HashMap<String, BigDecimal>) productInfo.third); // ProductWeight
        }
    }

    //----------------------------------------------------------------------------------------------
}
