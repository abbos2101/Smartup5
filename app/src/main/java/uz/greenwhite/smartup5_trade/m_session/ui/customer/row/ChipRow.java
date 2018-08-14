package uz.greenwhite.smartup5_trade.m_session.ui.customer.row;

import uz.greenwhite.lib.Command;

public class ChipRow {

    public final CharSequence title;
    public final Command command;
    public final Object tag;

    public ChipRow(CharSequence title, Command command, Object tag) {
        this.title = title;
        this.command = command;
        this.tag = tag;
    }
}
