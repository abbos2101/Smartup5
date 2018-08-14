package uz.greenwhite.smartup5_trade.m_session.ui.customer.content;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ReturnContent extends CustomerContent {

    public static ReturnContent newInstance(ArgSession arg, CharSequence title) {
        return newCustomerInstance(ReturnContent.class, arg, title);
    }


}