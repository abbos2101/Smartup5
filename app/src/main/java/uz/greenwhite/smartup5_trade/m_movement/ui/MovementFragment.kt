package uz.greenwhite.smartup5_trade.m_movement.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.text.TextUtils
import android.view.View
import android.widget.Button
import uz.greenwhite.lib.error.UserError
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.util.NumberUtil
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup.anor.ErrorUtil
import uz.greenwhite.smartup.anor.datasource.persist.EntryState
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_movement.MovementApi
import uz.greenwhite.smartup5_trade.m_movement.arg.ArgMovement
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementProduct
import uz.greenwhite.smartup5_trade.m_movement.variable.VMovementIncoming

class MovementFragment : MoldContentRecyclerFragment<MovementProduct>() {

    companion object {
        fun open(activity: Activity, arg: ArgMovement) {
            Mold.openContent(activity, MovementFragment::class.java,
                    Mold.parcelableArgument<ArgMovement>(arg, ArgMovement.UZUM_ADAPTER))
        }
    }

    private val argMovement: ArgMovement by lazy { Mold.parcelableArgument<ArgMovement>(this, ArgMovement.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Mold.setTitle(activity, R.string.warehouse_movement_incoming)

        setSearchMenu(object : MoldSearchListQuery() {
            override fun filter(item: MovementProduct, text: String): Boolean {
                return CharSequenceUtil.containsIgnoreCase(item.productName, text) ||
                        CharSequenceUtil.containsIgnoreCase(item.productCode, text)
            }
        })

        var data = Mold.getData<MovementData>(activity)
        if (data == null) {
            data = MovementData(argMovement.scope, argMovement.holder)
            Mold.setData(activity, data)
        }

        listItems = data.vMovementIncoming.incoming.products

        val header = setHeader(R.layout.movement_incoming_header)
        header.textView(R.id.tv_from_filial).text = DS.getString(R.string.movement_from_filial, data.vMovementIncoming.incoming.fromFilial)
        header.textView(R.id.tv_from_warehouse).text = DS.getString(R.string.movement_from_warehouse, data.vMovementIncoming.incoming.fromWarehouse)
        header.bind(R.id.et_date, data.vMovementIncoming.vDate)
        UI.bind(header.spinner(R.id.sp_warehouse), data.vMovementIncoming.vWarehouser, true)

        if (!TextUtils.isEmpty(data.vMovementIncoming.holder.state.errorText)) {
            header.textView(R.id.tv_error).visibility = View.VISIBLE
            header.textView(R.id.tv_error).text = data.vMovementIncoming.holder.state.errorText
        }

        if (argMovement.openSate == ArgMovement.K_ACCEPT) {

            UI.makeDatePicker(header.editText(R.id.et_date))

            val vsFooter = ViewSetup(activity, R.layout.z_movement_footer)
            val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

            val cSave = vsFooter.id<Button>(R.id.save)
            val cMakeEditable = vsFooter.id<Button>(R.id.make_editable)
            val cComplete = vsFooter.id<Button>(R.id.complete)

            cSave.setOnClickListener { saveIncomingTry(false) }
            cMakeEditable.setOnClickListener { makeEditable() }
            cComplete.setOnClickListener { saveIncomingReady() }

            when (data.vMovementIncoming.holder.state.state) {
                EntryState.NOT_SAVED, EntryState.SAVED -> {
                    cSave.visibility = View.VISIBLE
                    cComplete.visibility = View.VISIBLE
                }
                EntryState.READY -> cMakeEditable.visibility = View.VISIBLE
            }
        } else {
            header.id<View>(R.id.et_date).isEnabled = false
            header.id<View>(R.id.sp_warehouse).isEnabled = false
        }
    }

    override fun adapterGetLayoutResource(): Int = R.layout.movement_product

    @SuppressLint("SetTextI18n")
    override fun adapterPopulate(vs: ViewSetup, item: MovementProduct) {
        vs.id<View>(R.id.v_bottom_padding).visibility = View.GONE

        if (!adapter.isEmpty && adapter.filteredItems.size() > 1 &&
                item === adapter.filteredItems.get(adapter.filteredItems.size() - 1)) {
            vs.id<View>(R.id.v_bottom_padding).visibility = View.VISIBLE
        }

        vs.textView(R.id.tv_product_name).text = item.productName
        vs.textView(R.id.tv_card_code).text = item.cardName
        vs.textView(R.id.tv_expire_date).text = item.expireDate
        vs.textView(R.id.tv_price).text = NumberUtil.formatMoney(item.price)
        vs.textView(R.id.tv_quantity).text = NumberUtil.formatMoney(item.quantity)
        vs.textView(R.id.tv_total_sum).text = "${DS.getString(R.string.total_sum)} ${NumberUtil.formatMoney(item.quantity * item.price)}"
    }

    //--------------------------------------------------------------------------------------------------

    private fun makeEditable() {
        val data = Mold.getData<MovementData>(activity)
        MovementApi.dealMakeEdit(argMovement.scope, data.vMovementIncoming.holder.incoming)
        MovementFragment.open(activity, argMovement)
        activity.finish()
    }

    private fun saveIncomingReady() {
        try {
            val data = Mold.getData<MovementData>(activity)
            val error = data.vMovementIncoming.error
            if (error.isError) {
                throw UserError(error.errorMessage)
            }
            UI.confirm(activity, getString(R.string.save), getString(R.string.deal_prepare_visit)) { saveIncomingTry(true) }
        } catch (ex: Exception) {
            ErrorUtil.saveThrowable(ex)
            UI.alertError(activity, ErrorUtil.getErrorMessage(ex).message as String)
        }
    }

    private fun saveIncomingTry(ready: Boolean) {
        try {
            saveDeal(ready)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ErrorUtil.saveThrowable(ex)
            UI.alertError(activity, ErrorUtil.getErrorMessage(ex).message as String)
        }
    }

    private fun saveDeal(ready: Boolean) {
        val data = Mold.getData<MovementData>(activity)
        val holder = VMovementIncoming.toValue(data.vMovementIncoming)
        MovementApi.saveDeal(argMovement.scope, holder.incoming, ready)
        activity.finish()
    }

}
