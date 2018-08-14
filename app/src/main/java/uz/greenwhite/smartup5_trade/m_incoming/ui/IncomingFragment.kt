package uz.greenwhite.smartup5_trade.m_incoming.ui

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.greenwhite.lib.mold.Mold
import uz.greenwhite.lib.mold.MoldContentFragment
import uz.greenwhite.lib.util.SysUtil
import uz.greenwhite.lib.util.Util
import uz.greenwhite.lib.view_setup.UI
import uz.greenwhite.lib.view_setup.ViewSetup
import uz.greenwhite.smartup5_trade.R
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil
import uz.greenwhite.smartup5_trade.datasource.DS
import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_incoming.IncomingData
import uz.greenwhite.smartup5_trade.m_incoming.arg.ArgIncoming

class IncomingFragment : MoldContentFragment() {

    companion object {
        fun open(activity: Activity, arg: ArgIncoming) {
            val bundle = Mold.parcelableArgument(arg, ArgIncoming.UZUM_ADAPTER)
            Mold.openContent(activity, IncomingFragment::class.java, bundle)
        }

        fun newInstance(arg: ArgIncoming): MoldContentFragment {
            val bundle = Mold.parcelableArgument(arg, ArgIncoming.UZUM_ADAPTER)
            return Mold.parcelableArgumentNewInstance(IncomingFragment::class.java, bundle)
        }

        val K_CAMERA: Int = 1
    }


    private val argIncoming: ArgIncoming get() = Mold.parcelableArgument(this, ArgIncoming.UZUM_ADAPTER)

    private var vsRoot: ViewSetup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vsRoot = ViewSetup(inflater, container, R.layout.incoming)
        return vsRoot!!.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mold.setTitle(activity, R.string.warehouse_incoming)
        Mold.setSubtitle(activity, argIncoming.warehouse.name)

        if (Mold.getData<IncomingData>(activity) == null) {
            val incomingHolder = argIncoming.incomingHolder
            ScopeUtil.executeWithDialog(activity, jobMate, argIncoming, object : OnScopeReadyCallback<IncomingData>() {
                override fun onScopeReady(scope: Scope): IncomingData = IncomingData(scope, incomingHolder)
                override fun onDone(e: IncomingData) {
                    Mold.setData(activity, e)
                    makeView()
                }

                override fun onFail(throwable: Throwable?) {
                    super.onFail(throwable)
                    UI.alertError(activity, throwable)
                }
            })
        } else {
            makeView()
        }
    }

    private fun makeView() {
        val data = Mold.getData<IncomingData>(activity)

        vsRoot!!.textView(R.id.tv_date).text = UI.html().v(DS.getString(R.string.incoming_date)).fRed().v(" *").fRed().html()
        vsRoot!!.textView(R.id.tv_incoming_number).text = UI.html().v(DS.getString(R.string.incoming_number)).fRed().v(" *").fRed().html()
        vsRoot!!.textView(R.id.tv_currency).text = UI.html().v(DS.getString(R.string.incoming_currency)).fRed().v(" *").fRed().html()
        vsRoot!!.textView(R.id.tv_provider).text = UI.html().v(DS.getString(R.string.incoming_provider)).fRed().v(" *").fRed().html()

        vsRoot!!.editText(R.id.et_note).hint = UI.html().v(DS.getString(R.string.incoming_note)).html()

        vsRoot!!.bind(R.id.et_date, data.vIncoming.vHeader.incomingDate)
        vsRoot!!.bind(R.id.et_incoming_number, data.vIncoming.vHeader.incomingNumber)

        vsRoot!!.makeDatePicker(R.id.et_date)

        UI.bind(vsRoot!!.spinner(R.id.sp_currency), data.vIncoming.vHeader.currency, true)
        UI.bind(vsRoot!!.spinner(R.id.sp_provider), data.vIncoming.vHeader.providerPerson, true)

        vsRoot!!.bind(R.id.et_note, data.vIncoming.vHeader.note)

        val vsFooter = ViewSetup(activity, R.layout.incoming_footer)
        val bottomSheet = Mold.makeBottomSheet(activity, vsFooter.view)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        vsFooter.id<View>(R.id.btn_close).setOnClickListener { activity.finish() }
        vsFooter.id<View>(R.id.btn_done).setOnClickListener { openIncomingProducts() }
        vsFooter.id<View>(R.id.btn_products).setOnClickListener { Mold.addContent(activity, IncomingProductFragment.newInstance(argIncoming)) }

        val state = data.vIncoming.holder.state
        if (state.isNotSaved || state.isSaved) {
            vsFooter.id<View>(R.id.btn_close).visibility = View.VISIBLE
            vsFooter.id<View>(R.id.btn_done).visibility = View.VISIBLE
        } else {
            vsFooter.id<View>(R.id.btn_products).visibility = View.VISIBLE
        }

        vsRoot!!.id<View>(R.id.et_date).isEnabled = data.vIncoming.holder.state.isSaved || data.vIncoming.holder.state.isNotSaved
        vsRoot!!.id<View>(R.id.et_incoming_number).isEnabled = data.vIncoming.holder.state.isSaved || data.vIncoming.holder.state.isNotSaved
        vsRoot!!.id<View>(R.id.sp_currency).isEnabled = data.vIncoming.holder.state.isSaved || data.vIncoming.holder.state.isNotSaved
        vsRoot!!.id<View>(R.id.sp_provider).isEnabled = data.vIncoming.holder.state.isSaved || data.vIncoming.holder.state.isNotSaved
        vsRoot!!.id<View>(R.id.et_note).isEnabled = data.vIncoming.holder.state.isSaved || data.vIncoming.holder.state.isNotSaved
    }

    private fun openIncomingProducts() {
        val data = Mold.getData<IncomingData>(activity)
        val error = data.vIncoming.vHeader.error

        if (error.isError) {
            UI.alertError(activity, error.errorMessage)
        } else {
            if (!SysUtil.checkSelfPermissionGranted(activity, Manifest.permission.CAMERA)) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), K_CAMERA)
                return
            }

            Mold.addContent(activity, IncomingProductFragment.newInstance(argIncoming))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == K_CAMERA) {
            if (SysUtil.checkSelfPermissionGranted(activity, Manifest.permission.CAMERA)) {
                openIncomingProducts()
            } else {
                Mold.addContent(activity, IncomingProductFragment.newInstance(argIncoming))
            }
        }
    }

    override fun onBackPressed(): Boolean {
        UI.dialog()
                .title(R.string.warning)
                .message(R.string.exit_dont_save)
                .negative(R.string.no, Util.NOOP)
                .positive(R.string.yes) { activity.finish() }.show(activity)
        return true
    }
}
