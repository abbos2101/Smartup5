package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import uz.greenwhite.barcode.barcode.BarCodeScannerFragment
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgBarcodeProductDetail
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncoming
import uz.greenwhite.smartup5_trade.m_incoming.ui.adapter.IncomingSearchAdapter
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import java.io.IOException


class BarcodeProductFragment : BarCodeScannerFragment() {

//    companion object {
//        fun newInstance(arg: ArgIncoming): BarcodeProductFragment {
//            return Mold.parcelableArgumentNewInstance(BarcodeProductFragment::class.java,
//                    Mold.parcelableArgument<ArgIncoming>(arg, ArgIncoming.UZUM_ADAPTER))
//        }
//    }

    private var lastBarcode: String? = ""
    private val argIncoming: ArgIncoming get() = Mold.parcelableArgument<ArgIncoming>(this, ArgIncoming.UZUM_ADAPTER)
    private var vsRoot: ViewSetup? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming_barcode)
        activity.window.addFlags(128)
        return vsRoot?.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.getToolbar(activity)?.visibility = View.GONE

        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer()
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


        vsRoot?.id<View>(R.id.iv_back)?.setOnClickListener { Mold.replaceContent(activity, IncomingProductFragment.newInstance(argIncoming)) }

        setmCallBack {
            if (it != null) {
                val barcode = it.toString()
                if (TextUtils.isEmpty(barcode) || (!TextUtils.isEmpty(lastBarcode) && lastBarcode == barcode)) {
                    return@setmCallBack
                }
                findProduct(barcode)
            }
        }

        val data = Mold.getData<IncomingData>(activity)
        val viewSearch = vsRoot!!.id<AutoCompleteTextView>(R.id.actv_search)
        val adapter = IncomingSearchAdapter(activity)
        adapter.predicateSearch = object : MyPredicate<VIncomingProduct>() {
            override fun apply(p: VIncomingProduct): Boolean {
                try {
                    val text = viewSearch.text.toString()
                    return CharSequenceUtil.containsIgnoreCase(p.product.name, text) || CharSequenceUtil.containsIgnoreCase(p.product.code, text)
                } catch (e: Exception) {
                    return true
                }
            }

        }
        adapter.items = data.vIncoming.vProducts.items
        viewSearch.setAdapter(adapter)
        viewSearch.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val arg = ArgBarcodeProductDetail(argIncoming, adapter.getItem(position).product.id)
            Mold.addContent(activity, BarcodeProductDetailFragment.newInstance(arg))
        }
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            vsRoot?.id<View>(R.id.fl_rotate)?.visibility = View.VISIBLE
        } else {
            vsRoot?.id<View>(R.id.fl_rotate)?.visibility = View.GONE
        }
    }

    @Synchronized
    fun findProduct(barcode: String) {
        try {
            this.mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val vibrator = this.activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200L)

        val data = Mold.getData<IncomingData>(activity)
        val findProduct = data.vIncoming.vProducts.items.find { it.productBarcode != null && it.productBarcode.barcodes.contains(barcode) }
        if (findProduct == null) {
            lastBarcode = barcode
            Mold.makeSnackBar(activity, DS.getString(R.string.incoming_product_not_found)).show()
        } else {
            val arg = ArgBarcodeProductDetail(argIncoming, findProduct.product.id)
            Mold.addContent(activity, BarcodeProductDetailFragment.newInstance(arg))
        }
    }

    override fun onStop() {
        super.onStop()
        Mold.getToolbar(activity)?.visibility = View.VISIBLE
    }

}
