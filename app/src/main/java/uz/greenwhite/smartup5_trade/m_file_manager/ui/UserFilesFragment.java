package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldSwipeRecyclerContent;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.fab.FloatingActionButton;
import uz.greenwhite.lib.widget.fab.FloatingActionsMenu;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerApi;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgCreateOrRenameDialog;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgFileManager;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;
import uz.greenwhite.smartup5_trade.m_file_manager.job.DownloadFileJob;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

import static uz.greenwhite.smartup5_trade.datasource.DS.getString;

public class UserFilesFragment extends MoldSwipeRecyclerContent<UserFile> {

    public static MoldPageContent newInstance(ArgFileManager arg, @StringRes int titleResId) {
        return newContentInstance(UserFilesFragment.class, Mold.parcelableArgument(arg,
                ArgFileManager.UZUM_ADAPTER), getString(titleResId));
    }

    private ArgFileManager getArgFileManager() {
        return Mold.parcelableArgument(this, ArgFileManager.UZUM_ADAPTER);
    }

    private HashMap<String, Boolean> selectedItems = new HashMap<>();
    private ActionMode actionMode;
    private JobMate jobMate = new JobMate();
    private boolean isSelection = true;

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);
        setHasLongClick(true);
        setEmptyIcon(R.drawable.box_empty);
        setEmptyText(R.string.list_is_empty);
        onRefresh();
    }

    @Override
    public void onContentResume() {
        super.onContentResume();
        Mold.setTitle(getActivity(), R.string.f_manager_myFiles_title);
        Drawable iconCreateFolder = UI.changeDrawableColor(getActivity(), R.drawable.ic_create_new_folder_black_24dp, R.color.white);
        Drawable iconUploadFile = UI.changeDrawableColor(getActivity(), R.drawable.ic_insert_drive_file_black_24dp, R.color.white);
        final FloatingActionsMenu floatMenu = Mold.makeFloatActionMenu(getActivity());
        floatMenu.addButton(makeButton(iconCreateFolder, R.string.f_manager_float_create_folder, new Command() {
            @Override
            public void apply() {
                ArgFileManager arg = getArgFileManager();
                CreateOrRenameFolderDialog.show(getActivity(), ArgCreateOrRenameDialog
                        .newArgCreateFolder(arg, arg.folderId, getListFilteredItems()));
                floatMenu.collapse();
            }
        }));

        floatMenu.addButton(makeButton(iconUploadFile, R.string.f_manager_float_upload_file, new Command() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void apply() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                getContentFragment().startActivityForResult(intent, OpenFolderFragment.FILE_SELECT_CODE);
            }
        }));
    }

    @Override
    public void onRefresh() {
        startRefresh();

        final Scope scope = getArgFileManager().getScope();
        final MyArray<UserFile> userFiles = FileManagerApi.getUserFiles(scope)
                .filter(new MyPredicate<UserFile>() {
                    @Override
                    public boolean apply(UserFile userFile) {
                        return userFile.level.equals("1");
                    }
                });
        setListItems(userFiles);

        jobMate.execute(new ActionJob<>(getArgFileManager(), RT.URI_LOAD_USER_FILES, MyArray.from("1")))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        stopRefresh();
                        if (b) {
                            FileManagerApi.saveUserFiles(scope, s);
                            setListItems(Uzum.toValue(s, UserFile.UZUM_ADAPTER.toArray()));
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                        }
                    }
                });
    }

    @Override
    protected void onListItemChanged() {
        adapter.predicateOthers = new MyPredicate<UserFile>() {
            @Override
            public boolean apply(UserFile userFile) {
                return userFile.level.equals("1");
            }
        };
    }

    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, final UserFile item) {
        ArgFileManager arg = getArgFileManager();

        if (Mold.isActionModeEnabled(getActivity())) {
            if (isSelection)
                toggleSelection(item);

        } else if (item.isFolder()) {
            OpenFolderFragment.open(getActivity(), new ArgFileManager(arg, item.fileId, item.fileId, item.fileName, false, ArgFileManager.EXPLORE, MyArray.<String>emptyArray()));

        } else {
            UI.dialog()
                    .message(item.getFileInfo("Root"))
                    .negative(R.string.cancel, Util.NOOP)
                    .positive(R.string.open, new Command() {
                        @Override
                        public void apply() {
                            downloadFile(getActivity(), item, true);
                        }
                    }).show(getActivity());
        }
    }

    @Override
    protected void onItemLongClick(RecyclerAdapter.ViewHolder holder, UserFile item) {
        if (!Mold.isActionModeEnabled(getActivity())) {
            Mold.enableActionMode(getActivity(), callback);
        }
        if (isSelection)
            toggleSelection(item);

    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.f_manager_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, final UserFile userFile) {
        final FragmentActivity activity = getActivity();
        final ArgFileManager arg = getArgFileManager();

        changeViewBackground(vsItem, userFile);
        vsItem.textView(R.id.tv_filename).setText(userFile.fileName);
        vsItem.textView(R.id.tv_modify_date).setText(getString(
                R.string.f_manager_modify_date,
                String.valueOf(userFile.modifiedOn)
        ));

        vsItem.id(R.id.iv_file_img).setBackgroundResource(userFile.getIconResId(arg.isShared));

        vsItem.id(R.id.iv_file_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Mold.isActionModeEnabled(activity))
                    Mold.enableActionMode(activity, callback);
                if (isSelection)
                    toggleSelection(userFile);
            }
        });

        View more = vsItem.id(R.id.iv_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Mold.isActionModeEnabled(activity)) actionMode.finish();

                PopupBuilder popupOption = UI.popup();
                if (!TextUtils.isEmpty(userFile.extension))
                    popupOption.option(R.string.f_m_upload_download_file, new Command() {
                        @Override
                        public void apply() {
                            downloadFile(activity, userFile, false);
                        }
                    });

                popupOption.option(R.string.f_manager_option_rename, new Command() {
                    @Override
                    public void apply() {
                        CreateOrRenameFolderDialog.show(getActivity(), ArgCreateOrRenameDialog
                                .newArgRenameFile(arg, userFile.fileId, userFile.fileName, getListFilteredItems()));
                    }
                });

                popupOption.option(R.string.f_manager_option_copy, new Command() {
                    @Override
                    public void apply() {
                        startActivityForCopyOrMove(arg, MyArray.from(userFile.fileId), ArgFileManager.COPY, OpenFolderFragment.COPY_REQUEST_CODE);
                    }
                });

                popupOption.option(R.string.f_manager_option_move, new Command() {
                    @Override
                    public void apply() {
                        startActivityForCopyOrMove(arg, MyArray.from(userFile.fileId), ArgFileManager.MOVE, OpenFolderFragment.MOVE_REQUEST_CODE);
                    }
                });

                popupOption.option(R.string.f_m_access, new Command() {
                    @Override
                    public void apply() {
                        ArgFileManager newArg = new ArgFileManager(arg, userFile.folderId, userFile.fileId, false);
                        AccessFragment.open(activity, newArg, R.string.f_m_access);
                    }
                });


                popupOption.option(R.string.f_manager_option_remove, new Command() {
                    @Override
                    public void apply() {
                        UI.dialog().title(R.string.f_m_delete_files)
                                .message(DS.getString(R.string.f_m_want_delete, "1"))
                                .positive(R.string.yes, new Command() {
                                    @Override
                                    public void apply() {
                                        remove(getActivity(), MyArray.from(userFile.fileId));
                                    }
                                })
                                .negative(R.string.no, Util.NOOP)
                                .show(getActivity());
                    }
                });

                popupOption.show(v);

            }
        });
    }


    //--------------------------------------------------------------------------------------------------
    //  Changes the background of selected/unselected views
    private void changeViewBackground(ViewSetup vsItem, UserFile item) {
        if (Util.nvl(selectedItems.get(item.fileId), false)) {
            vsItem.view.setBackgroundColor(DS.getColor(R.color.action_mode_color));
            vsItem.id(R.id.icon_front).setVisibility(View.GONE);
            vsItem.id(R.id.icon_back).setVisibility(View.VISIBLE);
        } else {
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            vsItem.view.setBackgroundResource(backgroundResource);
            typedArray.recycle();
            vsItem.id(R.id.icon_front).setVisibility(View.VISIBLE);
            vsItem.id(R.id.icon_back).setVisibility(View.GONE);
        }
    }

    // Deletes the existing item from the array, if it was clicked again
    private void cleanItems(UserFile item) {
        if (!selectedItems.get(item.fileId)) {
            selectedItems.remove(item.fileId);
        }
        if (selectedItems.size() == 0) {
            actionMode.finish();
            actionMode = null;
        }

    }

    // Toggle the selection of items
    private void toggleSelection(UserFile item) {
        isSelection = true;
        selectedItems.put(item.fileId, !(Util.nvl(selectedItems.get(item.fileId), false)));
        cleanItems(item);
        if (selectedItems.size() != 0 || actionMode != null)
            actionMode.setTitle(DS.getString(R.string.file_manager_action_bar_select_info, String.valueOf(selectedItems.size())));

        adapter.notifyDataSetChanged();
    }

    //    CALLBACK for ACTION MODE
    private ActionMode.Callback callback = new ActionMode.Callback() {

        private static final int MENU_COPY = 3;
        private static final int MENU_MOVE = 4;
        private static final int MENU_REMOVE = 5;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            mSwipeRefreshLayout.setEnabled(false);

            menu.add(Menu.NONE, MENU_COPY, 3, R.string.f_manager_option_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.add(Menu.NONE, MENU_MOVE, 4, R.string.f_manager_option_move).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.add(Menu.NONE, MENU_REMOVE, 5, R.string.f_manager_option_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final MyArray<String> selectItemIds = MyArray.from(selectedItems.keySet());

            actionMode.finish();

            switch (item.getItemId()) {

                case MENU_COPY:
                    startActivityForCopyOrMove(getArgFileManager(), selectItemIds, ArgFileManager.COPY, OpenFolderFragment.COPY_REQUEST_CODE);
                    return true;

                case MENU_MOVE:
                    startActivityForCopyOrMove(getArgFileManager(), selectItemIds, ArgFileManager.MOVE, OpenFolderFragment.MOVE_REQUEST_CODE);
                    return true;

                case MENU_REMOVE:
                    UI.dialog()
                            .title(R.string.f_m_delete_files)
                            .message(DS.getString(R.string.f_m_want_delete, String.valueOf(selectItemIds.size())))
                            .positive(R.string.remove, new Command() {
                                @Override
                                public void apply() {
                                    remove(getActivity(), selectItemIds);
                                }
                            })
                            .negative(R.string.no, Util.NOOP)
                            .show(getActivity());
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectedItems.clear();
            actionMode = null;
            mSwipeRefreshLayout.setEnabled(true);
            adapter.notifyDataSetChanged();
        }
    };

    private FloatingActionButton makeButton(@NonNull Drawable icon,
                                            int titleResId,
                                            @NonNull final Command command) {
        FloatingActionButton fab = new FloatingActionButton(getActivity());
        fab.setSize(uz.greenwhite.lib.widget.fab.FloatingActionButton.SIZE_MINI);
        fab.setIconDrawable(icon);
        fab.setColorNormalResId(R.color.app_color_accent);
        fab.setColorPressedResId(R.color.app_color_accent);
        fab.setStrokeVisible(true);
        fab.setTitle(getString(titleResId));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command.apply();
            }
        });
        return fab;
    }


    private void startActivityForCopyOrMove(ArgFileManager arg, MyArray<String> fileIds, int state, int requestCode) {
        getContentFragment().startActivityForResult
                (OpenFolderFragment.newInstanceForResult(getActivity(), new ArgFileManager(
                        arg, "", false, state, fileIds)), requestCode);
    }

    private void remove(final Activity activity, MyArray<String> fileIds) {
        jobMate.execute(new ActionJob<>(getArgFileManager(), RT.URI_REMOVE_FILE, fileIds))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        if (b) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_deleted).show();
                            onRefresh();
                        } else {
                            UI.alertError(activity, ErrorUtil.getErrorMessage(throwable).message.toString());
                        }
                    }
                });
    }

    private void downloadFile(final Activity activity, final UserFile item, final boolean open) {
        ArgFileManager arg = getArgFileManager();
        if (!FileManagerUtil.fileExistsInDownloadFolder(arg.accountId, item.fileName)) {
            jobMate.executeWithDialog(activity, new DownloadFileJob(arg.accountId, item.sha, item.fileName))
                    .done(new Promise.OnDone<String>() {
                        @Override
                        public void onDone(final String s) {
                            if (open) {
                                FileManagerUtil.openFile(activity, getArgFileManager(), item);
                            } else {
                                Mold.makeSnackBar(activity, R.string.f_m_file_downloaded).setAction(R.string.open, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FileManagerUtil.openFile(activity, getArgFileManager(), item);
                                    }
                                }).show();
                            }
                        }
                    })
                    .fail(new Promise.OnFail() {
                        @Override
                        public void onFail(Throwable throwable) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_not_downloaded).setAction(R.string.repeat, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(activity, item, open);
                                }
                            }).show();
                        }
                    });
        } else {
            FileManagerUtil.openFile(activity, getArgFileManager(), item);
        }
    }
}
