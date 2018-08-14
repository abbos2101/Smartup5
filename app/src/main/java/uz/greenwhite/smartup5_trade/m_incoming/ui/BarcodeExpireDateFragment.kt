package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.util.DateUtil
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgBarcodeProductDetail
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import java.util.*

class BarcodeExpireDateFragment : MoldContentFragment() {
    companion object {
        fun newInstance(arg: ArgBarcodeProductDetail): BarcodeExpireDateFragment {
            val bundle = Mold.parcelableArgument<ArgBarcodeProductDetail>(arg, ArgBarcodeProductDetail.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(BarcodeExpireDateFragment::class.java, bundle)
        }
    }

    private val argBarcode: ArgBarcodeProductDetail
        get() = Mold.parcelableArgument<ArgBarcodeProductDetail>(this, ArgBarcodeProductDetail.UZUM_ADAPTER)

    private var vsRoot: ViewSetup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming_expire_date)
        return vsRoot!!.view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = Mold.getData<IncomingData>(activity)
        val product = data.vIncoming.vProducts.items.find(argBarcode.productId, VIncomingProduct.KEY_ADAPTER)

        Mold.setTitle(activity, product.product.name)

        vsRoot!!.id<View>(R.id.tv_skip).setOnClickListener { Mold.replaceContent(activity, IncomingProductFragment.newInstance(argBarcode)) }
        vsRoot!!.id<View>(R.id.tv_ok).setOnClickListener { Mold.replaceContent(activity, IncomingProductFragment.newInstance(argBarcode)) }

        val datePicker = vsRoot!!.id<DatePicker>(R.id.dp_expire_date)

//        val c: Calendar = parse(product.expireDate.value)
//        datePicker.minDate = System.currentTimeMillis()
//        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)) { _, year, monthOfYear, dayOfMonth ->
//            c.set(year, monthOfYear, dayOfMonth, 0, 0)
//            product.expireDate.value = DateUtil.format(c.time, DateUtil.FORMAT_AS_DATE)
//        }
    }

    private fun parse(s: String): Calendar {
        val date = DateUtil.parse(s) ?: Date()
        val c = Calendar.getInstance()
        c.time = date
        return c
    }

}