package uz.greenwhite.smartup5_trade.m_presentation.ui

import android.os.Bundle
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_presentation.arg.ArgPrDatePlan
import uz.greenwhite.smartup5_trade.m_presentation.bean.PrPlan
import uz.greenwhite.smartup5_trade.m_presentation.row.PrPlanRow
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession

class PrPresentationListFragment : MoldContentRecyclerFragment<PrPlanRow>() {

    companion object {
        fun newInstance(argSession: ArgSession): PrPresentationListFragment {
            val bundle = Mold.parcelableArgument<ArgSession>(argSession, ArgSession.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(PrPresentationListFragment::class.java, bundle)
        }
    }

    private val argSession: ArgSession by lazy { Mold.parcelableArgument<ArgSession>(this, ArgSession.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Mold.makeFloatAction(activity, R.drawable.ic_add_black_24dp).setOnClickListener {

            fun openPrDate(type: String) {
                PrDatePlanFragment.open(activity, ArgPrDatePlan(argSession, type))
            }

            UI.dialog().title(R.string.select)
                    .option("Medical care facility (MCF)", { openPrDate(PrPlan.T_MCF) })
                    .option("Pharmacy", { openPrDate(PrPlan.T_PHARMACY) })
                    .option("Doctor", { openPrDate(PrPlan.T_DOCTOR) })
                    .show(activity)
        }
    }

    override fun adapterGetLayoutResource(): Int {
        return R.layout.pr_plan_row
    }

    override fun adapterPopulate(viewSetup: ViewSetup, item: PrPlanRow) {

    }
}
