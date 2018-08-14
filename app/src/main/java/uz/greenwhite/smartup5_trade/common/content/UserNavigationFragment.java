package uz.greenwhite.smartup5_trade.common.content;// 14.11.2016

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.NavigationFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.bean.user.User;
import uz.greenwhite.smartup.anor.bean.user.UserFilial;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;

public abstract class UserNavigationFragment extends NavigationFragment {

    protected abstract ArgSession getArgument();

    protected final JobMate jobMate = new JobMate();

    protected ViewSetup vsHeader;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadUserNavigationHeader();
    }

    @Override
    public void onDrawerOpened() {
        super.onDrawerOpened();
        reloadUserNavigationHeader();
    }

    protected void reloadUserNavigationHeader() {
        if (vsHeader == null) {
            this.vsHeader = new ViewSetup(getActivity(), R.layout.z_navigation_header);
            setNavigationHeader(vsHeader);
        }

        final ArgSession arg = getArgument();
        final Filial filial = arg.getFilial();
        final User user = arg.getUser();
        if (user == null) return;

        ScopeUtil.execute(jobMate, arg, new OnScopeReadyCallback<String>() {
            @Override
            public String onScopeReady(Scope scope) {
                return DSUtil.getFilialRoles(scope).map(new MyMapper<Role, String>() {
                    @Override
                    public String apply(Role role) {
                        return role.name;
                    }
                }).mkString(", ");
            }

            @Override
            public void onDone(String roles) {
                UserFilial userFilial = User.HEADER_FILIAL;
                if (!"0".equals(arg.filialId)) {
                    userFilial = user.filials.find(filial.id, UserFilial.KEY_ADAPTER);
                }
                vsHeader.textView(R.id.tv_user_name).setText(user.name);
                vsHeader.textView(R.id.tv_user_role).setText(getString(R.string.session_role_uc_info, roles));
                vsHeader.textView(R.id.tv_user_filial).setText(getString(R.string.session_filial_uc_info, userFilial.name));

                vsHeader.id(R.id.tv_user_filial).setVisibility(View.VISIBLE);
            }
        });

        jobMate.execute(new FetchImageJob(arg.accountId, user.photoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsHeader.imageView(R.id.iv_avatar).setImageBitmap(result);
                            } else {
                                vsHeader.imageView(R.id.iv_avatar).setImageResource(R.drawable.default_userpic);
                            }
                        } else {
                            vsHeader.imageView(R.id.iv_avatar).setImageResource(R.drawable.default_userpic);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }
}
