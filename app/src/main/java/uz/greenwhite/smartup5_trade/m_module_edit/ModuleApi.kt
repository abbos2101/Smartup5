package uz.greenwhite.smartup5_trade.m_module_edit

import uz.greenwhite.lib.collection.MyArray
import uz.greenwhite.lib.collection.MyMapper
import uz.greenwhite.lib.collection.MyPredicate
import uz.greenwhite.lib.mold.NavigationItem
import uz.greenwhite.lib.util.Util
import uz.greenwhite.smartup.anor.datasource.AnorDS
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession

object ModuleApi {

    private val MAIN_FORM = "trade:module:main_form"
    private val FORM_VISIBLE = "trade:module:form_id_visible"
    private val FORM_ORDER_NO = "trade:module:form_order_no"

    //----------------------------------------------------------------------------------------------

    fun setMainForm(arg: ArgSession, formCode: String, formId: Int) {
        val key = String.format("%s:%s:%s", arg.accountId, formCode, MAIN_FORM)
        AnorDS.getPref().save(key, formId.toString())
    }

    fun getMainForm(arg: ArgSession, formCode: String): Int {
        val key = String.format("%s:%s:%s", arg.accountId, formCode, MAIN_FORM)
        val formId = Util.nvl(AnorDS.getPref().load(key))
        return if (formId.isNotEmpty()) formId.toInt() else -1
    }

    //----------------------------------------------------------------------------------------------

    fun saveFormIdVisible(arg: ArgSession, formCode: String, formId: Int) {
        var visibleFormIds = getVisibleFormIds(arg, formCode)
        visibleFormIds = if (visibleFormIds.contains(formId, MyMapper.identity())) {
            visibleFormIds.filter(object : MyPredicate<Int>() {
                override fun apply(id: Int?): Boolean = id != formId
            })
        } else {
            visibleFormIds.append(formId)
        }

        val key = String.format("%s:%s:%s", arg.accountId, formCode, FORM_VISIBLE)
        AnorDS.getPref().save(key, visibleFormIds.mkString(","))
    }

    fun makeFormVisible(arg: ArgSession, formCode: String, forms: MyArray<NavigationItem>): MyArray<NavigationItem> {
        val ids = getVisibleFormIds(arg, formCode)
        return MyArray.from(forms.filter { !ids.contains(it.id) })
    }

    fun containsVisibleFormId(arg: ArgSession, formCode: String, formId: Int): Boolean {
        return getVisibleFormIds(arg, formCode).contains(formId, MyMapper.identity())
    }

    fun getVisibleFormIds(arg: ArgSession, formCode: String): MyArray<Int> {
        val pref = AnorDS.getPref()
        val key = String.format("%s:%s:%s", arg.accountId, formCode, FORM_VISIBLE);
        val formIds = Util.nvl(pref.load(key))
        return stringToIntegerArray(formIds)
    }

    //----------------------------------------------------------------------------------------------

    fun makeFormOrderNo(arg: ArgSession, formCode: String, forms: MyArray<NavigationItem>): MyArray<NavigationItem> {
        val pref = AnorDS.getPref()
        val key = String.format("%s:%s:%s", arg.accountId, formCode, FORM_ORDER_NO)
        val formIds = Util.nvl(pref.load(key))
        val ids = stringToIntegerArray(formIds)
        val result = ids.mapNotNull { forms.find(it, NavigationItem.KEY_ADAPTER) }
        return MyArray.from(result + forms.filter { !ids.contains(it.id) })
    }

    fun saveFormOrderNo(arg: ArgSession, formCode: String, formIds: Collection<Int>) {
        val pref = AnorDS.getPref()
        val key = String.format("%s:%s:%s", arg.accountId, formCode, FORM_ORDER_NO);
        pref.save(key, formIds.joinToString(separator = ","))
    }

    //----------------------------------------------------------------------------------------------

    private fun stringToIntegerArray(value: String): MyArray<Int> {
        return if (value.isEmpty()) MyArray.emptyArray()
        else MyArray.from(value.split(",").map { it.toInt() })
    }
}
