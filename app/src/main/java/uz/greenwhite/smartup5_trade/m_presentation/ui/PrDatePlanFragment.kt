package uz.greenwhite.smartup5_trade.m_presentation.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import com.github.badoualy.datepicker.DatePickerTimeline
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_presentation.arg.ArgPrDatePlan
import java.util.*

class PrDatePlanFragment : MoldContentFragment() {

    companion object {
        fun open(activity: Activity, arg: ArgPrDatePlan) {
            val bundle = Mold.parcelableArgument<ArgPrDatePlan>(arg, ArgPrDatePlan.UZUM_ADAPTER);
            Mold.openContent(activity, PrDatePlanFragment::class.java, bundle)
        }
    }

    private val argPrDatePlan: ArgPrDatePlan by lazy { Mold.parcelableArgument<ArgPrDatePlan>(this, ArgPrDatePlan.UZUM_ADAPTER) }

    private var vsRoot: ViewSetup? = null;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.vsRoot = ViewSetup(inflater, container, R.layout.pr_plan_date)
        return this.vsRoot!!.view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


//        vsRoot!!.id<DatePickerTimeline>(R.id.dp_timeline).setFirstVisibleDate(2018, Calendar.JUNE, 1)
//        vsRoot!!.id<DatePickerTimeline>(R.id.dp_timeline).setLastVisibleDate(2020, Calendar.JUNE, 1)
    }

}