package uz.greenwhite.smartup5_trade.m_presentation.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_presentation.arg.ArgPrAddPlan

class PrAddPlanFragment : MoldContentFragment() {

    companion object {
        fun open(activity: Activity, arg: ArgPrAddPlan) {
            val bundle = Mold.parcelableArgument<ArgPrAddPlan>(arg, ArgPrAddPlan.UZUM_ADAPTER);
            Mold.openContent(activity, PrAddPlanFragment::class.java, bundle)
        }
    }

    private val argPrAddPlan: ArgPrAddPlan by lazy { Mold.parcelableArgument<ArgPrAddPlan>(this, ArgPrAddPlan.UZUM_ADAPTER) }

    private var vsRoot: ViewSetup? = null;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.vsRoot = ViewSetup(inflater, container, R.layout.pr_plan)
        return this.vsRoot!!.view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

}
