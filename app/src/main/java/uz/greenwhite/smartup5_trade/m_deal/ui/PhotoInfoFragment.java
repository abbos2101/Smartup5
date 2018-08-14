package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgPhotoInfo;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhotoForm;

public class PhotoInfoFragment extends DealFormContentFragment {

    public static PhotoInfoFragment newInstance(ArgPhotoInfo arg) {
        return Mold.parcelableArgumentNewInstance(PhotoInfoFragment.class,
                Mold.parcelableArgument(arg, ArgPhotoInfo.UZUM_ADAPTER));
    }

    public ArgPhotoInfo getArgPhotoInfo() {
        return Mold.parcelableArgument(this, ArgPhotoInfo.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.deal_photo_info);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.photo);

        final ArgPhotoInfo arg = getArgPhotoInfo();
        final DealData data = Mold.getData(getActivity());
        final VDealPhotoForm form = DealUtil.getDealForm(getActivity(), arg.formCode);
        final VDealPhoto vDealPhoto = form.findVDealPhoto(arg.photoSha);

        if (data.hasEdit()) {
            addMenu(R.drawable.ic_delete_black_24dp, R.string.remove, new Command() {
                @Override
                public void apply() {
                    UI.confirm(getActivity(), getString(R.string.warning), getString(R.string.deal_photo_remove_msg),
                            new Command() {
                                @Override
                                public void apply() {
                                    form.deletePhoto(arg.photoSha);
                                    Mold.popContent(getActivity());
                                }
                            });
                }
            });

            Mold.makeFloatAction(getActivity(), R.drawable.ic_edit_black_24dp)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showEditDialog();
                        }
                    });
        }

        ScopeUtil.execute(data.accountId, data.filialId, new OnScopeReadyCallback<Bitmap>() {
            @Override
            public Bitmap onScopeReady(Scope scope) {
                return form.getFullPhoto(scope, vDealPhoto);
            }

            @Override
            public void onDone(Bitmap bitmap) {
                vsRoot.imageView(R.id.pv_photo).setImageBitmap(bitmap);
            }
        });
    }

    private void showEditDialog() {
        ArgPhotoInfo arg = getArgPhotoInfo();
        VDealPhotoForm form = DealUtil.getDealForm(getActivity(), arg.formCode);
        VDealPhoto vDealPhoto = form.findVDealPhoto(arg.photoSha);

        ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_photo_edit);
        UI.bind(vs.spinner(R.id.sp_photo_type), vDealPhoto.photoType, true);
        vs.bind(R.id.et_photo_note, vDealPhoto.note);
        UI.bottomSheet().contentView(vs.view).show(getActivity());
    }
}
