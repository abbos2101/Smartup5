package uz.greenwhite.smartup5_trade.m_warehouse.ui

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.mold.NavigationItem
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_incoming.IncomingApi
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncoming
import uz.greenwhite.smartup5_trade.m_incoming.ui.IncomingFragment
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleMenu
import uz.greenwhite.smartup5_trade.m_stocktaking.StocktakingApi
import uz.greenwhite.smartup5_trade.m_stocktaking.arg.ArgStocktaking
import uz.greenwhite.smartup5_trade.m_stocktaking.ui.StocktakingFragment
import uz.greenwhite.smartup5_trade.m_warehouse.arg.ArgWarehouse
import uz.greenwhite.smartup5_trade.m_warehouse.row.WarehouseRow

class WarehouseIndexFragment : MoldContentFragment() {
    companion object {
        fun open(activity: Activity, arg: ArgWarehouse) {
            val bundle = Mold.parcelableArgument(arg, ArgWarehouse.UZUM_ADAPTER)
            Mold.openContent(activity, WarehouseIndexFragment::class.java, bundle)
        }
    }

    private val INCOMING = 1
    private val STOCKTAKING = 2 // корректировки
    private val EXIT = 1000

    private val FORMS = MyArray.from(
            NavigationItem(INCOMING, DS.getString(R.string.warehouse_incoming), R.drawable.store_2),
            NavigationItem(STOCKTAKING, DS.getString(R.string.warehouse_stocktaking), R.drawable.store_2)
    )

    private val argWarehouse: ArgWarehouse get() = Mold.parcelableArgument(this, ArgWarehouse.UZUM_ADAPTER)

    private var vsRoot: ViewSetup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.vsRoot = ViewSetup(inflater!!, container, R.layout.warehouse_index)
        return this.vsRoot!!.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val warehouse = argWarehouse.warehouse
        Mold.setTitle(activity, warehouse.name)
        Mold.setSubtitle(activity, warehouse.responsible.name)

        val result = RoleMenu.sortForms(argWarehouse, RoleMenu.WAREHOUSE, FORMS, NavigationItem.KEY_ADAPTER)
                .append(NavigationItem(EXIT, DS.getString(R.string.exit_go_out), R.drawable.ic_exit_to_app_black_24dp))

        val vg = vsRoot!!.viewGroup<ViewGroup>(R.id.ll_warehouse_forms_row)
        vg.removeAllViews()

        result.forEach {
            val vs = ViewSetup(activity, R.layout.z_card_view_row)
            vs.imageView(R.id.iv_card_view_icon).setImageDrawable(UI.changeDrawableColor(activity, it.icon, R.color.colorAccent))
            vs.textView(R.id.tv_card_view_title).text = it.title
            vs.view.setOnClickListener { _ -> showForm(it) }
            vg.addView(vs.view)
        }
    }

    override fun onStart() {
        super.onStart()
        reloadContent()
    }

    override fun reloadContent() {
        ScopeUtil.execute(jobMate, argWarehouse, object : OnScopeReadyCallback<MyArray<WarehouseRow>>() {
            override fun onScopeReady(scope: Scope): MyArray<WarehouseRow> {
                val incomings = IncomingApi.loadWarehouseIncoming(scope, argWarehouse.warehouseId).map { WarehouseRow(it) }
                val stocktakings = StocktakingApi.loadWarehouseStocktaking(scope, argWarehouse.warehouseId).map { WarehouseRow(it) }
                return MyArray.from(incomings).append(MyArray.from(stocktakings)).sort(WarehouseRow.SORT)
            }

            override fun onDone(result: MyArray<WarehouseRow>) {
                showHeaderView(result)
            }

            override fun onFail(throwable: Throwable?) {
                super.onFail(throwable)
                UI.alertError(activity, throwable)
            }
        })
    }

    private fun showHeaderView(items: MyArray<WarehouseRow>) {
        val vgInfo = vsRoot!!.viewGroup<ViewGroup>(R.id.ll_header_info)
        vgInfo.removeAllViews()
        vgInfo.visibility = if (items.isEmpty) View.GONE else View.VISIBLE

        items.forEach {
            val vs = ViewSetup(activity, R.layout.outlet_deal_info_row)
            vgInfo.addView(vs.view)

            vs.textView(R.id.tv_title).text = it.title
            vs.textView(R.id.tv_detail).text = it.detail

            if (TextUtils.isEmpty(it.state.errorText)) {
                vs.id<View>(R.id.tv_error).visibility = View.GONE
            } else {
                vs.id<View>(R.id.tv_error).visibility = View.VISIBLE
                vs.textView(R.id.tv_error).text = it.state.errorText
            }

            if (it.stateIcon != null) {
                vs.viewGroup<ViewGroup>(R.id.lf_state).background = it.stateIcon!!.second as Drawable
                vs.imageView(R.id.state).setImageDrawable(it.stateIcon!!.first as Drawable)
                vs.id<View>(R.id.lf_state).visibility = View.VISIBLE
            } else {
                vs.id<View>(R.id.lf_state).visibility = View.GONE
            }


            vs.view.setOnClickListener { _ ->
                if (WarehouseRow.T_INCOMING == it.type) {
                    IncomingFragment.open(activity, ArgIncoming(argWarehouse, it.entryId))

                } else if (WarehouseRow.T_STOCKTAKING == it.type) {
                    StocktakingFragment.open(activity, ArgStocktaking(argWarehouse, it.entryId))
                }
            }

            val ivEdit = vs.imageView(R.id.iv_edit)
            if (it.state.isReady) {
                ivEdit.visibility = View.VISIBLE
                ivEdit.setImageDrawable(WarehouseRow.EDIT_DRAWABLE)

                ivEdit.setOnClickListener { _ ->
                    if (WarehouseRow.T_INCOMING == it.type) {
                        IncomingApi.dealMakeEdit(argWarehouse.scope, it.entryId)
                        IncomingFragment.open(activity, ArgIncoming(argWarehouse, it.entryId))

                    } else if (WarehouseRow.T_STOCKTAKING == it.type) {
                        StocktakingApi.dealMakeEdit(argWarehouse.scope, it.entryId)
                        StocktakingFragment.open(activity, ArgStocktaking(argWarehouse, it.entryId))
                    }
                }
            } else {
                ivEdit.visibility = View.GONE
            }


            vs.view.setOnLongClickListener { _ ->
                UI.popup()
                        .option(R.string.remove) {
                            if (WarehouseRow.T_INCOMING == it.type) {
                                IncomingApi.dealDelete(argWarehouse.scope, it.entryId)
                            } else if (WarehouseRow.T_STOCKTAKING == it.type) {
                                StocktakingApi.dealDelete(argWarehouse.scope, it.entryId)
                            }
                            reloadContent()
                        }.show(vs.id<View>(R.id.lf_state))
                true
            }
        }
    }

    private fun showForm(form: NavigationItem) {
        val f = make(form.id)
        if (f != null) {
            Mold.addContent(activity, f, form)
        }
    }

    fun make(id: Int): MoldContentFragment? {
        return when (id) {
            INCOMING -> {
                IncomingFragment.open(activity, ArgIncoming(argWarehouse, ""))
                null
            }
            STOCKTAKING -> {
                StocktakingFragment.open(activity, ArgStocktaking(argWarehouse, ""))
                null
            }
            EXIT -> {
                activity.finish()
                null
            }
            else -> null
        }
    }

    override fun onBackPressed(): Boolean {
        UI.confirm(activity,
                DS.getString(R.string.exit_go_out),
                DS.getString(R.string.exit_warehouse_index)) { activity.finish() }
        return true
    }
}
