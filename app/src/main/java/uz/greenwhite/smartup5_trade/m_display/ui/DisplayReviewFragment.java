package uz.greenwhite.smartup5_trade.m_display.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.BitmapUtil;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.BarcodeUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_display.ArgDisplayReview;
import uz.greenwhite.smartup5_trade.m_display.row.ReviewRow;

public class DisplayReviewFragment extends MoldContentFragment {

    public static DisplayReviewFragment newInstance(ArgDisplayReview arg) {
        return Mold.parcelableArgumentNewInstance(DisplayReviewFragment.class,
                arg, ArgDisplayReview.UZUM_ADAPTER);
    }

    public ArgDisplayReview getArgDisplayReview() {
        return Mold.parcelableArgument(this, ArgDisplayReview.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.display_review);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadContent();
    }

    @Override
    public void reloadContent() {
        final ArgDisplayReview arg = getArgDisplayReview();
        final DisplayData data = Mold.getData(getActivity());
        final ReviewRow found = data.getReview(arg.inventoryId, arg.barcode);

        if (found == null) {
            UI.dialog()
                    .cancelable(false)
                    .title(R.string.warning)
                    .message(R.string.outlet_display_not_found)
                    .negative(R.string.close, new Command() {
                        @Override
                        public void apply() {
                            getActivity().onBackPressed();
                        }
                    })
                    .show(getActivity());
            return;
        }
        Mold.setTitle(getActivity(), found.getHeaderTextResId());

        vsRoot.textView(R.id.tv_review_title).setText(found.getTitle());
        vsRoot.textView(R.id.tv_review_detail).setText(found.getDetail());
        vsRoot.imageView(R.id.display_info).setImageDrawable(found.getIconState());

        vsRoot.bind(R.id.et_note, found.item.note);


        if (data.hasEdit()) {
            vsRoot.id(R.id.btn_display_barcode).setVisibility(
                    TextUtils.isEmpty(found.barcode) || found.item.isNotFound() ? View.VISIBLE : View.GONE);
            vsRoot.id(R.id.btn_display_photo).setVisibility(!found.photo && !found.item.isNotFound() ? View.VISIBLE : View.GONE);
            vsRoot.id(R.id.et_note).setVisibility(!found.item.isNotFound() ? View.VISIBLE : View.GONE);

        } else {
            vsRoot.id(R.id.btn_display_barcode).setVisibility(View.GONE);
            vsRoot.id(R.id.btn_display_photo).setVisibility(View.GONE);
            vsRoot.id(R.id.et_note).setEnabled(false);
        }

        vsRoot.id(R.id.btn_display_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewRow found = data.getReview(arg.inventoryId, arg.barcode);
                if (found.item.isNotFound()) {
                    startActivityForResult(BarcodeUtil.barcodeIntent(getActivity()), BarcodeUtil.BARCODE_REQUEST);
                } else {
                    v.setVisibility(View.GONE);
                }
            }
        });

        vsRoot.id(R.id.btn_display_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewRow found = data.getReview(arg.inventoryId, arg.barcode);
                if (!found.item.isNotFound()) {
                    openCamera();
                } else {
                    v.setVisibility(View.GONE);
                }
            }
        });

        if (found.photo) {
            Scope scope = arg.getScope();
            Bitmap bitmap = scope.ds.loadThumbSha(found.item.photoSha.getText());
            vsRoot.imageView(R.id.iv_display_photo).setVisibility(View.VISIBLE);
            vsRoot.imageView(R.id.iv_display_photo).setImageBitmap(bitmap);
            if (data.hasEdit()) {
                vsRoot.imageView(R.id.iv_display_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ReviewRow found = data.getReview(arg.inventoryId, arg.barcode);
                        if (found.photo) {
                            UI.dialog().title(R.string.warning)
                                    .message(R.string.display_do_you_want_to_delete)
                                    .negative(R.string.cancel, Util.NOOP)
                                    .positive(R.string.remove, new Command() {
                                        @Override
                                        public void apply() {
                                            try {
                                                Scope scope = arg.getScope();
                                                DisplayData data = Mold.getData(getActivity());
                                                data.vDisplay.removePhotoToReview(scope, found.barcode);
                                                reloadContent();
                                            } catch (Exception e) {
                                                if (BuildConfig.DEBUG) e.printStackTrace();
                                                ErrorUtil.saveThrowable(e);
                                                UI.alertError(getActivity(), e);
                                            }
                                        }
                                    }).show(getActivity());
                        }
                    }
                });
            }
        } else {
            vsRoot.imageView(R.id.iv_display_photo).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(found.displayPhoto)) {
            jobMate.execute(new FetchImageJob(arg.accountId, found.displayPhoto))
                    .always(new Promise.OnAlways<Bitmap>() {
                        @Override
                        public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                            if (resolved) {
                                if (result != null) {
                                    vsRoot.imageView(R.id.iv_image).setImageBitmap(result);
                                } else {
                                    vsRoot.imageView(R.id.iv_image).setImageResource(R.drawable.display_photo);
                                }
                            } else {
                                vsRoot.imageView(R.id.iv_image).setImageResource(R.drawable.display_photo);
                                if (error != null) error.printStackTrace();
                            }
                        }
                    });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(),true);


        if (requestCode == BarcodeUtil.BARCODE_REQUEST) {
            final String barcode = BarcodeUtil.getBarcodeInActivityResult(getActivity(), requestCode, resultCode, intent);
            if (TextUtils.isEmpty(barcode)) return;
            try {
                final ArgDisplayReview arg = getArgDisplayReview();
                final DisplayData data = Mold.getData(getActivity());
                final ReviewRow found = data.getReview(arg.inventoryId, arg.barcode);
                if (found.item.isNotFound()) {
                    data.vDisplay.foundElseLinkReview(barcode, found.displayInventId);
                    reloadContent();
                } else {
                    UI.alertError(getActivity(), DS.getString(R.string.outlet_display_barcode_already_avialable));
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
                ErrorUtil.saveThrowable(e);
                UI.alertError(getActivity(), e);
            }
        } else if (requestCode == CAMERA_RESULT && resultCode == Activity.RESULT_OK) {
            try {
                ArgDisplayReview arg = getArgDisplayReview();
                Bitmap bitmap = BitmapUtil.decodeFile(getPhotoTempFile(arg.accountId), 100);
                DisplayData data = Mold.getData(getActivity());
                data.vDisplay.addPhotoToReview(arg.getScope(), arg.barcode, bitmap);
                reloadContent();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
                ErrorUtil.saveThrowable(e);
                UI.alertError(getActivity(), e);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int CAMERA = 1001;
    private static final int CAMERA_RESULT = 1002;


    private File getPhotoTempFile(String accountId) {
        return new File(DS.getServerPath(accountId), "image.tmp");
    }

    private void openCamera() {
        if (!SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA);
            return;
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            ArgDisplayReview arg = getArgDisplayReview();
            File photoFile = getPhotoTempFile(arg.accountId);
            Uri uriForFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uriForFile = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
            } else {
                uriForFile = Uri.fromFile(photoFile);
            }
            i.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            startActivityForResult(i, CAMERA_RESULT);
        } else {
            UI.alert(getActivity(), getString(R.string.warning), getString(R.string.deal_camera_app_not_found));
        }
    }
}
