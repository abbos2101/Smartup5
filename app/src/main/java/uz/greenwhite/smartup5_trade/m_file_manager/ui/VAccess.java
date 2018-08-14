package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.FileAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.User;

public class VAccess extends VariableLike {

    public final ValueSpinner user;
    public final ValueSpinner accessType;

    private MyArray<FileAccess> fileAccesses;
    private MyArray<User> availableUsers;
    String userId;


    VAccess(final MyArray<FileAccess> fileAccesses, MyArray<User> users, final String userId) {
        this.fileAccesses = fileAccesses;
        this.userId = userId;
        this.availableUsers = users.filter(new MyPredicate<User>() {
            @Override
            public boolean apply(final User user) {
                return !fileAccesses.contains(new MyPredicate<FileAccess>() {
                    @Override
                    public boolean apply(FileAccess fileAccess) {
                        return !fileAccess.forUserId.equals(userId) && fileAccess.forUserId.equals(user.userId);
                    }
                });
            }
        });
        this.user = makeUser();
        this.accessType = makeAccess();

    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) return error;

        if (TextUtils.isEmpty(user.getValue().code)) {
            return ErrorResult.make("User is empty!");
        }

        if (TextUtils.isEmpty(accessType.getValue().code)) {
            return ErrorResult.make("Access type not chosen!");
        }

        return ErrorResult.NONE;
    }

    public FileAccess toValue() {
        return new FileAccess(
                this.user.getValue().code,
                this.user.getValue().name.toString(),
                accessType.getValue().code,
                true);
    }

    private ValueSpinner makeUser() {
        MyArray<SpinnerOption> options = availableUsers.map(new MyMapper<User, SpinnerOption>() {
            @Override
            public SpinnerOption apply(User user) {
                return new SpinnerOption(user.userId, user.userName);
            }
        });
        FileAccess current = fileAccesses.findFirst(new MyPredicate<FileAccess>() {
            @Override
            public boolean apply(FileAccess fileAccess) {
                return userId.equals(fileAccess.forUserId);
            }
        });

        SpinnerOption option = options.get(0);
        if (current != null) {
            option = options.find(current.forUserId, SpinnerOption.KEY_ADAPTER);
        }
        return new ValueSpinner(options, option);
    }

    private ValueSpinner makeAccess() {
        SpinnerOption optionRead, optionWrite, optionEdit, optionFull;

        optionRead = new SpinnerOption(FileAccess.ACCESS_READ, DS.getString(R.string.file_access_read));
        optionWrite = new SpinnerOption(FileAccess.ACCESS_WRITE, DS.getString(R.string.file_access_add));
        optionEdit = new SpinnerOption(FileAccess.ACCESS_EDIT, DS.getString(R.string.file_access_edit));
        optionFull = new SpinnerOption(FileAccess.ACCESS_FULL, DS.getString(R.string.file_access_full));

        MyArray<SpinnerOption> options = MyArray.from(optionRead, optionWrite, optionEdit, optionFull);

        FileAccess current = fileAccesses.findFirst(new MyPredicate<FileAccess>() {
            @Override
            public boolean apply(FileAccess fileAccess) {
                return userId.equals(fileAccess.forUserId);
            }
        });

        SpinnerOption option = options.get(0);
        if (current != null && !TextUtils.isEmpty(current.accessType)) {
            option = options.find(current.accessType, SpinnerOption.KEY_ADAPTER);
        }
        return new ValueSpinner(options, option);
    }
}
