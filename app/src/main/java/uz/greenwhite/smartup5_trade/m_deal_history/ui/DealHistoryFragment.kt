package uz.greenwhite.smartup5_trade.m_deal_history.ui

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.collection.MyMapper
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.util.DateUtil
import uz.greenwhite.lib.uzum.Uzum
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.ErrorUtil
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.MyImageView
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.datasource.RT
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal
import uz.greenwhite.smartup5_trade.m_deal.ui.DealIndexFragment
import uz.greenwhite.smartup5_trade.m_deal_history.HistoryUtil
import uz.greenwhite.smartup5_trade.m_deal_history.arg.ArgHistory
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDeal
import uz.greenwhite.smartup5_trade.m_deal_history.row.HistoryRow
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob
import java.util.*

class DealHistoryFragment : MoldContentRecyclerFragment<HistoryRow>() {

    companion object {
        fun open(activity: Activity, arg: ArgHistory) {
            Mold.openContent(activity, DealHistoryFragment::class.java,
                    Mold.parcelableArgument(arg, ArgHistory.UZUM_ADAPTER))
        }
    }

    private val argHistory: ArgHistory by lazy { Mold.parcelableArgument(this, ArgHistory.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.setTitle(activity!!, R.string.outlet_deal_history)
    }

    override fun onStart() {
        super.onStart()
        reloadContent()
    }

    override fun reloadContent() {

        ScopeUtil.execute(jobMate, argHistory, object : OnScopeReadyCallback<MyArray<HistoryRow>>() {

            override fun onScopeReady(scope: Scope): MyArray<HistoryRow>? {
                val currencies = scope.ref.currencys
                val outlets = scope.ref.outlets

                val today = DateUtil.format(Date(), DateUtil.FORMAT_AS_DATE)

                return scope.ref.dealHistories.map(object : MyMapper<DealHistory, HistoryRow>() {
                    override fun apply(item: DealHistory): HistoryRow? {
                        if ((!TextUtils.isEmpty(argHistory.outletId) && argHistory.outletId != item.personId) ||
                                !outlets.contains(item.personId, Outlet.KEY_ADAPTER)) return null
                        return HistoryRow(outlets.find(item.personId, Outlet.KEY_ADAPTER), item, currencies)
                    }
                }).filterNotNull().filter(object : MyPredicate<HistoryRow>() {
                    override fun apply(item: HistoryRow): Boolean = today == item.history.dealDate
                })
            }

            override fun onDone(historyRows: MyArray<HistoryRow>) {
                listItems = historyRows.sort({ l, r -> MyPredicate.compare(l.history.stateIntValue, r.history.stateIntValue) })
            }

            override fun onFail(throwable: Throwable) {
                super.onFail(throwable)
                UI.alertError(activity, throwable)
            }
        })
    }

    override fun onItemClick(holder: RecyclerAdapter.ViewHolder?, item: HistoryRow) {
        val data = MyArray.from(item.history.dealId)
        jobMate.executeWithDialog(activity, ActionJob<MyArray<String>>(argHistory, RT.URI_LOAD_DEAL, data))
                .done({
                    openDealHistoryForEdit(item.history.dealId, Uzum.toValue(it, HDeal.UZUM_ADAPTER))
                })
                .fail({
                    UI.alertError(activity!!, ErrorUtil.getErrorMessage(it).message as String)
                })
    }

    private fun openDealHistoryForEdit(dealId: String, hDeal: HDeal) {
        val deal = HistoryUtil.historyDealToLocalDeal(argHistory.scope, dealId, hDeal)
        val argOutlet = ArgOutlet(argHistory, deal.outletId)
        DealIndexFragment.open(ArgDeal(argOutlet, deal))
    }

    override fun adapterGetLayoutResource(): Int {
        return R.layout.deal_history_row
    }

    override fun adapterPopulate(vs: ViewSetup, item: HistoryRow) {
        (vs.imageView(R.id.iv_avatar) as MyImageView).setImageResource(R.drawable.bg_1, item.history.iconBackground)
        vs.imageView(R.id.iv_icon).setImageResource(item.history.iconImage)

        vs.textView(R.id.title).text = item.outlet.name
        vs.textView(R.id.tv_deal_state).text = item.history.stateName
        vs.textView(R.id.tv_deal_state).setBackgroundResource(item.history.currencyBackground)
        vs.textView(R.id.detail).text = DS.getString(R.string.deal_history_delivery_date, item.history.dealDeliveryDate)

        if (!TextUtils.isEmpty(item.amount)) {
            vs.id<View>(R.id.ll_deal_amount).visibility = View.VISIBLE
            vs.textView(R.id.tv_amount).text = item.amount
        } else {
            vs.id<View>(R.id.ll_deal_amount).visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.currency)) {
            vs.textView(R.id.tv_amount_currency).text = item.currency
        } else {
            vs.textView(R.id.tv_amount_currency).visibility = View.GONE
        }
    }
}
