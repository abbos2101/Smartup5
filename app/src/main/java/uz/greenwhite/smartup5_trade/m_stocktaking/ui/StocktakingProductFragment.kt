package uz.greenwhite.smartup5_trade.m_stocktaking.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomSheetBehavior
import android.text.TextUtils
import android.view.View
import android.widget.Button
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.error.UserError
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.MoldTuningFragment
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.util.NumberUtil
import uz.greenwhite.lib.view_setup.ModelChange
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup.anor.ErrorUtil
import uz.greenwhite.smartup.anor.datasource.persist.EntryState
import uz.greenwhite.smartup.anor.m_admin.AdminApi
import uz.greenwhite.smartup5_trade.BarcodeUtil
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.UIUtils
import uz.greenwhite.smartup5_trade.common.CustomNumberKeyboard
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_stocktaking.StocktakingApi
import uz.greenwhite.smartup5_trade.m_stocktaking.arg.ArgStocktaking
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktakingProduct
import java.math.BigDecimal

class StocktakingProductFragment : MoldContentRecyclerFragment<VStocktakingProduct>() {

    companion object {
        fun newInstance(argStocktaking: ArgStocktaking): StocktakingProductFragment {
            return Mold.parcelableArgumentNewInstance(StocktakingProductFragment::class.java,
                    Mold.parcelableArgument<ArgStocktaking>(argStocktaking, ArgStocktaking.UZUM_ADAPTER))
        }
    }

    private val argStocktaking: ArgStocktaking by lazy { Mold.parcelableArgument<ArgStocktaking>(this, ArgStocktaking.UZUM_ADAPTER) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = Mold.getData<StocktakingData>(activity)

        Mold.setTitle(activity, "№${data.vStocktaking.vHeader.vNumber.value}")

        setSearchMenu(object : MoldSearchListQuery() {
            override fun filter(item: VStocktakingProduct, text: String): Boolean {
                return CharSequenceUtil.containsIgnoreCase(item.product.name, text) ||
                        CharSequenceUtil.containsIgnoreCase(item.product.code, text) ||
                        (item.barcodes != null && item.barcodes!!.barcodes.contains(text))
            }
        })

        addSubMenu(getString(R.string.filter)) { Mold.openTuningDrawer(activity) }

        addSubMenu(getString(R.string.barcode)) { BarcodeUtil.showBarcodeDialog(this) }

        setHeader(R.layout.stocktaking_header)

        reloadContent()

        makeFooter()
    }

    override fun reloadContent() {
        val data = Mold.getData<StocktakingData>(activity)
        var items = data.vStocktaking.vProducts.items
        if (!(data.vStocktaking.holder.state.isNotSaved || data.vStocktaking.holder.state.isSaved)) {
            items = items.filter(object : MyPredicate<VStocktakingProduct>() {
                override fun apply(item: VStocktakingProduct): Boolean = item.hasValue() || item.error.isError
            })
        }
        listItems = items.sort({ l, r ->
            if (l.hasValue() || l.error.isError) {
                return@sort -1
            } else if (r.hasValue()) {
                return@sort 1
            }
            return@sort 0
        })

        setListFilter()
    }

    fun setListFilter() {
        if (adapter != null) {
            val data = Mold.getData<StocktakingData>(activity) ?: return
            super.setListFilter(data.filter.predicate)
        }
    }


    private fun makeFooter() {
        val data = Mold.getData<StocktakingData>(activity) ?: return

        val vsFooter = ViewSetup(activity, R.layout.z_incoming_footer)
        val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        if (!AdminApi.isCalculatorKeyboard()) {
            CustomNumberKeyboard.init(activity, vsFooter)
            vsFooter.id<View>(R.id.ll_keyboard).visibility = View.VISIBLE
        } else {
            vsFooter.id<View>(R.id.ll_keyboard).visibility = View.GONE
        }
        val cSave = vsFooter.id<Button>(R.id.save)
        val cMakeEditable = vsFooter.id<Button>(R.id.make_editable)
        val cComplete = vsFooter.id<Button>(R.id.complete)

        cSave.setOnClickListener { saveStocktakingTry(false) }
        cMakeEditable.setOnClickListener { makeEditable() }
        cComplete.setOnClickListener { saveStocktakingReady() }


        when (data.vStocktaking.holder.state.state) {
            EntryState.NOT_SAVED, EntryState.SAVED -> {
                cSave.visibility = View.VISIBLE
                cComplete.visibility = View.VISIBLE
            }
            EntryState.READY -> cMakeEditable.visibility = View.VISIBLE
        }
    }

