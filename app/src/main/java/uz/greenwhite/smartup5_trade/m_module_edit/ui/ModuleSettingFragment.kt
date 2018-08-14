package uz.greenwhite.smartup5_trade.m_module_edit.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.collection.MyMapper
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment
import uz.greenwhite.lib.mold.NavigationItem
import uz.greenwhite.lib.mold.RecyclerAdapter
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.m_module_edit.ModuleApi
import uz.greenwhite.smartup5_trade.m_module_edit.arg.ArgModule
import uz.greenwhite.smartup5_trade.m_session.common.SwipeAndDragHelper
import uz.greenwhite.smartup5_trade.m_session.ui.SessionIndexFragment

class ModuleSettingFragment : MoldContentRecyclerFragment<NavigationItem>(), SwipeAndDragHelper.ActionCompletionContract {

    companion object {
        fun open(activity: Activity, arg: ArgModule) {
            val bundle = Mold.parcelableArgument<ArgModule>(arg, ArgModule.UZUM_ADAPTER)
            Mold.openContent(activity, ModuleSettingFragment::class.java, bundle)
        }
    }

    private val argModule: ArgModule by lazy { Mold.parcelableArgument<ArgModule>(this, ArgModule.UZUM_ADAPTER) }

    private val touchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(SwipeAndDragHelper(this, false));
    }

    private var cListItems: ArrayList<NavigationItem> = ArrayList()
    private var cFormVisible: MyArray<Int> = MyArray.emptyArray()
    private var defaultFormId: Int = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.setTitle(activity, R.string.module_title)

        setHasLongClick(true)
        touchHelper.attachToRecyclerView(cRecycler)
    }

    override fun onStart() {
        super.onStart()
        reloadContent()
    }

    override fun reloadContent() {
        val formItems = argModule.formItems
        defaultFormId = ModuleApi.getMainForm(argModule, argModule.formCode)
        if (defaultFormId == -1 && formItems.nonEmpty() || !formItems.contains(defaultFormId, NavigationItem.KEY_ADAPTER)) {
            if (formItems.nonEmpty()) {
                defaultFormId = formItems.get(0).id
            }
        }
        cListItems = ArrayList(formItems.asList())
        cFormVisible = ModuleApi.getVisibleFormIds(argModule, argModule.formCode)
        listItems = formItems
    }

    override fun onItemClick(holder: RecyclerAdapter.ViewHolder, item: NavigationItem) {
        val popup = UI.popup()

        if (ArgModule.PERSON != argModule.formCode) {
            popup.option(DS.getString(R.string.module_set_main), { ModuleApi.setMainForm(argModule, argModule.formCode, item.id);reloadContent() })
        }

        if (SessionIndexFragment.SYNC != item.id) {
            popup.option(if (ModuleApi.containsVisibleFormId(argModule, argModule.formCode, item.id)) DS.getString(R.string.module_show) else
                DS.getString(R.string.module_hide), { ModuleApi.saveFormIdVisible(argModule, argModule.formCode, item.id); reloadContent() })
        }

        popup.show(holder.itemView)
    }

    override fun onItemLongClick(holder: RecyclerAdapter.ViewHolder?, item: NavigationItem?) {
        touchHelper.startDrag(holder);
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        adapter.notifyItemMoved(oldPosition, newPosition)
        val item = cListItems[oldPosition]
        cListItems.removeAt(oldPosition)
        cListItems.add(newPosition, item)

        ModuleApi.saveFormOrderNo(argModule, argModule.formCode, cListItems.map { it.id })
    }

    override fun onViewSwiped(position: Int) {
        adapter.notifyItemRemoved(position)
        cListItems.removeAt(position)
    }

    override fun adapterGetLayoutResource(): Int {
        return R.layout.z_module_row
    }

    override fun adapterPopulate(vs: ViewSetup, item: NavigationItem) {
        vs.imageView(R.id.iv_icon).setImageDrawable(UI.changeDrawableColor(activity, item.icon, R.color.colorAccent))
        vs.textView(R.id.tv_title).text = item.title

        vs.id<View>(R.id.miv_main).visibility = if (item.id == defaultFormId && ArgModule.PERSON != argModule.formCode) View.VISIBLE else View.GONE
        vs.id<View>(R.id.miv_visible).visibility = if (cFormVisible.contains(item.id, MyMapper.identity())) View.VISIBLE else View.GONE
    }
}
