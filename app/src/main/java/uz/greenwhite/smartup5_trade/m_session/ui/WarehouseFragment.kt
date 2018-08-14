package uz.greenwhite.smartup5_trade.m_session.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_session.SessionUtil
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession
import uz.greenwhite.smartup5_trade.m_session.row.WarehouseRow
import uz.greenwhite.smartup5_trade.m_warehouse.arg.ArgWarehouse
import uz.greenwhite.smartup5_trade.m_warehouse.ui.WarehouseIndexFragment

class WarehouseFragment : MoldContentRecyclerFragment<WarehouseRow>() {
    companion object {
        fun newInstance(arg: ArgSession): WarehouseFragment {
            return Mold.parcelableArgumentNewInstance(WarehouseFragment::class.java, arg, ArgSession.UZUM_ADAPTER)
        }
    }

    private val argSession: ArgSession by lazy { Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.setTitle(activity!!, R.string.warehouse_title)

    }

    override fun onStart() {
        super.onStart()

        ScopeUtil.execute(jobMate, argSession, object : OnScopeReadyCallback<MyArray<WarehouseRow>>() {
            override fun onScopeReady(scope: Scope): MyArray<WarehouseRow> {
                return SessionUtil.getWarehouseRows(scope)
            }

            override fun onDone(warehouseRows: MyArray<WarehouseRow>) {
                super.onDone(warehouseRows)
                listItems = warehouseRows.filterNotNull()
                makeFooter()
            }
        })
    }


    private fun makeFooter() {
        val vsFooter = ViewSetup(activity, R.layout.person_count_foter)
        vsFooter.textView(R.id.tv_persons).text = DS.getString(R.string.session_warehouse)
        vsFooter.textView(R.id.tv_person_count).text = "${adapter.filteredItems.size()}"
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            private fun populate() {
                vsFooter.textView(R.id.tv_person_count).text = "${adapter.filteredItems.size()}"
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                this.populate()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                this.populate()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                this.populate()
            }

            override fun onChanged() {
                super.onChanged()
                this.populate()
            }
        })
        val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    override fun onItemClick(holder: RecyclerAdapter.ViewHolder?, item: WarehouseRow) {
        WarehouseIndexFragment.open(activity, ArgWarehouse(argSession, item.warehouse.id))
    }

    override fun adapterGetLayoutResource(): Int = R.layout.session_warehouse

    override fun adapterPopulate(vsItem: ViewSetup, item: WarehouseRow) {
        vsItem.id<View>(R.id.v_bottom_padding).visibility = View.GONE
        if (!adapter.isEmpty && adapter.filteredItems.size() > 1) {
            val filteredItems = adapter.filteredItems
            val lastItem = filteredItems.get(filteredItems.size() - 1)
            if (item === lastItem) {
                vsItem.id<View>(R.id.v_bottom_padding).visibility = View.VISIBLE
            }
        }

        vsItem.textView(R.id.tv_title).text = item.title
        vsItem.textView(R.id.tv_detail).text = item.detail

        if (item.icon != null) {
            vsItem.viewGroup<ViewGroup>(R.id.lf_state).background = item.icon.second as Drawable
            vsItem.imageView(R.id.state).setImageDrawable(item.icon.first as Drawable)
        }

        vsItem.id<View>(R.id.lf_state).visibility = if (item.icon == null) View.GONE else View.VISIBLE
        vsItem.imageView(R.id.iv_icon).visibility = View.VISIBLE
        vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image)
    }
}
