package uz.greenwhite.smartup5_trade.m_stocktaking.ui

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.util.Util
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.ErrorUtil
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_stocktaking.arg.ArgStocktaking

class StocktakingFragment : MoldContentFragment() {

    companion object {
        fun open(activity: Activity, arg: ArgStocktaking) {
            Mold.openContent(activity, StocktakingFragment::class.java,
                    Mold.parcelableArgument<ArgStocktaking>(arg, ArgStocktaking.UZUM_ADAPTER))
        }
    }

    private val argStocktaking: ArgStocktaking by lazy { Mold.parcelableArgument<ArgStocktaking>(this, ArgStocktaking.UZUM_ADAPTER) }


    private var vsRoot: ViewSetup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.vsRoot = ViewSetup(inflater, container, R.layout.stocktaking)
        return vsRoot!!.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.setTitle(activity, R.string.warehouse_stocktaking)
        Mold.setSubtitle(activity, argStocktaking.warehouse.name)

        if (Mold.getData<StocktakingData>(activity) == null) {
            val stocktakingHolder = argStocktaking.stocktakingHolder ?: return
            ScopeUtil.executeWithDialog(activity, jobMate, argStocktaking, object : OnScopeReadyCallback<StocktakingData>() {

                override fun onScopeReady(scope: Scope?): StocktakingData = StocktakingData(scope, stocktakingHolder)

                override fun onDone(data: StocktakingData) {
                    Mold.setData(activity, data)
                    makeView()
                }

                override fun onFail(throwable: Throwable?) {
                    super.onFail(throwable)
                    UI.alertError(activity!!, ErrorUtil.getErrorMessage(throwable).message as String)
                }
            })
        } else {
            makeView()
        }
    }

    fun makeView() {
        val data = Mold.getData<StocktakingData>(activity) ?: return

        vsRoot!!.textView(R.id.tv_date).text = UI.html().v(DS.getString(R.string.incoming_date)).fRed().v(" *").fRed().html()
        vsRoot!!.textView(R.id.tv_number).text = UI.html().v(DS.getString(R.string.stocktaking_number)).fRed().v(" *").fRed().html()
        vsRoot!!.textView(R.id.tv_currency).text = UI.html().v(DS.getString(R.string.incoming_currency)).fRed().v(" *").fRed().html()

        vsRoot!!.bind(R.id.et_number, data.vStocktaking.vHeader.vNumber)
        vsRoot!!.bind(R.id.et_date, data.vStocktaking.vHeader.vDate)
        vsRoot!!.bind(R.id.et_note, data.vStocktaking.vHeader.vNote)

        UI.bind(vsRoot!!.spinner(R.id.sp_currency), data.vStocktaking.vHeader.vCurrency, true)

        vsRoot!!.spinner(R.id.sp_currency).isEnabled = data.vStocktaking.vProducts.items.none({ it.hasValue() })

        val vsFooter = ViewSetup(activity, R.layout.incoming_footer)
        val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        vsFooter.id<View>(R.id.btn_close).setOnClickListener { activity.finish() }
        vsFooter.id<View>(R.id.btn_done).setOnClickListener { openProducts() }
        vsFooter.id<View>(R.id.btn_products).setOnClickListener { openProducts() }

        val state = data.vStocktaking.holder.state
        if (state.isNotSaved || state.isSaved) {
            vsFooter.id<View>(R.id.btn_close).visibility = View.VISIBLE
            vsFooter.id<View>(R.id.btn_done).visibility = View.VISIBLE
        } else {
            vsFooter.id<View>(R.id.btn_products).visibility = View.VISIBLE
        }

        val viewEnable = data.vStocktaking.holder.state.isSaved || data.vStocktaking.holder.state.isNotSaved

        vsRoot!!.id<View>(R.id.et_number).isEnabled = viewEnable
        vsRoot!!.id<View>(R.id.sp_currency).isEnabled = viewEnable
        vsRoot!!.id<View>(R.id.et_note).isEnabled = viewEnable
    }

    private fun openProducts() {
        val data = Mold.getData<StocktakingData>(activity)
        val error = data.vStocktaking.vHeader.error

        if (error.isError) {
            UI.alertError(activity, error.errorMessage)
        } else {
            Mold.addContent(activity, StocktakingProductFragment.newInstance(argStocktaking))
        }
    }

    override fun onBackPressed(): Boolean {
        UI.dialog()
                .title(R.string.warning)
                .message(R.string.exit_dont_save)
                .negative(R.string.no, Util.NOOP)
                .positive(R.string.yes) { activity.finish() }.show(activity)
        return true
    }
}