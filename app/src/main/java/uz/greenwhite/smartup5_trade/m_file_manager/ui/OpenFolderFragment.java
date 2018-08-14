package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.HashMap;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentSwipeRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.lib.widget.fab.FloatingActionButton;
import uz.greenwhite.lib.widget.fab.FloatingActionsMenu;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerApi;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgCreateOrRenameDialog;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgFileManager;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.CopyOrMoveFile;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.SharedFile;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;
import uz.greenwhite.smartup5_trade.m_file_manager.job.DownloadFileJob;
import uz.greenwhite.smartup5_trade.m_file_manager.job.SendFileJob;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

import static android.app.Activity.RESULT_OK;
import static uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil.getPath;

public class OpenFolderFragment extends MoldContentSwipeRecyclerFragment<UserFile> {

    public static OpenFolderFragment newInstance(ArgFileManager arg) {
        return Mold.parcelableArgumentNewInstance(OpenFolderFragment.class,
                arg, ArgFileManager.UZUM_ADAPTER);
    }

    public static Intent newInstanceForResult(Activity activity, ArgFileManager arg) {
        return Mold.newContent(activity, OpenFolderFragment.class, Mold.parcelableArgument(arg, ArgFileManager.UZUM_ADAPTER));
    }

    public static void open(Activity activity, ArgFileManager arg) {
        Mold.openContent(activity, OpenFolderFragment.class, Mold.parcelableArgument(arg, ArgFileManager.UZUM_ADAPTER));
    }

    public static final int COPY_REQUEST_CODE = 1;
    public static final int MOVE_REQUEST_CODE = 2;
    public static final int FILE_SELECT_CODE = 11;

    Scope scope;
    private JobMate jobMate = new JobMate();
    private HashMap<String, Boolean> selectedItems = new HashMap<>();
    private ActionMode actionMode;
    private boolean isSelection = true;

