package uz.greenwhite.smartup5_trade.m_session.ui.customer.content;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class DeliveryContent extends CustomerContent {

    public static DeliveryContent newInstance(ArgSession arg, CharSequence title) {
        return newCustomerInstance(DeliveryContent.class, arg, title);
    }

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);


        setEmptyIcon(R.drawable.empty);
        setEmptyText(R.string.session_today_empty);
    }
}
