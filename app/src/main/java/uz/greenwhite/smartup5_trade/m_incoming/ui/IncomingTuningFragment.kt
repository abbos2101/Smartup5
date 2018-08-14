package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.os.Bundle
import android.widget.LinearLayout
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.filter.FilterUtil
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldTuningSectionFragment
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.filter.IncomingFilter

class IncomingTuningFragment : MoldTuningSectionFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSections(MyArray.from(filterSection).filterNotNull())
    }

    private val filter: IncomingFilter? by lazy { Mold.getData<IncomingData>(activity).filter }

    private val filterSection: Section?
        get() {
            val filter = filter ?: return null
            return object : LinearLayoutSection() {
                override fun addViews(cnt: LinearLayout) {
                    cnt.removeAllViews()

                    val filters = MyArray.from(
                            filter.product.groupFilter
                    )

                    val views = FilterUtil.addAll(cnt, filters.filterNotNull().toSuper())
                    FilterUtil.addClearButton(activity, cnt, views)
                }
            }
        }


    override fun onDrawerClosed() {
        Mold.getContentFragment<IncomingProductFragment>(activity).setListFilter()
    }
}