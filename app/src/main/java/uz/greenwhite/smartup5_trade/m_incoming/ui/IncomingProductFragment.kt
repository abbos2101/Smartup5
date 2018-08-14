package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.view.Gravity
import android.view.View
import android.widget.Button
import uz.greenwhite.lib.error.UserError
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.MoldTuningFragment
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.util.NumberUtil
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup.anor.ErrorUtil
import uz.greenwhite.smartup.anor.datasource.persist.EntryState
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingApi
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncoming
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncomingProduct
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct

class IncomingProductFragment : MoldContentRecyclerFragment<VIncomingProduct>() {

    companion object {
        fun newInstance(arg: ArgIncoming): IncomingProductFragment {
            val bundle = Mold.parcelableArgument(arg, ArgIncoming.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(IncomingProductFragment::class.java, bundle)
        }
    }

    private val argIncoming: ArgIncoming get() = Mold.parcelableArgument(this, ArgIncoming.UZUM_ADAPTER)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val data = Mold.getData<IncomingData>(activity)

        setHasLongClick(true)

        Mold.setTitle(activity, "№${data.vIncoming.vHeader.incomingNumber.value}")

        setEmptyText(R.string.list_is_empty)

        setSearchMenu(object : MoldSearchListQuery() {
            override fun filter(item: VIncomingProduct, text: String): Boolean {
                return CharSequenceUtil.containsIgnoreCase(item.product.name, text) ||
                        return CharSequenceUtil.containsIgnoreCase(item.product.code, text)
            }
        })

        addSubMenu(DS.getString(R.string.filter), {
            Mold.openTuningDrawer(activity)
        })

        val makeFloatAction = Mold.makeFloatAction(activity, R.drawable.ic_add_black_24dp)
        val lp = CoordinatorLayout.LayoutParams(makeFloatAction.layoutParams)
        val resources = DS.getResources()
        val padding16 = resources.getDimension(R.dimen.padding_16dp).toInt()
        val padding80 = resources.getDimension(R.dimen.padding_80dp).toInt()

        lp.setMargins(padding16, padding16, padding16, padding80)
        lp.gravity = Gravity.BOTTOM or Gravity.RIGHT
        makeFloatAction.layoutParams = lp
        makeFloatAction.setOnClickListener {
            IncomingFilialProductDialog.show(activity)
        }

        reloadContent()

        makeFooter()
    }

    override fun onStart() {
        super.onStart()
        reloadContent()
    }

    override fun reloadContent() {
        listItems = Mold.getData<IncomingData>(activity).vIncoming.vProducts.items
        setListFilter()
    }

    private fun makeFooter() {
        val data = Mold.getData<IncomingData>(activity) ?: return

        val vsFooter = ViewSetup(activity, R.layout.z_incoming_footer)
        val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        val cSave = vsFooter.id<Button>(R.id.save)
        val cMakeEditable = vsFooter.id<Button>(R.id.make_editable)
        val cComplete = vsFooter.id<Button>(R.id.complete)

        cSave.setOnClickListener { saveIncomingTry(false) }
        cMakeEditable.setOnClickListener { makeEditable() }
        cComplete.setOnClickListener { saveIncomingReady() }


        when (data.vIncoming.holder.state.state) {
            EntryState.NOT_SAVED, EntryState.SAVED -> {
                cSave.visibility = View.VISIBLE
                cComplete.visibility = View.VISIBLE
            }
            EntryState.READY -> cMakeEditable.visibility = View.VISIBLE
        }
    }

    fun setListFilter() {
        if (adapter != null) {
            val data = Mold.getData<IncomingData>(activity) ?: return
            setListFilter(data.filter.predicate)
        }
    }


    override fun onItemClick(holder: RecyclerAdapter.ViewHolder, item: VIncomingProduct) {
        val arg = ArgIncomingProduct(argIncoming, item.product.id)
        Mold.addContent(activity, IncomingProductDetailFragment.newInstance(arg))
    }

    override fun onItemLongClick(holder: RecyclerAdapter.ViewHolder?, item: VIncomingProduct?) {
        UI.popup()
                .option(R.string.remove, {
                    Mold.getData<IncomingData>(activity).vIncoming.removeProduct(item)
                    reloadContent()
                })
                .show(holder!!.vsItem.view)
    }

    override fun onBackPressed(): Boolean {
        Mold.replaceContent(activity, IncomingFragment.newInstance(argIncoming))
        return true
    }

    override fun getTuningFragment(): MoldTuningFragment {
        return IncomingTuningFragment()
    }

    override fun adapterGetLayoutResource(): Int = R.layout.incoming_product_row

    override fun adapterPopulate(vsItem: ViewSetup, item: VIncomingProduct) {
        vsItem.id<View>(R.id.v_bottom_padding).visibility = View.GONE

        if (!adapter.isEmpty && adapter.filteredItems.size() > 1 &&
                item === adapter.filteredItems.get(adapter.filteredItems.size() - 1)) {
            vsItem.id<View>(R.id.v_bottom_padding).visibility = View.VISIBLE
        }

        val data = Mold.getData<IncomingData>(activity)

        val state = data.vIncoming.holder.state
        vsItem.id<View>(R.id.et_price).isEnabled = state.isSaved || state.isNotSaved
        vsItem.id<View>(R.id.et_quantity).isEnabled = state.isSaved || state.isNotSaved

        vsItem.textView(R.id.tv_product_name).text = item.product.name
        val items = item.productDetails.items

        if (items.size() == 1) {
            val detail = items.get(0)
            vsItem.bind(R.id.et_price, detail.price)
            vsItem.bind(R.id.et_quantity, detail.quantity)

            vsItem.id<View>(R.id.et_price).visibility = View.VISIBLE

        } else if (items.size() > 1) {

            vsItem.id<View>(R.id.et_price).visibility = View.GONE
            vsItem.editText(R.id.et_quantity).setText(NumberUtil.formatMoney(item.allQuantity))
            vsItem.editText(R.id.et_quantity).isEnabled = false
        }
    }

    //--------------------------------------------------------------------------------------------------

    private fun makeEditable() {
        val data = Mold.getData<IncomingData>(activity)
        IncomingApi.dealMakeEdit(argIncoming.scope, data.vIncoming.holder.incoming)
        IncomingFragment.open(activity, argIncoming)
        activity.finish()
    }

    private fun saveIncomingReady() {
        try {
            val data = Mold.getData<IncomingData>(activity)
            val error = data.vIncoming.error
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
        val data = Mold.getData<IncomingData>(activity)
        val incoming = data.vIncoming.convertToIncoming()
        IncomingApi.saveDeal(argIncoming.scope, incoming, ready)
        activity.finish()
    }
}