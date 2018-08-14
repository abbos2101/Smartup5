package uz.greenwhite.smartup5_trade.m_outlet.builder;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatOption;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatQuiz;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResult;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResultDetail;
import uz.greenwhite.smartup5_trade.m_outlet.categorization.CatHolder;
import uz.greenwhite.smartup5_trade.m_outlet.variable.VCategorization;
import uz.greenwhite.smartup5_trade.m_outlet.variable.VCategorizationRow;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class BuilderCategorization {
    public static String stringify(VCategorization vCategorization) {
        CatResult catResult = vCategorization.toValue();
        return Uzum.toJson(catResult, CatResult.UZUM_ADAPTER);
    }

    public static VCategorization make(final Scope scope, final String outletId) {
        final Outlet outlet = DSUtil.getOutlet(scope, outletId);
        final MyArray<CatQuiz> catQuizes = scope.ref.getCatQuizes().filter(new MyPredicate<CatQuiz>() {
            @Override
            public boolean apply(CatQuiz catQuiz) {
                return catQuiz.outletTypeIds.contains(outlet.categorizationTypeId, MyMapper.<String>identity());
            }
        });

        final MyArray<CatHolder> catHolders = scope.entry.getCatHolders();
        final CatHolder entryFound = catHolders.findFirst(new MyPredicate<CatHolder>() {
            @Override
            public boolean apply(CatHolder catHolder) {
                return catHolder.outletCatQuiz.outletId.equals(outletId) &&
                        catHolder.outletCatQuiz.filialId.equals(scope.filialId);
            }
        });

        MyArray<VCategorizationRow> vCategorizationRows = catQuizes.map(new MyMapper<CatQuiz, VCategorizationRow>() {
            private ValueSpinner makeSpinner(final CatQuiz catQuiz) {
                final MyArray<SpinnerOption> options = catQuiz.catOptions.map(new MyMapper<CatOption, SpinnerOption>() {
                    @Override
                    public SpinnerOption apply(CatOption catOption) {
                        return new SpinnerOption(catOption.optionId, catOption.name, catOption);
                    }
                }).prepend(new SpinnerOption("0", DS.getString(R.string.not_selected)));
                SpinnerOption selected = options.get(0);
                if (entryFound != null) {
                    CatResultDetail catResultDetail = entryFound.outletCatQuiz.quizes
                            .find(catQuiz.quizId, CatResultDetail.KEY_ADAPTER);
                    if (catResultDetail != null) {
                        selected = Util.nvl(options.find(catResultDetail.optionId, SpinnerOption.KEY_ADAPTER), selected);
                    }
                }
                return new ValueSpinner(options, selected);
            }

            @Override
            public VCategorizationRow apply(CatQuiz catQuiz) {
                return new VCategorizationRow(catQuiz, makeSpinner(catQuiz));
            }
        }).sort(new Comparator<VCategorizationRow>() {
            @Override
            public int compare(VCategorizationRow l, VCategorizationRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.catQuiz.orderNo, r.catQuiz.orderNo);
            }
        });
        EntryState entryState = EntryState.NOT_SAVED_ENTRY;
        String entryId = "";
        if (entryFound != null) {
            entryState = entryFound.entryState;
            entryId = entryFound.entryId;
        } else {
            entryId = "" + AdminApi.nextSequence();
        }
        return new VCategorization(entryId, scope.filialId, outletId, outlet.categorizationTypeId,
                entryState, new ValueArray<>(vCategorizationRows));
    }
}