package uz.greenwhite.smartup5_trade.m_shipped.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;

public class SDealNoteFragment extends MoldContentFragment {

    ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.sdeal_note);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SDealData data = Mold.getData(getActivity());

//        vsRoot.imageView(R.id.iv_sdeal_note).setColorFilter(AnorDS.getColor(R.color.normal_silver));
        vsRoot.textView(R.id.tv_sdeal_note).setText(data.vDeal.sDealRef.sDeal.note.note);
    }

}
