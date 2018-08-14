package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.collection.MyRecyclerAdapter
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldDialogFragment
import uz.greenwhite.lib.mold.MoldSearchQuery
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.lib.widget.DividerItemDecoration
import uz.greenwhite.lib.widget.MySearchView
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncomingProduct
import uz.greenwhite.smartup5_trade.m_session.bean.Product

class IncomingFilialProductDialog : MoldDialogFragment() {

    companion object {
        fun show(activity: FragmentActivity) {
            IncomingFilialProductDialog().show(activity.supportFragmentManager, "incoming-filial-product")
        }
    }

    private val M_DONE = 1
    private val M_SEARCH = 2

    private val adapter: FilialProductAdapter by lazy { FilialProductAdapter(activity) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vsRoot = ViewSetup(activity, R.layout.incoming_filial_product)

        val data = Mold.getData<IncomingData>(activity)

        val toolbar = vsRoot.id<Toolbar>(R.id.tb_toolbar)
        toolbar.title = DS.getString(R.string.incoming_filial_product)

        toolbar.navigationIcon = DS.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { dismiss() }

        toolbar.setOnMenuItemClickListener {
            if (it.itemId == M_DONE) {
                prepareProduct()
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }

        val menu = toolbar.menu
        menu.clear()

        addSearchMenu(menu, MoldSearchListQuery(adapter), R.drawable.gwslib_search_white_24dp)

        val menuItem = menu.add(Menu.NONE, M_DONE, Menu.NONE, DS.getString(R.string.incoming_done))
        menuItem.icon = UI.changeDrawableColor(activity, R.drawable.ic_done_black_24dp, R.color.toolbar_icon_color_silver_dark)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        val recycler = vsRoot.id<RecyclerView>(R.id.rv_list)
        recycler.addItemDecoration(DividerItemDecoration(activity))
        recycler.layoutManager = GridLayoutManager(this.activity, 1)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (1 == newState) {
                    UI.hideKeyboardSoft(activity)
                }
            }
        })

        val productIds = data.vIncoming.vProducts.items.map(VIncomingProduct.KEY_ADAPTER)

        recycler.adapter = adapter
        adapter.items = MyArray.from(data.vIncoming.products.filter { !productIds.contains(it.id) })

        val d = BottomSheetDialog(activity, R.style.Theme_Design_BottomSheetDialog)
        d.setContentView(vsRoot.view)
        d.setOnShowListener {
            val frame = d.findViewById(android.support.design.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(frame!!).state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return d
    }

    private fun prepareProduct() {
        val data = Mold.getData<IncomingData>(activity)
        val products = data.vIncoming.products
        val productIds = data.vIncoming.vProducts.items.map(VIncomingProduct.KEY_ADAPTER)
        adapter.selected.forEach {
            if (!productIds.contains(it)) {
                data.vIncoming.addProduct(products.find(it, Product.KEY_ADAPTER))
            }
        }
        val fragment = Mold.getContentFragment<IncomingProductFragment>(activity)
        fragment.reloadContent()
        dismiss()
    }

    private fun addSearchMenu(menu: Menu, searchQuery: MoldSearchQuery, searchQueryIcon: Int) {
        val item = menu.add(Menu.NONE, M_SEARCH, Menu.NONE, uz.greenwhite.lib.R.string.search)
        item.icon = UI.changeDrawableColor(activity, searchQueryIcon, uz.greenwhite.lib.R.color.toolbar_icon_color_silver_dark)
        item.setShowAsAction(10)
        val searchView = MySearchView(activity)
        searchView.isIconified = false
        item.actionView = searchView
        searchView.setOnQueryTextListener(searchQuery)
        searchView.clearFocus()
    }
}

class MoldSearchListQuery(val adapter: FilialProductAdapter) : MoldSearchQuery() {

    override fun onQueryText(s: String) {
        adapter.predicateSearch = object : MyPredicate<Product>() {
            override fun apply(item: Product): Boolean = CharSequenceUtil.containsIgnoreCase(item.name, s)
        }
        adapter.searchText = s
        adapter.filter()
    }
}

class FilialProductAdapter(context: Context) : MyRecyclerAdapter<Product>(context) {

    val selected = HashSet<String>()

    override fun populate(vs: ViewSetup, item: Product) {
        vs.textView(R.id.tv_product_name).text = item.name

        vs.compoundButton<CompoundButton>(R.id.cb_select).setOnCheckedChangeListener(null)
        vs.compoundButton<CompoundButton>(R.id.cb_select).isChecked = selected.contains(item.id)
        vs.compoundButton<CompoundButton>(R.id.cb_select).setOnCheckedChangeListener({ _, _ ->
            itemClick(null, item)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val vs = ViewSetup(this.context, R.layout.z_list_checkpox)

        val attrs = intArrayOf(uz.greenwhite.lib.R.attr.selectableItemBackground)
        val typedArray = this.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        vs.view.setBackgroundResource(backgroundResource)
        typedArray.recycle()
        return ViewHolder(vs)
    }

    override fun itemClick(holder: ViewHolder?, item: Product) {
        if (selected.contains(item.id)) {
            selected.remove(item.id)
        } else {
            selected.add(item.id)
        }
        notifyDataSetChanged()
    }
}
