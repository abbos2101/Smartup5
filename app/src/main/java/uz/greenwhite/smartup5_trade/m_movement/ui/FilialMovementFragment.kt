package uz.greenwhite.smartup5_trade.m_movement.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.ErrorUtil
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_movement.arg.ArgMovement
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming
import uz.greenwhite.smartup5_trade.m_movement.row.MovementRow
import uz.greenwhite.smartup5_trade.m_session.SessionUtil
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession

class FilialMovementFragment : MoldContentRecyclerFragment<MovementRow>() {

    companion object {
        fun newInstance(arg: ArgSession): FilialMovementFragment {
            return Mold.parcelableArgumentNewInstance(FilialMovementFragment::class.java,
                    Mold.parcelableArgument<ArgSession>(arg, ArgSession.UZUM_ADAPTER))
        }
    }

    private val argSession: ArgSession by lazy { Mold.parcelableArgument<ArgSession>(this, ArgSession.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSearchMenu(object : MoldSearchListQuery() {
            override fun filter(item: MovementRow, text: String): Boolean {
                return CharSequenceUtil.containsIgnoreCase(item.incoming.fromFilial, text) ||
                        return CharSequenceUtil.containsIgnoreCase(item.incoming.fromWarehouse, text)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        reloadContent()
    }

    override fun reloadContent() {
        ScopeUtil.execute(jobMate, argSession, object : OnScopeReadyCallback<MyArray<MovementRow>>() {
            override fun onScopeReady(scope: Scope?): MyArray<MovementRow> {
                return SessionUtil.getMovementRows(scope!!)
            }

            override fun onDone(items: MyArray<MovementRow>?) {
                listItems = items
            }

            override fun onFail(throwable: Throwable?) {
                super.onFail(throwable)
                UI.alertError(activity!!, ErrorUtil.getErrorMessage(throwable).message as String)
            }
        })
    }

    override fun onItemClick(holder: RecyclerAdapter.ViewHolder, item: MovementRow) {
        if (MovementIncoming.K_READY == item.incoming.state) {
            if (item.entryStates.isNotSaved) {
                UI.bottomSheet().title(R.string.select)
                        .option(R.string.warehouse_accept, {
                            MovementFragment.open(activity, ArgMovement(argSession, ArgMovement.K_ACCEPT, item.incoming.movementId))
                        })
                        .option(R.string.warehouse_open, {
                            MovementFragment.open(activity, ArgMovement(argSession, ArgMovement.K_OPEN, item.incoming.movementId))
                        })
                        .show(activity)
            } else {
                MovementFragment.open(activity, ArgMovement(argSession, ArgMovement.K_ACCEPT, item.incoming.movementId))
            }
        } else {
            MovementFragment.open(activity, ArgMovement(argSession, ArgMovement.K_OPEN, item.incoming.movementId))
        }
    }


    override fun adapterGetLayoutResource(): Int = R.layout.movement_incoming

    override fun adapterPopulate(vs: ViewSetup, item: MovementRow) {
        vs.id<View>(R.id.v_bottom_padding).visibility = View.GONE
        if (!adapter.isEmpty && adapter.filteredItems.size() > 1) {
            val filteredItems = adapter.filteredItems
            val lastItem = filteredItems.get(filteredItems.size() - 1)
            if (item === lastItem) {
                vs.id<View>(R.id.v_bottom_padding).visibility = View.VISIBLE
            }
        }

        vs.textView(R.id.tv_title).text = item.title
        vs.textView(R.id.tv_detail).text = item.detail

        if (item.icon != null) {
            vs.viewGroup<ViewGroup>(R.id.lf_state).background = item.icon.second as Drawable
            vs.imageView(R.id.state).setImageDrawable(item.icon.first as Drawable)
        }

        vs.id<View>(R.id.lf_state).visibility = if (item.icon == null) View.GONE else View.VISIBLE
        vs.imageView(R.id.iv_icon).visibility = View.VISIBLE
        vs.imageView(R.id.iv_avatar).setBackgroundResource(item.image)

    }

}
