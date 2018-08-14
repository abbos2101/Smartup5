package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.View
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.job.internal.Manager
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.util.Util
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.BarcodeUtil
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncomingProduct
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProductDetail


class IncomingProductDetailFragment : MoldContentRecyclerFragment<VIncomingProductDetail>() {

    companion object {
        fun newInstance(arg: ArgIncomingProduct): IncomingProductDetailFragment {
            val bundle = Mold.parcelableArgument(arg, ArgIncomingProduct.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(IncomingProductDetailFragment::class.java, bundle)
        }
    }

    private val argProduct: ArgIncomingProduct get() = Mold.parcelableArgument(this, ArgIncomingProduct.UZUM_ADAPTER)

    private var vIncomingProduct: VIncomingProduct? = null
    private var vIncomingProductDetail: VIncomingProductDetail? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val data = Mold.getData<IncomingData>(activity)

        this.vIncomingProduct = data.vIncoming.vProducts.items
                .find(argProduct.productId, VIncomingProduct.KEY_ADAPTER) ?: return

        setHasLongClick(true)
        Mold.setTitle(activity, vIncomingProduct!!.product.name)
        reloadContent()

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true// true if moved, false otherwise
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                if (adapter.filteredItems.size() <= 1) {
                    UI.dialog()
                            .title(R.string.warning)
                            .message(R.string.remove)
                            .positive({
                                Mold.getData<IncomingData>(activity).vIncoming.removeProduct(vIncomingProduct)
                                Mold.popContent(activity)
                            })
                            .negative(Util.NOOP)
                            .show(activity)
                } else {
                    vIncomingProduct!!.removeProductDetail(adapter.getItem(viewHolder.layoutPosition))
                    adapter.removeItem(viewHolder.layoutPosition)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(cRecycler)
    }

    override fun reloadContent() {
        var number = 1
        listItems = MyArray.from(vIncomingProduct!!.productDetails.items.sort({ l, r ->
            return@sort MyPredicate.compare(l.number, r.number)
        }).filter {
            it.number = number++
            return@filter true
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val barcode = BarcodeUtil.getBarcodeInActivityResult(activity, requestCode, resultCode, data)
        if (!TextUtils.isEmpty(barcode) && vIncomingProductDetail != null) {
            vIncomingProductDetail!!.cardNumber.value = barcode
            reloadContent()
        }
    }

    override fun onItemLongClick(holder: RecyclerAdapter.ViewHolder?, item: VIncomingProductDetail?) {
        val view = holder!!.vsItem.id<View>(R.id.ll_detail)
        view.visibility = if (view.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onBackPressed(): Boolean {
        UI.hideKeyboardSoft(activity)
        Manager.handler.postDelayed({
            Mold.popContent(activity)
        }, 100)
        return true
    }

    override fun adapterPopulate(vs: ViewSetup, item: VIncomingProductDetail) {
        vs.textView(R.id.tv_number).text = "${item.number})"

        vs.textView(R.id.tv_product_quantity).text = UI.html().v(DS.getString(R.string.incoming_quantity)).fRed().v(" *").fRed().html()
        vs.textView(R.id.tv_product_price).text = UI.html().v(DS.getString(R.string.incoming_price)).fRed().v(" *").fRed().html()

        vs.bind(R.id.et_quantity, item.quantity)
        vs.bind(R.id.et_price, item.price)
        vs.bind(R.id.et_card_number, item.cardNumber)
        vs.bind(R.id.et_manufacture_price, item.manufacturePrice)
        vs.bind(R.id.et_expire_date, item.expireDate)

        vs.makeDatePicker(R.id.et_expire_date, true)

        vs.id<View>(R.id.iv_barcode).setOnClickListener {
            vIncomingProductDetail = item
            BarcodeUtil.showBarcodeDialog(this)
        }

        vs.id<View>(R.id.iv_copy).setOnClickListener {
            vIncomingProduct?.copy(item)
            reloadContent()
        }

        val data = Mold.getData<IncomingData>(activity)
        val entryState = data.vIncoming.holder.state

        vs.id<View>(R.id.et_card_number).isEnabled = entryState.isSaved || entryState.isNotSaved
        vs.id<View>(R.id.et_manufacture_price).isEnabled = entryState.isSaved || entryState.isNotSaved
        vs.id<View>(R.id.et_quantity).isEnabled = entryState.isSaved || entryState.isNotSaved
        vs.id<View>(R.id.et_expire_date).isEnabled = entryState.isSaved || entryState.isNotSaved
        vs.id<View>(R.id.et_price).isEnabled = entryState.isSaved || entryState.isNotSaved
        vs.id<View>(R.id.iv_barcode).visibility = if (entryState.isSaved || entryState.isNotSaved) View.VISIBLE else View.GONE
    }

    override fun adapterGetLayoutResource(): Int = R.layout.incoming_product_detail

}