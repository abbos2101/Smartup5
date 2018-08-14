package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgBarcodeProductDetail
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct

class BarcodeManufacturePriceFragment : MoldContentFragment() {
    companion object {
        fun newInstance(arg: ArgBarcodeProductDetail): BarcodeManufacturePriceFragment {
            val bundle = Mold.parcelableArgument<ArgBarcodeProductDetail>(arg, ArgBarcodeProductDetail.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(BarcodeManufacturePriceFragment::class.java, bundle)
        }
    }

    private val argBarcode: ArgBarcodeProductDetail
        get() =
            Mold.parcelableArgument<ArgBarcodeProductDetail>(this, ArgBarcodeProductDetail.UZUM_ADAPTER)

    private var vsRoot: ViewSetup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming_barcode_price)
        return vsRoot!!.view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = Mold.getData<IncomingData>(activity)
        val product = data.vIncoming.vProducts.items.find(argBarcode.productId, VIncomingProduct.KEY_ADAPTER)

        Mold.setTitle(activity, product.product.name)

//        vsRoot!!.bind(R.id.et_price, product.manufacturePrice)

        vsRoot!!.textView(R.id.tv_quant).text = DS.getString(R.string.incoming_manufacture_price)

        vsRoot!!.id<View>(R.id.btn_close).setOnClickListener { Mold.popContent(activity) }
        vsRoot!!.id<View>(R.id.btn_next).setOnClickListener {
            Mold.addContent(activity, BarcodeExpireDateFragment.newInstance(argBarcode))
        }

    }

}