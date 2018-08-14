package uz.greenwhite.smartup5_trade.m_person_edit.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldPageTabFragment;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class NaturalPersonCreateFragment extends MoldPageTabFragment {


    public static void open(ArgPerson arg) {
        Mold.openContent(NaturalPersonCreateFragment.class, Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER));
    }

    public ArgPerson getArgPerson() {
        return Mold.parcelableArgument(this, ArgPerson.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArgPerson argPerson = getArgPerson();

        Mold.setTitle(getActivity(), R.string.add_legal_person_doctor);

        if (!TextUtils.isEmpty(argPerson.roomId)) {
            Mold.setSubtitle(getActivity(), getString(R.string.outlet_info_room, argPerson.getRoom().name));
        }

        addMenu(R.drawable.ic_save_black_24dp, R.string.save, new Command() {
            @Override
            public void apply() {
                savePerson();
            }
        });

        if (argPerson.editPerson()) {
            loadOutletInfo();
        } else {
            reloadContent();
        }
    }

    @Override
    public void reloadContent() {
        reloadContent(null);
    }

    public void reloadContent(@Nullable NaturalPersonInfo inf) {
        ArgPerson arg = getArgPerson();
        NaturalPersonData data = Mold.getData(getActivity());
        if (data == null) {
            if (inf == null) {
                inf = arg.getNaturalPersonInfo();
            }
            data = new NaturalPersonData(arg.accountId, inf);
            Mold.setData(getActivity(), data);
        }
        Scope scope = arg.getScope();

        MyArray<MoldPageContent> sections = MyArray.from(
                NaturalPersonContent.newInstance(arg, getString(R.string.natural_person_doctor)),

                NaturalPersonAdditionallyContent.newInstance(arg, getString(R.string.info)),

                PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_GROUP) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_GROUP) ?
                        PersonCharactsContent.newInstance(arg, getString(R.string.person_charact)) : null
        ).filterNotNull();
        setSections(sections);
    }

    @Override
    public void onAboveContentPopped(Object result) {
        if (result instanceof LatLng) {
            LatLng latLng = (LatLng) result;
            String location = latLng.latitude + "," + latLng.longitude;
            NaturalPersonData data = Mold.getData(getActivity());
            data.info.additionally.vLatLng.setValue(location);

            reloadCurrentContent();
        }
    }

    private void reloadCurrentContent() {
        ViewPager vp = getViewPager();
        if (adapter != null && vp != null && adapter.getCount() > 0) {
            MoldPageContent content = adapter.getItem(vp.getCurrentItem());
            if (content != null) content.reloadContent();
        }
    }

    private void loadOutletInfo() {
        ArgPerson arg = getArgPerson();
        MyArray<String> data = MyArray.from(arg.personId, arg.filialId, arg.roomId);
        this.jobMate.executeWithDialog(getActivity(), new ActionJob<>(arg,
                RT.URI_LOAD_NATURAL_EDIT, data, UzumAdapter.STRING_ARRAY))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            reloadContent(Uzum.toValue(result, NaturalPersonInfo.UZUM_ADAPTER));
                        } else {
                            UI.alertError(getActivity(), error);
                        }
                    }
                });
    }

    private void savePerson() {
        NaturalPersonData data = Mold.getData(getActivity());
        ErrorResult error = data.info.getError();
        if (error.isError()) {
            UI.alertError(getActivity(), error.getErrorMessage());
            return;
        }
        NaturalPersonInfo info = data.info.toValue();

        this.jobMate.executeWithDialog(getActivity(),
                new ActionJob<>(getArgPerson(), RT.URI_CREATE_NATURAL_PERSON, info, NaturalPersonInfo.UZUM_ADAPTER))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            ArgPerson arg = getArgPerson();
                            jobMate.stopListening();
                            jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener());
                            if (!JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {
                                jobMate.execute(new TapeSyncJob(AdminApi.getAccount(arg.accountId)));
                            }
                        } else {
                            UI.alertError(getActivity(), error);
                        }
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        NaturalPersonData data = Mold.getData(getActivity());
        if (data != null && data.info.modified()) {
            UI.dialog()
                    .title(R.string.warning)
                    .message(R.string.exit_dont_save)
                    .negative(R.string.no, Util.NOOP)
                    .positive(R.string.yes, new Command() {
                        @Override
                        public void apply() {
                            getActivity().finish();
                        }
                    }).show(getActivity());
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    private class SyncListener implements LongJobListener<ProgressValue> {

        private final ProgressDialog pd;

        SyncListener() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
        }

        @Override
        public void onStart() {
            pd.setMessage(DS.getString(R.string.outlet_run_full_sync));
            pd.show();
        }

        @Override
        public void onStop(Throwable error) {
            pd.dismiss();
            if (error != null) {
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(error).message);
            } else {
                getActivity().finish();
            }
        }

        @Override
        public void onProgress(ProgressValue progress) {
        }
    }
}
