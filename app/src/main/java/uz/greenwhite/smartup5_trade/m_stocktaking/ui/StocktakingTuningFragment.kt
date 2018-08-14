package uz.greenwhite.smartup5_trade.m_stocktaking.ui

import android.os.Bundle
import android.widget.LinearLayout
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.filter.FilterBoolean
import uz.greenwhite.lib.filter.FilterBooleanList
import uz.greenwhite.lib.filter.FilterUtil
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldTuningSectionFragment
import uz.greenwhite.lib.mold.MoldTuningSectionFragment.Section
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.m_stocktaking.filter.StocktakingFilter

class StocktakingTuningFragment : MoldTuningSectionFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lastPriceSection = Section { inflater, parent ->
            val v = inflater.inflate(R.layout.stocktaking_filter_last_price, parent, false)
            v.findViewById(R.id.btn_last_price).setOnClickListener { lastPrice() }
            return@Section v
        }

        setSections(MyArray.from(lastPriceSection, filterSection).filterNotNull())
    }

    private val filter: StocktakingFilter? by lazy { Mold.getData<StocktakingData>(activity).filter }

    private val filterSection: MoldTuningSectionFragment.Section?
        get() {
            val filter = filter ?: return null
            return object : MoldTuningSectionFragment.LinearLayoutSection() {
                override fun addViews(cnt: LinearLayout) {
                    cnt.removeAllViews()

                    val filters = MyArray.from(
                            filter.product.groupFilter,
                            filter.productCard,
                            FilterBooleanList(
                                    MyArray.from<FilterBoolean>(
                                            filter.product.hasBarcode,
                                            filter.hasValue
                                    ).filterNotNull())
                    )

                    val views = FilterUtil.addAll(cnt, filters.filterNotNull())
                    FilterUtil.addClearButton(activity, cnt, views)
                }
            }
        }

    private fun lastPrice() {
        val data = Mold.getData<StocktakingData>(activity) ?: return
        data.vStocktaking.vProducts.items.forEach { it.setLastPrice(data.vStocktaking.vHeader.vCurrency.value.code) }
        Mold.getContentFragment<StocktakingProductFragment>(activity).reloadContent()
        Mold.closeDrawers(activity)


    }

    override fun onDrawerClosed() {
        Mold.getContentFragment<StocktakingProductFragment>(activity).setListFilter()
    }
}
