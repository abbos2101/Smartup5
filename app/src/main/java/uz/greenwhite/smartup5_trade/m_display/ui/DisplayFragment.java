package uz.greenwhite.smartup5_trade.m_display.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.BarcodeUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_display.ArgDisplayReview;
import uz.greenwhite.smartup5_trade.m_display.DisplayApi;
import uz.greenwhite.smartup5_trade.m_display.DisplayUtil;
import uz.greenwhite.smartup5_trade.m_display.row.ReviewRow;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;


public class DisplayFragment extends MoldContentRecyclerFragment<ReviewRow> {

    public static void open(ArgOutlet arg) {
        Mold.openContent(DisplayFragment.class, Mold.parcelableArgument(arg, ArgOutlet.UZUM_ADAPTER));
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.outlet_display_barcode);

        ArgOutlet arg = getArgOutlet();
        DisplayData data = Mold.getData(getActivity());
        if (data == null) {
            data = new DisplayData(arg.getScope(), arg.outletId);
            Mold.setData(getActivity(), data);
        }

        if (data.hasEdit()) {
            addMenu(R.drawable.ic_filter_center_focus_black_24dp, R.string.outlet_barcode_scanner, new Command() {
                @Override
                public void apply() {
                    startActivityForResult(BarcodeUtil.barcodeIntent(getActivity()), BarcodeUtil.BARCODE_REQUEST);
                }
            });
            if (data.vDisplay.holder.entryState.isSaved()) {
                addSubMenu(DS.getString(R.string.remove), new Command() {
                    @Override
                    public void apply() {
                        removeDisplay();
                    }
                });
            }

            setHasLongClick(true);

            ViewSetup vs = new ViewSetup(getActivity(), R.layout.display_footer);
            vs.id(R.id.ll_save_or_ready).setVisibility(View.VISIBLE);
            vs.id(R.id.btn_edit).setVisibility(View.GONE);
            vs.id(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save(false);
                }
            });
            vs.id(R.id.btn_ready).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save(true);
                }
            });
            BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vs.view);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else if (data.vDisplay.holder.entryState.isReady()) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.display_footer);
            vs.id(R.id.ll_save_or_ready).setVisibility(View.GONE);
            vs.id(R.id.btn_edit).setVisibility(View.VISIBLE);
            vs.id(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeEdit();
                }
            });
            BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vs.view);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if (!TextUtils.isEmpty(data.vDisplay.holder.entryState.serverResult)) {
            CharSequence error = data.vDisplay.holder.entryState.getErrorText();
            ViewSetup vs = setHeader(R.layout.display_header);
            vs.textView(R.id.tv_error).setText(error);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        try {
            DisplayData data = Mold.getData(getActivity());
            setListItems(data.vDisplay.getReviews());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void itemLongClick(final ReviewRow reviewRow) {
        DisplayData data = Mold.getData(getActivity());
        if (!data.hasEdit()) {
            return;
        }

        if (reviewRow.item.isNew() || reviewRow.item.isNotFound()) {
            if (reviewRow.item.isNew()) {
                UI.dialog().option(R.string.outlet_attach_display, new Command() {
                    @Override
                    public void apply() {
                        DisplayUtil.link(DisplayFragment.this, getListItems(), reviewRow);
                    }
                }).option(R.string.outlet_display_remove_barcode, new Command() {
                    @Override
                    public void apply() {
                        DisplayUtil.remove(DisplayFragment.this, reviewRow);
                    }
                }).show(getActivity());
            } else if (!reviewRow.item.hasBarcode()) {
                DisplayUtil.link(DisplayFragment.this, getListItems(), reviewRow);
            }
        } else if (reviewRow.item.isLinked() || reviewRow.item.isFound()) {
            if (reviewRow.item.isLinked()) {
                UI.dialog().option(R.string.outlet_undock_display_barcode, new Command() {
                    @Override
                    public void apply() {
                        DisplayUtil.unlink(DisplayFragment.this, reviewRow);
                    }
                }).option(R.string.outlet_display_remove_barcode, new Command() {
                    @Override
                    public void apply() {
                        DisplayUtil.remove(DisplayFragment.this, reviewRow);
                    }
                }).show(getActivity());
            } else {
                DisplayUtil.unlink(DisplayFragment.this, reviewRow);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void save(boolean ready) {
        try {
            DisplayData data = Mold.getData(getActivity());
            if (data.vDisplay.hasValue()) {
                ErrorResult error = data.vDisplay.getError();
                if (error.isError()) {
                    UI.alertError(getActivity(), error.getErrorMessage());
                    return;
                }
                DisplayApi.saveDisplay(getArgOutlet(), data, ready);
            }
            getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    private void removeDisplay() {
        try {
            DisplayApi.displayDelete(getArgOutlet(), (DisplayData) Mold.getData(getActivity()));
            DisplayFragment.open(getArgOutlet());
            getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    private void makeEdit() {
        try {
            DisplayApi.displayMakeEdit(getArgOutlet(), (DisplayData) Mold.getData(getActivity()));
            DisplayFragment.open(getArgOutlet());
            getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(),true);

        final String barcode = BarcodeUtil.getBarcodeInActivityResult(getActivity(), requestCode, resultCode, intent);
        if (TextUtils.isEmpty(barcode)) return;

        try {
            DisplayData data = Mold.getData(getActivity());
            data.vDisplay.appendNewReview(barcode);
            reloadContent();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);

        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.display_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final ReviewRow item) {
        vsItem.id(R.id.v_bottom).setVisibility(item.last ? View.VISIBLE : View.GONE);
        vsItem.id(R.id.ll_display_header).setVisibility(item.firstItem ? View.VISIBLE : View.GONE);

        vsItem.textView(R.id.tv_header_name).setText(item.getHeaderTextResId());
        vsItem.textView(R.id.tv_title).setText(item.getTitle());
        vsItem.textView(R.id.tv_detail).setText(item.getDetail());
        vsItem.imageView(R.id.display_info).setImageDrawable(item.getIconState());

        vsItem.id(R.id.ll_display_photo_note).setVisibility(item.photo || item.note ? View.VISIBLE : View.GONE);
        vsItem.id(R.id.iv_inf_photo).setVisibility(item.photo ? View.VISIBLE : View.GONE);
        vsItem.id(R.id.iv_inf_note).setVisibility(item.note ? View.VISIBLE : View.GONE);

        ErrorResult error = item.item.getError();
        TextView tvError = vsItem.textView(R.id.tv_error);
        if (error.isError()) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(ErrorUtil.getErrorMessage(error.getErrorMessage()).message);
        } else {
            tvError.setVisibility(View.GONE);
        }

        vsItem.id(R.id.ll_display_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayReviewFragment content = DisplayReviewFragment.newInstance(
                        new ArgDisplayReview(getArgOutlet(), item.displayInventId, item.barcode));
                Mold.addContent(getActivity(), content);
            }
        });

        vsItem.id(R.id.ll_display_row).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClick(item);
                return true;
            }
        });

        vsItem.id(R.id.iv_display_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mold.makeSnackBar(getActivity(), R.string.outlet_display_photo_download).show();
                requestImage(vsItem, item);
            }
        });

        requestImage(vsItem, item);

    }

    private void requestImage(final ViewSetup vsItem, final ReviewRow item) {
        if (!TextUtils.isEmpty(item.displayPhoto)) {
            jobMate.execute(new FetchImageJob(getArgOutlet().accountId, item.displayPhoto))
                    .always(new Promise.OnAlways<Bitmap>() {
                        @Override
                        public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                            if (resolved) {
                                if (result != null) {
                                    vsItem.imageView(R.id.iv_display_image).setImageBitmap(result);
                                } else {
                                    vsItem.imageView(R.id.iv_display_image).setImageResource(R.drawable.display_photo);
                                }
                            } else {
                                vsItem.imageView(R.id.iv_display_image).setImageResource(R.drawable.display_photo);
                                if (error != null) error.printStackTrace();
                            }
                        }
                    });
        }
    }
}
