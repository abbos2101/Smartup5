package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import uz.greenwhite.barcode.barcode.BarCodeScannerFragment
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.view_setup.ModelChange
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgBarcodeProductDetail
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import java.io.IOException
import java.math.BigDecimal

class BarcodeProductDetailFragment : BarCodeScannerFragment() {

    companion object {
        fun newInstance(arg: ArgBarcodeProductDetail): BarcodeProductDetailFragment {
            return Mold.parcelableArgumentNewInstance(BarcodeProductDetailFragment::class.java,
                    Mold.parcelableArgument<ArgBarcodeProductDetail>(arg, ArgBarcodeProductDetail.UZUM_ADAPTER))
        }
    }

    private val argBarcode: ArgBarcodeProductDetail
        get() =
            Mold.parcelableArgument<ArgBarcodeProductDetail>(this, ArgBarcodeProductDetail.UZUM_ADAPTER)

    private var vsRoot: ViewSetup? = null
    private var lastFoundTime: Long = 0
    private var isBox: Boolean = false
    private var onChange: ModelChange? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming_barcode_product)
        return vsRoot!!.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.getToolbar(activity)?.visibility = View.GONE

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            try {
                val file = activity.resources.openRawResourceFd(com.google.zxing.client.android.R.raw.zxing_beep)

                try {
                    mediaPlayer!!.setDataSource(file.fileDescriptor, file.startOffset, file.length)
                } finally {
                    file.close()
                }

                mediaPlayer!!.setVolume(0.1f, 0.1f)
                mediaPlayer!!.prepare()
            } catch (var8: IOException) {
                mediaPlayer!!.release()
            }

        }
        vsRoot!!.id<View>(R.id.iv_back).setOnClickListener { Mold.popContent(activity) }

        vsRoot!!.id<View>(R.id.fab_next).setOnClickListener {
            Mold.addContent(activity, BarcodePriceFragment.newInstance(argBarcode))
        }

        val data = Mold.getData<IncomingData>(activity)
        val findProduct = data.vIncoming.vProducts.items.find(argBarcode.productId, VIncomingProduct.KEY_ADAPTER)

        onChange = ModelChange {
//            vsRoot!!.id<View>(R.id.fab_next).visibility = if (findProduct.quantity.isZero) View.GONE else View.VISIBLE
        }

        onChange!!.onChange()

//        vsRoot!!.bind(R.id.et_quantity, findProduct.quantity)
//        vsRoot!!.model(R.id.et_quantity).add(onChange)
//
//        setmCallBack { findProduct(it.toString()) }
//
//        val product = findProduct.product
//        val hasBox = product.isInputBox && product.boxQuant.compareTo(BigDecimal.ZERO) != 0
//        val hasQuant: Boolean
//
//        if (hasBox) {
//            val quantPart = product.getQuantPart(findProduct.quantity.value)
//            hasQuant = product.isInputQuant || quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0
//        } else {
//            hasQuant = true
//        }
//
//        val flQuant = vsRoot!!.id<ViewGroup>(R.id.fl_quant)
//        val flBox = vsRoot!!.id<ViewGroup>(R.id.fl_box)
//
//        flQuant.visibility = if (hasQuant) View.VISIBLE else View.GONE
//        flBox.visibility = if (hasBox) View.VISIBLE else View.GONE
//
//        flQuant.setOnClickListener { isBox = false; reloadTab(isBox) }
//        flBox.setOnClickListener { isBox = true; reloadTab(isBox) }
//
//        if (hasQuant) vsRoot!!.id<TextView>(R.id.tv_quant).text = findProduct.product.measureName
//        if (hasBox) vsRoot!!.id<TextView>(R.id.tv_box).text = findProduct.product.boxName
//
//        isBox = !hasQuant
//        reloadTab(isBox)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            vsRoot?.id<View>(R.id.fl_rotate)?.visibility = View.VISIBLE
        } else {
            vsRoot?.id<View>(R.id.fl_rotate)?.visibility = View.GONE
        }
    }


    private fun reloadTab(box: Boolean) {
        vsRoot!!.id<ViewGroup>(R.id.fl_quant).setBackgroundResource(if (box) R.color.white else R.color.colorAccent)
        vsRoot!!.id<TextView>(R.id.tv_quant).setTextColor(DS.getColor(if (box) R.color.text_color else R.color.white))

        vsRoot!!.id<ViewGroup>(R.id.fl_box).setBackgroundResource(if (box) R.color.colorAccent else R.color.white)
        vsRoot!!.id<TextView>(R.id.tv_box).setTextColor(DS.getColor(if (box) R.color.white else R.color.text_color))
    }

    @Synchronized
    fun findProduct(barcode: String) {
        try {
            this.mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (lastFoundTime == 0L || ((System.currentTimeMillis() - lastFoundTime) / 1000) > 1) {
            lastFoundTime = System.currentTimeMillis()

//            val data = Mold.getData<IncomingData>(activity)
//            val findProduct = data.vIncoming.vProducts.items.find(argBarcode.productId, VIncomingProduct.KEY_ADAPTER)
//            if (findProduct?.productBarcode?.barcodes?.contains(barcode) == true) {
//                findProduct.quantity.value = if (isBox) findProduct.quantity.quantity.add(findProduct.product.boxQuant) else findProduct.quantity.quantity.add(BigDecimal.ONE)
//                vsRoot!!.bind(R.id.et_quantity, findProduct.quantity)
//                Mold.makeSnackBar(activity, DS.getString(R.string.incoming_add_quant_via_barcode)).show()
//                onChange?.onChange()
//                if (onChange != null) {
//                    vsRoot!!.model(R.id.et_quantity).add(onChange)
//                }
//            }
        }
    }

    override fun onStop() {
        super.onStop()
        Mold.getToolbar(activity)?.visibility = View.VISIBLE
    }
}