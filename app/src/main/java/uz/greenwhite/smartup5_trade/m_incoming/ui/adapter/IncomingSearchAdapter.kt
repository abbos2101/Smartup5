package uz.greenwhite.smartup5_trade.m_incoming.ui.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import uz.greenwhite.lib.collection.MyAdapter
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct

class IncomingSearchAdapter(context: Context) : MyAdapter<VIncomingProduct, IncomingSearchAdapter.ViewHolder>(context) {

    override fun getLayoutResource(): Int = android.R.layout.simple_list_item_2

    override fun makeHolder(view: View): ViewHolder {
        val holder = ViewHolder()
        holder.text1 = UI.id(view, android.R.id.text1)
        holder.text2 = UI.id(view, android.R.id.text2)
        return holder
    }

    override fun populate(holder: ViewHolder, item: VIncomingProduct) {
        holder.text1!!.text = item.product.name
        if (item.productBarcode != null) {
            holder.text2!!.text = DS.getString(R.string.incoming_product_barcode, item.productBarcode.barcodes.mkString(", "))
            holder.text2!!.visibility = View.VISIBLE
        } else {
            holder.text2!!.visibility = View.GONE
        }
    }

    class ViewHolder {
        var text1: TextView? = null
        var text2: TextView? = null
    }
}
