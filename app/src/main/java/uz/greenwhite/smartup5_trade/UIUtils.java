package uz.greenwhite.smartup5_trade;// 01.11.2016

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyRecyclerAdapter;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.view_setup.BottomSheet;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.common.AnimatorListenerAdapterProxy;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.common.MyImageView;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;
import uz.greenwhite.smartup5_trade.m_near.ui.NearOutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.row.Customer;

public class UIUtils {


    public static void showErrorText(TextView cError, ErrorResult errorResult) {
        if (errorResult.isError()) {
            cError.setText(errorResult.getErrorMessage());
            cError.setVisibility(View.VISIBLE);
        } else {
            cError.setText("");
            cError.setVisibility(View.GONE);
        }
    }

    public static void editTextTouchListener(final EditText et, final Command command) {
        et.setOnLongClickListener(null);
        et.setKeyListener(null);
        et.setFocusable(false);
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (command != null) command.apply();
                }
                return false;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean showAlertIsEmpty(Activity activity, MyArray adapter) {
        if (adapter == null || (adapter != null && adapter.isEmpty())) {
            UI.alertError(activity, DS.getString(R.string.near_outlet_is_empty));
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static void openNearOutletIndexFragment(Activity activity,
                                                   ArgSession arg,
                                                   Location location,
                                                   MyArray<String> outletIds) {
        try {
            AppError.checkNull(activity, arg);
            if (location != null && !showAlertIsEmpty(activity, outletIds)) {
                ArgNearOutlet argNear = ArgNearOutlet.create(arg, location, outletIds);
                NearOutletIndexFragment.open(argNear);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e, arg.accountId);
            UI.alertError(activity, (String) ErrorUtil.getErrorMessage(e).message);
        }
    }

    public static void showRoomDialog(final FragmentActivity activity,
                                      final MyArray<Room> rooms,
                                      final MyCommand<Room> command) {
        if (rooms.isEmpty()) {
            Mold.makeSnackBar(activity, R.string.exists_filial_room_is_empty).show();
            return;
        }
        if (rooms.size() == 1) {
            command.apply(rooms.get(0));
        } else {
            UI.bottomSheet()
                    .title(R.string.select_room)
                    .option(rooms, new BottomSheet.CommandFacade<Room>() {

                        @Override
                        public Object getIcon(Room val) {
                            return R.drawable.ic_room_black_24dp;
                        }

                        @Override
                        public CharSequence getName(Room val) {
                            return val.name;
                        }

                        @Override
                        public void apply(Room room) {
                            command.apply(room);
                        }
                    }).show(activity);
        }
    }

    public static void openMap(Activity activity, Outlet outlet, String location) {
        String query = location + "(" + Uri.encode(outlet.name) + ")";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + query);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public static void openMap(Activity activity, Outlet outlet, OutletLocation location) {
        openMap(activity, outlet, location != null ? location.location : outlet.latLng);
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (num == 6) {
            view.setTranslationX(0);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "translationX", x));
        animatorSet.setDuration(50);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shakeView(view, num == 5 ? 0 : -x, num + 1);
            }
        });
        animatorSet.start();
    }

    public static ViewSetup makeEditText(MoldPageContent content, @DrawableRes int iconResId, @StringRes int resId, final ValueString value, int inputType) {
        return makeEditText(content, iconResId, DS.getString(resId), value, inputType);
    }

    public static ViewSetup makeEditText(MoldPageContent content, @StringRes int resId, final ValueString value, int inputType) {
        return makeEditText(content, 0, DS.getString(resId), value, inputType);
    }

    public static ViewSetup makeEditText(MoldPageContent content, @DrawableRes int iconResId, @StringRes int resId, final ValueString value) {
        return makeEditText(content, iconResId, DS.getString(resId), value, InputType.TYPE_CLASS_TEXT);
    }

    public static ViewSetup makeEditText(MoldPageContent content, @StringRes int resId, final ValueString value) {
        return makeEditText(content, 0, DS.getString(resId), value, InputType.TYPE_CLASS_TEXT);
    }

    public static ViewSetup makeEditText(MoldPageContent content, @DrawableRes int iconResId, CharSequence title, final ValueString value) {
        return makeEditText(content, iconResId, title, value, InputType.TYPE_CLASS_TEXT);
    }

    public static ViewSetup makeEditText(MoldPageContent content, CharSequence title, final ValueString value) {
        return makeEditText(content, 0, title, value, InputType.TYPE_CLASS_TEXT);
    }

    public static ViewSetup makeEditText(MoldPageContent content, @DrawableRes int iconResId, CharSequence title, final ValueString value, int inputType) {
        ViewSetup vs = new ViewSetup(content.getActivity(), R.layout.oedit_edittext);

        if (iconResId == 0) {
            vs.imageView(R.id.miv_icon).setVisibility(View.GONE);
        } else {
            vs.imageView(R.id.miv_icon).setVisibility(View.VISIBLE);
            ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(iconResId, R.color.default_icon);
        }

        EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        cValue.setInputType(inputType);
        final TextView cError = vs.id(R.id.error);
        UI.bind(cValue, value);
        UI.getModel(cValue).add(new ModelChange() {
            @Override
            public void onChange() {
                UIUtils.showErrorText(cError, value.getError());
            }
        });
        return vs;
    }
}
