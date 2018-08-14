package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.barcode.barcode.BarCodeScannerFragment
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgBarcodeProductDetail
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import java.io.IOException

class BarcodeManufactureFragment : BarCodeScannerFragment() {

    companion object {
        fun newInstance(arg: ArgBarcodeProductDetail): BarcodeManufactureFragment {
            return Mold.parcelableArgumentNewInstance(BarcodeManufactureFragment::class.java,
                    Mold.parcelableArgument<ArgBarcodeProductDetail>(arg, ArgBarcodeProductDetail.UZUM_ADAPTER))
        }
    }

    private val argBarcode: ArgBarcodeProductDetail get() = Mold.parcelableArgument<ArgBarcodeProductDetail>(this, ArgBarcodeProductDetail.UZUM_ADAPTER)
    private var vsRoot: ViewSetup? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming_barcode_manufacture)
        activity.window.addFlags(128)
        return vsRoot?.view
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
        val data = Mold.getData<IncomingData>(activity)
        val product = data.vIncoming.vProducts.items.find(argBarcode.productId, VIncomingProduct.KEY_ADAPTER)

        vsRoot!!.id<View>(R.id.tv_skip).setOnClickListener { Mold.addContent(activity, BarcodeExpireDateFragment.newInstance(argBarcode)) }
        vsRoot!!.id<View>(R.id.fab_backward).setOnClickListener { Mold.popContent(activity) }
        vsRoot!!.id<View>(R.id.fab_next).setOnClickListener { Mold.addContent(activity, BarcodeManufacturePriceFragment.newInstance(argBarcode)) }

//        vsRoot!!.bind(R.id.et_manufacture_barcode, product.cardNumber)

//        vsRoot!!.model(R.id.et_manufacture_barcode).add({
//            vsRoot!!.id<View>(R.id.fab_next).visibility = if (product.cardNumber.isEmpty) View.GONE else View.VISIBLE
//        }).notifyListeners()

        setmCallBack { setBarcode(it.toString()) }
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
    private fun setBarcode(barcode: String) {
        try {
            this.mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        vsRoot!!.editText(R.id.et_manufacture_barcode).setText(barcode)
        vsRoot!!.id<View>(R.id.fab_next).performClick()
    }

    override fun onStop() {
        super.onStop()
        Mold.getToolbar(activity)?.visibility = View.VISIBLE
    }

}