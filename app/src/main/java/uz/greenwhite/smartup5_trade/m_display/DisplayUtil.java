package uz.greenwhite.smartup5_trade.m_display;

import android.text.TextUtils;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_display.row.ReviewRow;
import uz.greenwhite.smartup5_trade.m_display.ui.DisplayData;

public class DisplayUtil {

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void link(final MoldContentFragment content, MyArray<ReviewRow> items, final ReviewRow item) {
        final MyArray<ReviewRow> filter = items.filter(new MyPredicate<ReviewRow>() {
            @Override
            public boolean apply(ReviewRow val) {
                return item.item.isNew() ? !val.item.isNew() && TextUtils.isEmpty(val.barcode)
                        : val.item.isNew() && TextUtils.isEmpty(val.displayInventId);
            }
        });

        if (filter.isEmpty()) {
            UI.dialog()
                    .title(R.string.warning)
                    .message(item.item.isNew() ?
                            R.string.outlet_display_missing : R.string.outlet_barcode_missing)
                    .show(content.getActivity());
            return;
        }

        UI.dialog()
                .title(item.item.isNew() ?
                        R.string.outlet_attach_display : R.string.outlet_attach_barcode)
                .option(filter, new DialogBuilder.CommandFacade<ReviewRow>() {
                    @Override
                    public CharSequence getName(ReviewRow val) {
                        return UI.html().v(item.item.isNew()
                                ? val.displayName + "<br/>" + DS.getString(R.string.outlet_code, val.displayCode)
                                : val.barcode).html();
                    }

                    @Override
                    public void apply(ReviewRow val) {
                        try {
                            String barcode = item.item.isNew() ? item.barcode : val.barcode;
                            String inventId = item.item.isNew() ? val.displayInventId : item.displayInventId;
                            DisplayData data = Mold.getData(content.getActivity());
                            data.vDisplay.linkReview(barcode, inventId);
                            content.reloadContent();
                        } catch (Exception e) {
                            ErrorUtil.saveThrowable(e);
                            UI.alertError(content.getActivity(), e);
                        }
                    }
                })
                .show(content.getActivity());
    }

    public static void unlink(final MoldContentFragment content, final ReviewRow item) {
        UI.dialog().title(R.string.outlet_undock_display_barcode)
                .message(UI.html().v(item.displayName).br()
                        .v(DS.getString(R.string.outlet_code, item.displayCode)).br()
                        .v(item.barcode).html())
                .positive(new Command() {
                    @Override
                    public void apply() {
                        try {
                            DisplayData data = Mold.getData(content.getActivity());
                            data.vDisplay.unlink(item.barcode);
                            content.reloadContent();
                        } catch (Exception e) {
                            ErrorUtil.saveThrowable(e);
                            UI.alertError(content.getActivity(), e);
                        }
                    }
                }).show(content.getActivity());
    }

    public static void remove(final MoldContentFragment content, final ReviewRow item) {
        UI.dialog().title(R.string.warning)
                .message(R.string.outlet_do_you_want_to_delete)
                .positive(new Command() {
                    @Override
                    public void apply() {
                        try {
                            DisplayData data = Mold.getData(content.getActivity());
                            data.vDisplay.removeReview(item.barcode);
                            content.reloadContent();
                        } catch (Exception e) {
                            ErrorUtil.saveThrowable(e);
                            UI.alertError(content.getActivity(), e);
                        }
                    }
                }).show(content.getActivity());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