    override fun getTuningFragment(): MoldTuningFragment = StocktakingTuningFragment()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(), true)

        val barcode = BarcodeUtil.getBarcodeInActivityResult(activity, requestCode, resultCode, data)
        if (!TextUtils.isEmpty(barcode)) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ Mold.setSearchViewText(activity, barcode) }, 500)
        }
    }

    override fun adapterGetLayoutResource(): Int = R.layout.stocktaking_product_row

    override fun adapterPopulate(vs: ViewSetup, item: VStocktakingProduct) {
        vs.id<View>(R.id.v_bottom_padding).visibility = View.GONE

        if (!adapter.isEmpty && adapter.filteredItems.size() > 1 &&
                item === adapter.filteredItems.get(adapter.filteredItems.size() - 1)) {
            vs.id<View>(R.id.v_bottom_padding).visibility = View.VISIBLE
        }

        vs.textView(R.id.tv_product_name).text = item.product.name
        if (TextUtils.isEmpty(item.card.code)) {
            vs.id<View>(R.id.tv_card_code).visibility = View.GONE
        } else {
            vs.id<View>(R.id.tv_card_code).visibility = View.VISIBLE
            vs.textView(R.id.tv_card_code).text = item.card.code
        }

        if (TextUtils.isEmpty(item.expireDate)) {
            vs.id<View>(R.id.tv_expire_date).visibility = View.GONE
        } else {
            vs.id<View>(R.id.tv_expire_date).visibility = View.VISIBLE
            vs.textView(R.id.tv_expire_date).text = item.expireDate
        }

        vs.textView(R.id.tv_balance).text = NumberUtil.formatMoney(item.allBalance)
        vs.textView(R.id.tv_balance_with_booked).text = NumberUtil.formatMoney(item.availBalance)

        vs.bind(R.id.et_quantity, item.quantity)
        vs.bind(R.id.et_price, item.price)

        if (!AdminApi.isCalculatorKeyboard()) {
            CustomNumberKeyboard.prepare(vs.editText(R.id.et_quantity))
            CustomNumberKeyboard.prepare(vs.editText(R.id.et_price))
        }
        val onError = ModelChange {
            UIUtils.showErrorText(vs.textView(R.id.error), item.error)
        }

        onError.onChange()

        val priceChange = ModelChange {
            val stocktaking = item.quantity.quantity.subtract(item.allBalance)
            vs.textView(R.id.tv_total_price).text = if (item.quantity.nonZero() &&
                    item.price.nonEmpty() && stocktaking > BigDecimal.ZERO)
                DS.getString(R.string.stocktaking_total_price, NumberUtil.formatMoney(stocktaking.multiply(item.price.quantity))) else ""
        }

        priceChange.onChange()

        vs.model(R.id.et_quantity).add({
            val stocktaking = item.quantity.quantity.subtract(item.allBalance)
            vs.textView(R.id.et_stocktaking).text = if (item.quantity.nonZero()) NumberUtil.formatMoney(stocktaking) else ""

            val etPrice = vs.textView(R.id.et_price)
            etPrice.isEnabled = stocktaking > BigDecimal.ZERO
            etPrice.text = if (etPrice.isEnabled) etPrice.text else ""

        }).notifyListeners().add(priceChange).add(onError)

        vs.model(R.id.et_price).add(priceChange).add(onError)
    }

    //--------------------------------------------------------------------------------------------------

    private fun makeEditable() {
        val data = Mold.getData<StocktakingData>(activity)
        StocktakingApi.dealMakeEdit(argStocktaking.scope, data.vStocktaking.holder.stocktaking)
        StocktakingFragment.open(activity, argStocktaking)
        activity.finish()
    }

    private fun saveStocktakingReady() {
        try {
            val data = Mold.getData<StocktakingData>(activity)
            val error = data.vStocktaking.error
            if (error.isError) {
                throw UserError(error.errorMessage)
            }
            UI.confirm(activity, getString(R.string.save), getString(R.string.deal_prepare_visit)) { saveStocktakingTry(true) }
        } catch (ex: Exception) {
            ErrorUtil.saveThrowable(ex)
            UI.alertError(activity, ErrorUtil.getErrorMessage(ex).message as String)
        }
    }

    private fun saveStocktakingTry(ready: Boolean) {
        try {
            saveDeal(ready)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ErrorUtil.saveThrowable(ex)
            UI.alertError(activity, ErrorUtil.getErrorMessage(ex).message as String)
        }
    }

    private fun saveDeal(ready: Boolean) {
        val data = Mold.getData<StocktakingData>(activity)
        StocktakingApi.saveDeal(argStocktaking.scope, data.vStocktaking.convertToValue(), ready)
        activity.finish()
    }
}

