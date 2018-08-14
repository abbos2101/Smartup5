package uz.greenwhite.smartup5_trade.common;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;

import static android.inputmethodservice.KeyboardView.OnKeyboardActionListener;

public class CustomNumberKeyboard {

    public static void init(Activity activity, ViewSetup viewSetup) {
        new CustomNumberKeyboard(activity, viewSetup);
    }

    public static void prepare(EditText et) {
        et.setInputType(InputType.TYPE_NULL);
        et.setOnFocusChangeListener(ON_FOCUS_NUMBER_EDIT_TEXT);
        et.setGravity(Gravity.RIGHT);
    }

    private final Activity mHostActivity;
    private final ViewSetup mViewSetup;
    private final KeyboardView mKeyboardView;

    public CustomNumberKeyboard(Activity activity, ViewSetup viewSetup) {
        this.mHostActivity = activity;
        this.mViewSetup = viewSetup;
        this.mKeyboardView = this.mViewSetup.id(R.id.keyboardview);
        Keyboard keyboard = new Keyboard(activity, R.xml.number_keyboard);
        mKeyboardView.setKeyboard(keyboard);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(okal);
        // Hide the standard keyboard initially
        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        mHostActivity.getWindow().setSoftInputMode(mode);
    }

    private final OnKeyboardActionListener okal = new OnKeyboardActionListener() {

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            View currentFocus = mHostActivity.getWindow().getCurrentFocus();
            if (currentFocus == null || !(currentFocus instanceof EditText)) {
                return;
            }

            EditText et = (EditText) currentFocus;
            KeyEvent evt = new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode);
            et.dispatchKeyEvent(evt);
        }

        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };


    private static OnFocusNumberEditText ON_FOCUS_NUMBER_EDIT_TEXT = new OnFocusNumberEditText();

    private static class OnFocusNumberEditText implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && v instanceof EditText) {
                EditText et = (EditText) v;
                ((EditText) v).setSelection(et.getText().length());
            }
        }
    }

}
