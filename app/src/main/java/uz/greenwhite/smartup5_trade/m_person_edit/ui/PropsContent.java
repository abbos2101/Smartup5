package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VOutletProps;

public class PropsContent extends MoldPageContent {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(PropsContent.class, bundle, title);
    }

    private ViewSetup vsRoot;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, int position) {
        this.vsRoot = new ViewSetup(inflater, null, R.layout.oedit_scroll);
        return this.vsRoot.view;
    }

    @Override
    public void onContentResume() {
        super.onContentResume();
        reloadContent();
    }

    public void reloadContent() {
        PersonData data = Mold.getData(getActivity());
        if (data == null) return;
        VOutletProps props = data.info.props;

        MyArray<ViewSetup> views = MyArray.from(
                UIUtils.makeEditText(this, R.string.typeorg, props.typeorg),
                UIUtils.makeEditText(this, R.string.department, props.department),
                UIUtils.makeEditText(this, R.string.typeown, props.typeown),
                UIUtils.makeEditText(this, R.string.orgform, props.orgform),
                UIUtils.makeEditText(this, R.string.ladress, props.laddress),
                UIUtils.makeEditText(this, R.string.inn, props.inn),
                UIUtils.makeEditText(this, R.string.kopf, props.kopf),
                UIUtils.makeEditText(this, R.string.kfc, props.kfc),
                UIUtils.makeEditText(this, R.string.okonx, props.okonx),
                UIUtils.makeEditText(this, R.string.okpo, props.okpo),
                UIUtils.makeEditText(this, R.string.okud, props.okud),
                UIUtils.makeEditText(this, R.string.coato, props.coato),
                UIUtils.makeEditText(this, R.string.coogu, props.coogu),
                UIUtils.makeEditText(this, R.string.oked, props.oked),
                UIUtils.makeEditText(this, R.string.svift, props.svift)
        );
        if (vsRoot != null) {
            ViewGroup vg = vsRoot.viewGroup(R.id.ll_content);
            vg.removeAllViews();
            boolean first = true;
            for (ViewSetup vs : views) {
                if (!first) {
                    vg.addView(new ViewSetup(getActivity(), R.layout.z_divider).view);
                }
                vg.addView(vs.view);
                first = false;
            }
        }
    }
}
