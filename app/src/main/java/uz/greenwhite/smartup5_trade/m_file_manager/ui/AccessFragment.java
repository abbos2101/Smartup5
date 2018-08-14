package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentSwipeRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgFileManager;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.FileAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.NewAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.User;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class AccessFragment extends MoldContentSwipeRecyclerFragment<FileAccess> {

    public static void open(Activity activity, ArgFileManager arg, @StringRes int titleResId) {
        Mold.addContent(activity,
                Mold.parcelableArgumentNewInstance(AccessFragment.class, arg, ArgFileManager.UZUM_ADAPTER), DS.getString(titleResId));
    }

    private ArgFileManager getArg() {
        return Mold.parcelableArgument(this, ArgFileManager.UZUM_ADAPTER);
    }

    private JobMate jobMate = new JobMate();
    private MyArray<FileAccess> tempItems = MyArray.emptyArray();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(R.string.list_is_empty);
        setEmptyIcon(R.drawable.ic_visibility_off_black_48dp);

        addMenu(R.drawable.ic_person_add_black_24dp, R.string.add, new Command() {
            @Override
            public void apply() {
                openDialog("");
            }
        });
        addMenu(R.drawable.ic_done_black_24dp, R.string.send, new Command() {
            @Override
            public void apply() {
                changeFileAccess(new NewAccess(getArg().fileId, adapter.getItems()));
            }
        });

        onRefresh();
    }

    @Override
    public void onRefresh() {
//        loads attached user to the file (those for whom access is given)
        loadFileAccess(getArg().fileId);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.file_access_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vs, final FileAccess item) {
        vs.textView(R.id.tv_user_name).setText(item.userName);
        vs.textView(R.id.tv_access_type).setText(item.getAccessName());
        vs.id(R.id.iv_remove_access).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListItems(adapter.getItems().filter(new MyPredicate<FileAccess>() {
                    @Override
                    public boolean apply(FileAccess val) {
                        return !item.forUserId.equals(val.forUserId);
                    }
                }));
            }
        });
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, FileAccess item) {
        super.onItemClick(holder, item);
        openDialog(item.forUserId);
    }

    //----------------------------------------------------------------------------------------------
    private void loadFileAccess(final String fileId) {
        jobMate.execute(new ActionJob<>(getArg(), RT.URI_LOAD_ACCESS, MyArray.from(fileId)))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String s, Throwable throwable) {
                        stopRefresh();
                        if (resolved) {
                            //the file accessors and access types are loaded here saved to HashMap for further use
                            setListItems(Uzum.toValue(s, FileAccess.UZUM_ADAPTER.toArray()));
                        } else {
                            UI.alert(getActivity(), DS.getString(R.string.error), ErrorUtil.getErrorMessage(throwable.getMessage()).message);
                        }
                    }
                });
    }

    private void changeFileAccess(NewAccess json) {
        jobMate.executeWithDialog(getActivity(), new ActionJob<>(getArg(), RT.URI_CHANGE_ACCESS, json, NewAccess.UZUM_ADAPTER))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String s, Throwable throwable) {
                        if (resolved) {
                            Mold.popContent(getActivity());
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable.getMessage()).message);
                        }
                    }
                });
    }

    public void addItem(final FileAccess item, final String oldUserId) {
        MyArray<FileAccess> filter = adapter.getItems().filter(new MyPredicate<FileAccess>() {
            @Override
            public boolean apply(FileAccess fileAccess) {
                return !fileAccess.forUserId.equals(oldUserId);
            }
        });
        setListItems(filter.append(item));
    }

    private void openDialog(final String userId) {
        final ArgFileManager arg = getArg();
        jobMate.execute(new ActionJob<>(arg, uz.greenwhite.smartup.anor.datasource.RT.URI_LOAD_RECIPIENTS))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            MyArray<User> users = Uzum.toValue(result, User.UZUM_ADAPTER.toArray());
                            AccessDialog.show(getActivity(),
                                    new ArgAccess(arg.accountId, arg.filialId,
                                            arg.fileId, adapter.getItems(), userId, users));
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(error.getMessage()).message);
                        }
                    }
                });
    }
}
