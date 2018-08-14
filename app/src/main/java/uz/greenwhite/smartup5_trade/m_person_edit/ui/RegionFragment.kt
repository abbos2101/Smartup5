package uz.greenwhite.smartup5_trade.m_person_edit.ui

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
import android.view.ViewGroup
import android.widget.FrameLayout
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.collection.MyRecyclerAdapter
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldDialogFragment
import uz.greenwhite.lib.mold.MoldSearchQuery
import uz.greenwhite.lib.util.CharSequenceUtil
import uz.greenwhite.lib.variable.SpinnerOption
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.lib.widget.MySearchView
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgRegion

class RegionFragment : MoldDialogFragment() {

    companion object {
        fun open(activity: FragmentActivity, arg: ArgRegion) {
            val dialog = RegionFragment()
            dialog.arguments = Mold.parcelableArgument<ArgRegion>(arg, ArgRegion.UZUM_ADAPTER)
            dialog.show(activity.supportFragmentManager, "region-dialog")
        }
    }

    private val adapter: RegionAdapter by lazy { RegionAdapter(activity) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vsRoot = ViewSetup(activity, R.layout.person_region_dialog)

        val data = Mold.getData<PersonData>(activity)
        val value = data.info.person.region

        val toolbar = vsRoot.id<Toolbar>(R.id.tb_toolbar)

        toolbar.navigationIcon = DS.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { dismiss() }

        val menu = toolbar.menu
        menu.clear()

        val recyclerAdapter = adapter
        addSearchMenu(menu, MoldSearchListQuery(recyclerAdapter), R.drawable.gwslib_search_white_24dp)

        val recycler = vsRoot.id<RecyclerView>(R.id.rv_list)
        recycler.layoutManager = GridLayoutManager(this.activity, 1)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (1 == newState) {
                    UI.hideKeyboardSoft(activity)
                }

            }
        })
        recycler.adapter = recyclerAdapter
        recyclerAdapter.items = value.options
        recyclerAdapter.setItemClick {
            value.value = it
            val fragment = Mold.getContentFragment<PersonCreateFragment>(activity)
            fragment.reloadPageContent()
            dismiss()
        }

        val d = BottomSheetDialog(activity, R.style.Theme_Design_BottomSheetDialog)
        d.setContentView(vsRoot.view)
        d.setOnShowListener {
            val frame = d.findViewById(android.support.design.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(frame!!).state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return d
    }

    private fun addSearchMenu(menu: Menu, searchQuery: MoldSearchQuery, searchQueryIcon: Int) {
        val item = menu.add(0, 1, 0, uz.greenwhite.lib.R.string.search)
        item.icon = UI.changeDrawableColor(activity, searchQueryIcon, uz.greenwhite.lib.R.color.toolbar_icon_color_silver_dark)
        item.setShowAsAction(10)
        val searchView = MySearchView(activity)
        searchView.isIconified = false
        item.actionView = searchView
        searchView.setOnQueryTextListener(searchQuery)
        searchView.clearFocus()
    }
}

class MoldSearchListQuery(val adapter: RegionAdapter) : MoldSearchQuery() {

    override fun onQueryText(s: String) {
        adapter.predicateSearch = object : MyPredicate<SpinnerOption>() {
            override fun apply(item: SpinnerOption): Boolean = CharSequenceUtil.containsIgnoreCase(item.name, s)
        }
        adapter.searchText = s
        adapter.filter()
    }
}

class RegionAdapter(context: Context) : MyRecyclerAdapter<SpinnerOption>(context) {

    private lateinit var itemClickListener: (SpinnerOption) -> Unit

    override fun populate(vs: ViewSetup, item: SpinnerOption) {
        vs.textView(android.R.id.text1).text = item.name
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val vs = ViewSetup(this.context, android.R.layout.simple_list_item_1)
        val attrs = intArrayOf(uz.greenwhite.lib.R.attr.selectableItemBackground)
        val typedArray = this.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        vs.view.setBackgroundResource(backgroundResource)
        typedArray.recycle()
        return ViewHolder(vs)
    }

    fun setItemClick(itemClickListener: (SpinnerOption) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    override fun itemClick(holder: ViewHolder, item: SpinnerOption) {
        itemClickListener.invoke(item)
    }
}