    public ArgFileManager getArgFileManager() {
        return Mold.parcelableArgument(this, ArgFileManager.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        setHasLongClick(true);
        setEmptyIcon(R.drawable.box_empty);
        setEmptyText(R.string.list_is_empty);
        setListItems(MyArray.<UserFile>emptyArray());

        final ArgFileManager arg = getArgFileManager();
        scope = getArgFileManager().getScope();

        if (arg.isShared)
            loadSharedFilesFromDb();

        else {
            loadUserFilesFromDb();
        }

        if (arg.isExplore()) {
            Mold.setTitle(getActivity(), arg.fileName);
        } else if (arg.isShared) {
            isSelection = false;
        } else if (arg.isMoveOrCopy()) {
            isSelection = false;
            if (arg.state == ArgFileManager.COPY) {
                Mold.setTitle(getActivity(), R.string.f_manager_copy_title);
                addMenu(R.drawable.ic_done_black_24dp, R.string.ok, new Command() {
                    @Override
                    public void apply() {
                        copyFile(getActivity(), arg.fileIdsToCopyOrMove, arg.folderId);
                    }
                });
            } else {
                Mold.setTitle(getActivity(), R.string.f_manager_move_title);
                addMenu(R.drawable.ic_done_black_24dp, R.string.ok, new Command() {
                    @Override
                    public void apply() {
                        moveFile(getActivity(), arg.fileIdsToCopyOrMove, arg.folderId);
                    }
                });
            }

            addMenu(R.drawable.ic_cancel_black_24dp, R.string.cancel, new Command() {
                @Override
                public void apply() {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            });
        }

    }

    @Override
    public void reloadContent() {
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();

        final ArgFileManager arg = getArgFileManager();
        if (!arg.isShared) {
            Drawable iconCreateFolder = UI.changeDrawableColor(getActivity(), R.drawable.ic_create_new_folder_black_24dp, R.color.white);
            Drawable iconUploadFile = UI.changeDrawableColor(getActivity(), R.drawable.ic_insert_drive_file_black_24dp, R.color.white);

            final FloatingActionsMenu floatMenu = Mold.makeFloatActionMenu(getActivity());
            floatMenu.addButton(makeButton(iconCreateFolder, R.string.f_manager_float_create_folder, new Command() {
                @Override
                public void apply() {
                    CreateOrRenameFolderDialog.show(getActivity(), ArgCreateOrRenameDialog
                            .newArgCreateFolder(arg, arg.folderId, getListFilteredItems()));
                    floatMenu.collapse();
                }
            }));

            floatMenu.addButton(makeButton(iconUploadFile, R.string.f_manager_float_upload_file, new Command() {
                @Override
                public void apply() {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, FILE_SELECT_CODE);
                }
            }));
        }
    }

    @Override
    public void onRefresh() {
        startRefresh();
        if (getArgFileManager().isShared) {
            loadSharedFiles();
        } else {
            loadUserFiles();
        }
    }

    @Override
    protected void onListItemChanged() {

        adapter.predicateOthers = new MyPredicate<UserFile>() {
            @Override
            public boolean apply(UserFile userFile) {
                return userFile.folderId.equals(getArgFileManager().folderId);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(),true);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    UI.dialog()
                            .title(R.string.f_m_upload_download_file)
                            .message(R.string.f_m_want_upload_download)
                            .positive(R.string.yes, new Command() {
                                @Override
                                public void apply() {
                                    //noinspection ConstantConditions
                                    try {
                                        String path = getPath(getContext(), data.getData());

                                        if (TextUtils.isEmpty(path)) {
                                            UI.alertError(getActivity(), DS.getString(R.string.file_manager_dir_is_empty));
                                            return;
                                        }

                                        File file = new File(path);
                                        fileUpload(getActivity(), file, getArgFileManager().fileId);
                                    } catch (Exception e) {
                                        UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
                                    }
                                }
                            })
                            .negative(R.string.no, Util.NOOP)
                            .show(getActivity());
                    break;
            }
        }
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, final UserFile item) {
        ArgFileManager arg = getArgFileManager();

        if (Mold.isActionModeEnabled(getActivity())) {
            if (isSelection)
                toggleSelection(item);

        } else if (TextUtils.isEmpty(item.sha)) {
            Mold.addContent(getActivity(),
                    OpenFolderFragment.newInstance(new ArgFileManager(arg, item.fileId, item.fileId, item.fileName, arg.isShared,
                            arg.getState(), arg.fileIdsToCopyOrMove)));
        } else {
            UI.dialog()
                    .message(item.getFileInfo(arg.fileName))
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
        if (!getArgFileManager().isShared && isSelection) {
            if (!Mold.isActionModeEnabled(getActivity())) {
                Mold.enableActionMode(getActivity(), callback);
            }
            toggleSelection(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.f_manager_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final UserFile item) {
        final FragmentActivity activity = getActivity();
        final ArgFileManager arg = getArgFileManager();

        changeViewBackground(vsItem, item);

        vsItem.textView(R.id.tv_filename).setText(item.fileName);
        vsItem.id(R.id.iv_file_img).setBackgroundResource(item.getIconResId(arg.isShared));

        if (item instanceof SharedFile) {
            SharedFile sharedFile = (SharedFile) item;
            vsItem.textView(R.id.tv_modify_date).setText(getString(R.string.f_manager_modify_date, arg.isShared ?
                    sharedFile.modifiedOn + "\n| " + sharedFile.ownerName : sharedFile.modifiedOn));

        } else {
            vsItem.textView(R.id.tv_modify_date).setText(getString(R.string.f_manager_modify_date, item.modifiedOn));
        }

        if (!arg.isShared && isSelection)
            vsItem.id(R.id.iv_file_img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arg.isExplore()) {
                        if (!Mold.isActionModeEnabled(activity))
                            Mold.enableActionMode(activity, callback);
                        toggleSelection(item);
                    }
                }
            });

        View more = vsItem.id(R.id.iv_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Mold.isActionModeEnabled(activity)) actionMode.finish();

                PopupBuilder popupOption = UI.popup();
                if (!item.isFolder()) {
                    popupOption.option(R.string.f_m_upload_download_file, new Command() {
                        @Override
                        public void apply() {
                            downloadFile(activity, item, false);
                        }
                    });
                }

                if (!arg.isShared) {
                    popupOption.option(R.string.f_manager_option_rename, new Command() {
                        @Override
                        public void apply() {
                            CreateOrRenameFolderDialog.show(getActivity(), ArgCreateOrRenameDialog
                                    .newArgRenameFile(arg, item.fileId, item.fileName, getListFilteredItems()));
                        }
                    });

                    popupOption.option(R.string.f_manager_option_copy, new Command() {
                        @Override
                        public void apply() {
                            startActivityForCopyOrMove(arg, MyArray.from(item.fileId), ArgFileManager.COPY, COPY_REQUEST_CODE);
                        }
                    });


                    popupOption.option(R.string.f_manager_option_move, new Command() {
                        @Override
                        public void apply() {
                            startActivityForCopyOrMove(arg, MyArray.from(item.fileId), ArgFileManager.MOVE, MOVE_REQUEST_CODE);
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
                                            removeFile(getActivity(), MyArray.from(item.fileId));
                                        }
                                    })
                                    .negative(R.string.no, Util.NOOP)
                                    .show(getActivity());
                        }
                    });
                }
                popupOption.show(v);
            }
        });

        if (arg.isMoveOrCopy()) {
            more.setVisibility(View.GONE);
        }
    }

    //--------------------------------------------------------------------------------------------------

    private void loadUserFiles() {
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

    private void loadSharedFiles() {
        jobMate.execute(new ActionJob<>(getArgFileManager(), RT.URI_LOAD_SHARED_FILES))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        stopRefresh();
                        if (b) {
                            FileManagerApi.saveUserFiles(scope, s);
                            setListItems(Uzum.toValue(s, SharedFile.UZUM_ADAPTER.toArray()).<UserFile>toSuper());
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                        }
                    }
                });
    }

    private void loadUserFilesFromDb() {
        final ArgFileManager arg = getArgFileManager();

        MyArray<UserFile> myFiles = FileManagerApi.getUserFiles(scope).filter(new MyPredicate<UserFile>() {
            @Override
            public boolean apply(UserFile userFile) {
                return userFile.folderId.equals(arg.folderId);
            }
        });

        if (arg.state == ArgFileManager.MOVE) {
            final MyArray<String> fileIds = arg.fileIdsToCopyOrMove;
            myFiles = myFiles.filter(new MyPredicate<UserFile>() {
                @Override
                public boolean apply(UserFile userFile) {
                    return !fileIds.contains(userFile.fileId, MyMapper.<String>identity());
                }
            });
        }
        setListItems(myFiles);
    }

    private void loadSharedFilesFromDb() {
        final ArgFileManager arg = getArgFileManager();

        MyArray<SharedFile> sharedFiles = FileManagerApi.getSharedFiles(scope)
                .filter(new MyPredicate<SharedFile>() {
                    @Override
                    public boolean apply(SharedFile sharedFile) {
                        return sharedFile.folderId.equals(arg.folderId);
                    }
                });

        setListItems(sharedFiles.<UserFile>toSuper());
    }


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
        selectedItems.put(item.fileId, !(Util.nvl(selectedItems.get(item.fileId), false)));
        cleanItems(item);
        if (selectedItems.size() != 0 || actionMode != null)
            actionMode.setTitle("Выбрано " + String.valueOf(selectedItems.size()));

        adapter.notifyDataSetChanged();
    }

    //    CALLBACK for ACTION MODE
    private ActionMode.Callback callback = new ActionMode.Callback() {

        private static final int MENU_COPY = 3;
        private static final int MENU_MOVE = 4;
        private static final int MENU_REMOVE = 5;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ArgFileManager arg = getArgFileManager();
            actionMode = mode;
            mSwipeRefreshLayout.setEnabled(false);

            if (!arg.isShared) {
                menu.add(Menu.NONE, MENU_COPY, 3, R.string.f_manager_option_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.add(Menu.NONE, MENU_MOVE, 4, R.string.f_manager_option_move).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.add(Menu.NONE, MENU_REMOVE, 5, R.string.f_manager_option_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

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
                    startActivityForCopyOrMove(getArgFileManager(), selectItemIds, ArgFileManager.COPY, COPY_REQUEST_CODE);
                    return true;

                case MENU_MOVE:
                    startActivityForCopyOrMove(getArgFileManager(), selectItemIds, ArgFileManager.MOVE, MOVE_REQUEST_CODE);
                    return true;

                case MENU_REMOVE:
                    UI.dialog().title(R.string.f_m_delete_files)
                            .message(DS.getString(R.string.f_m_want_delete, String.valueOf(selectItemIds.size())))
                            .positive(R.string.yes, new Command() {
                                @Override
                                public void apply() {
                                    removeFile(getActivity(), selectItemIds);
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


    private void startActivityForCopyOrMove(ArgFileManager arg, MyArray<String> fileIds, int state, int requestCode) {
        startActivityForResult
                (newInstanceForResult(getActivity(), new ArgFileManager(
                        arg, "1", arg.fileId, "", false, state, fileIds)), requestCode);
    }

    public void copyFile(final Activity activity, MyArray<String> filesToCopy, String toFolder) {
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgFileManager(), RT.URI_COPY_FILE,
                new CopyOrMoveFile(filesToCopy, toFolder), CopyOrMoveFile.UZUM_ADAPTER))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        if (b) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_copied).show();
                            activity.setResult(RESULT_OK);
                            activity.finish();
                        } else
                            UI.alertError(activity, ErrorUtil.getErrorMessage(throwable).message.toString());

                    }
                });
    }

    public void moveFile(final Activity activity, MyArray<String> filesToMove, String toFolder) {
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgFileManager(), RT.URI_MOVE_FILE,
                new CopyOrMoveFile(filesToMove, toFolder), CopyOrMoveFile.UZUM_ADAPTER))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        if (b) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_moved).show();
                            activity.setResult(RESULT_OK);
                            activity.finish();
                        } else {
                            UI.alertError(activity, ErrorUtil.getErrorMessage(throwable).message.toString());
                        }
                    }
                });
    }

    private void removeFile(final Activity activity, MyArray<String> fileIds) {
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

    public void fileUpload(final Activity activity, final File file, final String folderId) {
        boolean fileExists = getListFilteredItems().contains(new MyPredicate<UserFile>() {
            @Override
            public boolean apply(UserFile item) {
                return item.fileName.equals(file.getName());
            }
        });

        if (fileExists) {
            UI.alert(activity, DS.getString(R.string.warning), DS.getString(R.string.f_m_duplicate_name_error));
        } else {
            jobMate.executeWithDialog(activity, new SendFileJob(getArgFileManager().getAccount(), RT.URI_PHOTO, file))
                    .done(new Promise.OnDone<String>() {
                        @Override
                        public void onDone(String s) {
                            sendFileInfo(activity, folderId, s);
                            Mold.makeSnackBar(activity, R.string.f_m_file_uploaded).show();
                        }
                    })
                    .fail(new Promise.OnFail() {
                        @Override
                        public void onFail(Throwable throwable) {
                            UI.alertError(activity, (String) ErrorUtil.getErrorMessage(throwable).message);
                        }
                    });
        }
    }

    private void sendFileInfo(final Activity activity, final String folderId, final String sha) {
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgFileManager(), RT.URI_UPLOAD_FILE, MyArray.from(folderId, sha)))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String s) {
                        Mold.makeSnackBar(activity, R.string.f_m_file_saved_to_server).show();
                        onRefresh();
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable throwable) {
                        Mold.makeSnackBar(activity, ErrorUtil.getErrorMessage(throwable).message)
                                .setAction(R.string.message_retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendFileInfo(activity, folderId, sha);
                                    }
                                }).show();
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
