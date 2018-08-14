package uz.greenwhite.smartup5_trade.m_deal.ui;// 09.08.2016

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.RootUtil;
import uz.greenwhite.smartup5_trade.common.VerticalScrollingTextView;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgPhotoInfo;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhotoForm;
import uz.greenwhite.smartup5_trade.m_session.bean.PhotoType;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class PhotoFragment extends DealFormRecyclerFragment<VDealPhoto> {

    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }

    private static final int CAMERA = 1;

    private VDealPhotoForm form;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.photo);
        cRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        form = DealUtil.getDealForm(this);

        final DealData data = Mold.getData(getActivity());
        if (data.hasEdit()) {
            Mold.makeFloatAction(getActivity(), R.drawable.ic_add_black_24dp)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ScopeUtil.execute(data.accountId, data.filialId, new OnScopeReadyCallback<Boolean>() {
                                @Override
                                public Boolean onScopeReady(Scope scope) {
                                    return !RootUtil.isDeviceRooted(getActivity(), scope);
                                }

                                @Override
                                public void onDone(Boolean result) {
                                    if (result) takePhoto();
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        setListItems(form.photos.getItems());
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, VDealPhoto item) {
        ArgPhotoInfo arg = new ArgPhotoInfo(getArgDeal(), form.code, item.sha);
        Mold.addContent(getActivity(), PhotoInfoFragment.newInstance(arg));
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_photo_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealPhoto item) {
        VerticalScrollingTextView tvDetails = (VerticalScrollingTextView) vsItem.textView(R.id.tv_detail);
        tvDetails.setText(item.tvDetail());
        tvDetails.setContinuousScrolling(true);
        tvDetails.setMovementMethod(new ScrollingMovementMethod());
        tvDetails.scroll();

        vsItem.textView(R.id.tv_date).setText(DateUtil.convert(item.date, DateUtil.FORMAT_AS_DATE));

        DealData data = Mold.getData(getActivity());
        ScopeUtil.execute(data.accountId, data.filialId, new OnScopeReadyCallback<Bitmap>() {
            @Override
            public Bitmap onScopeReady(Scope scope) {
                return form.getPhoto(scope, item);
            }

            @Override
            public void onDone(Bitmap bitmap) {
                vsItem.imageView(R.id.photo).setImageBitmap(bitmap);
            }
        });
    }

    private static final int CAMERA_RESULT = 100;

    private void takePhoto() {
        DealData dealData = Mold.getData(getActivity());
        MyArray<PhotoType> photoTypes = dealData.vDeal.dealRef.getPhotoType()
                .sort(new Comparator<PhotoType>() {
                    @Override
                    public int compare(PhotoType l, PhotoType r) {
                        int compare = MyPredicate.compare(l.orderNo, r.orderNo);
                        if (compare == 0) {
                            return CharSequenceUtil.compareToIgnoreCase(l.name, r.name);
                        }
                        return compare;
                    }
                });


        switch (photoTypes.size()) {
            case 0:
                UI.alert(getActivity(), getString(R.string.warning), getString(R.string.deal_photo_type_not_fond));
                break;
            case 1:
                capturePhoto(photoTypes.get(0).typeId);
                break;
            default:
                UI.dialog()
                        .title(R.string.deal_photo_type_dialog)
                        .option(photoTypes, new DialogBuilder.CommandFacade<PhotoType>() {
                            @Override
                            public CharSequence getName(PhotoType val) {
                                return val.name;
                            }

                            @Override
                            public void apply(PhotoType val) {
                                capturePhoto(val.typeId);
                            }
                        }).show(getActivity());
                break;
        }
    }

    private void capturePhoto(String photoTypeId) {
        DealData dealData = Mold.getData(getActivity());
        dealData.photoTypeId = photoTypeId;
        if (!SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA);
            return;
        }
        openCamera();
    }

    private File getPhotoTempFile(String accountId) {
        return new File(DS.getServerPath(accountId), "image.tmp");
    }

    private void openCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            DealData dealData = Mold.getData(getActivity());
            File photoFile = getPhotoTempFile(dealData.accountId);
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

    //-------------------------------------RESULT---------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA && SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.CAMERA)) {
            openCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT && resultCode == Activity.RESULT_OK) {
            DealData dealData = Mold.getData(getActivity());

            final Setting setting = dealData.vDeal.dealRef.setting;
            final PhotoType photoType = form.dealRef.getPhotoType(dealData.photoTypeId);
            final File photoFile = getPhotoTempFile(dealData.accountId);
            final MyArray<String> photoDealInfo = getPhotoDealInfo(dealData, photoType.name);

            ScopeUtil.execute(dealData.accountId, dealData.filialId, new OnScopeReadyCallback<Void>() {
                @Override
                public Void onScopeReady(Scope scope) {
                    try {
                        form.addPhoto(scope, setting, photoType, photoFile, photoDealInfo);
                    } catch (Exception e) {
                        UI.alertError(getActivity(), e);
                    }
                    return null;
                }

                @Override
                public void onDone(Void aVoid) {
                    reloadContent();
                }

                @Override
                public void onFail(Throwable throwable) {
                    super.onFail(throwable);
                    UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                }
            });
        }
    }

    private MyArray<String> getPhotoDealInfo(DealData d, String photoType) {
        Deal deal = d.vDeal.dealRef.dealHolder.deal;
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATETIME);
        String outletName = Util.nvl(d.vDeal.dealRef.outlet.name);
        return MyArray.from(
                getString(R.string.deal_photo_outlet_name, deal.outletId, outletName),
                getString(R.string.deal_photo_type, photoType),
                getString(R.string.deal_photo_date, today));
    }
}
