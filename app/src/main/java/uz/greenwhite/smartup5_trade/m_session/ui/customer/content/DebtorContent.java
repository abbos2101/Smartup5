package uz.greenwhite.smartup5_trade.m_session.ui.customer.content;


import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class DebtorContent extends CustomerContent {

    public static DebtorContent newInstance(ArgSession arg, CharSequence title) {
        return newCustomerInstance(DebtorContent.class, arg, title);
    }


    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);

        setEmptyIcon(R.drawable.empty);
        setEmptyText(R.string.session_today_empty);
    }
}
