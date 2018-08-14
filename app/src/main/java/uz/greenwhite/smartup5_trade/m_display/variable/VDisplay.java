package uz.greenwhite.smartup5_trade.m_display.variable;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.error.UserError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_display.bean.Display;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayBarcode;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayHolder;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayRequest;
import uz.greenwhite.smartup5_trade.m_display.row.ReviewRow;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class VDisplay extends VariableLike {

    public final DisplayHolder holder;
    private final ValueArray<VReview> reviews;
    private final MyArray<Display> displays;

    public VDisplay(DisplayHolder holder,
                    ValueArray<VReview> reviews,
                    MyArray<Display> displays) {
        this.holder = holder;
        this.reviews = reviews;
        this.displays = displays;
    }

    public void addPhotoToReview(Scope scope, String barcode, Bitmap bitmap) {
        assert scope.ref != null;
        assert scope.entry != null;

        Setting setting = scope.ref.getSettingWithDefault();
        VReview found = find(VReview.keyAdapterByBarcode(barcode));
        if (found == null) throw AppError.NullPointer();
        if (found.isNotFound()) {
            throw new AppError(DS.getString(R.string.display_you_canot_attach_photo_if_not_barcode));
        }
        String sha = scope.entry.savePhoto(scope, holder.display.entryId, bitmap, setting.common.photoConfig);
        found.photoSha.setText(sha);
    }

    public void removePhotoToReview(Scope scope, String barcode) {
        VReview found = find(VReview.keyAdapterByBarcode(barcode));
        if (found == null) throw AppError.NullPointer();
        if (found.photoSha.nonEmpty()) {
            scope.ds.db.photoUpdateStateBySha(found.photoSha.getText(), EntryState.NOT_SAVED);
            found.photoSha.setText("");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasValue() {
        return reviews.getItems().findFirst(new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview vReview) {
                return vReview.hasBarcode();
            }
        }) != null;
    }

    public DisplayBarcode toValue() {
        MyArray<DisplayRequest> items = this.reviews.getItems().filter(new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview vReview) {
                return vReview.hasBarcode() && !vReview.isNotFound();
            }
        }).map(new MyMapper<VReview, DisplayRequest>() {
            @Override
            public DisplayRequest apply(VReview v) {
                String barcode = v.barcode.getValue();
                String displayInventId = v.displayInventId.getValue();
                String photoSha = v.photoSha.getValue();
                String note = v.note.getValue();
                Integer state = v.state.getValue();
                return new DisplayRequest(barcode, displayInventId, photoSha, note, state);
            }
        });
        String entryId = holder.display.entryId;
        String filialId = holder.display.filialId;
        String outletId = holder.display.outletId;
        return new DisplayBarcode(entryId, filialId, outletId, items);
    }

    public MyArray<ReviewRow> getReviews() {
        MyArray<ReviewRow> inventReviews = reviews.getItems().filter(new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview val) {
                return val.displayInventId.nonEmpty() && !val.isNew();
            }
        }).map(new MyMapper<VReview, ReviewRow>() {
            @Override
            public ReviewRow apply(VReview val) {
                String inventId = val.displayInventId.getText();
                Display found = displays.find(inventId, Display.KEY_ADAPTER);
                if (found == null) {
                    throw new AppError(DS.getString(R.string.display_outlet_invent_not_found, inventId));
                }
                String barcode = val.barcode.getValue();
                boolean photo = val.photoSha.nonEmpty();
                boolean note = val.note.nonEmpty();
                int state = val.state.getValue();
                return new ReviewRow(found.shortName, found.code, found.photoSha, found.inventNumber,
                        barcode, inventId, photo, note, state, val);
            }
        }).sort(new Comparator<ReviewRow>() {
            @Override
            public int compare(ReviewRow l, ReviewRow r) {
                int compare = CharSequenceUtil.compareToIgnoreCase(l.displayName, r.displayName);
                if (compare == 0) {
                    compare = CharSequenceUtil.compareToIgnoreCase(l.displayCode, r.displayCode);
                    if (compare == 0) {
                        compare = CharSequenceUtil.compareToIgnoreCase(l.barcode, r.barcode);
                    }
                }
                return compare;
            }
        });

        MyArray<ReviewRow> barcodeReview = reviews.getItems().filter(new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview val) {
                return val.hasBarcode() && val.isNew();
            }
        }).map(new MyMapper<VReview, ReviewRow>() {
            @Override
            public ReviewRow apply(VReview val) {
                String barcode = val.barcode.getValue();
                boolean photo = val.photoSha.nonEmpty();
                boolean note = val.note.nonEmpty();
                int state = val.state.getValue();
                return new ReviewRow("", "", "", "", barcode, "", photo, note, state, val);
            }
        }).sort(new Comparator<ReviewRow>() {
            @Override
            public int compare(ReviewRow l, ReviewRow r) {
                return CharSequenceUtil.compareToIgnoreCase(l.barcode, r.barcode);
            }
        });

        if (barcodeReview.nonEmpty()) {
            barcodeReview.get(0).firstItem = true;
            barcodeReview.get(barcodeReview.size() - 1).last = true;
        } else if (inventReviews.nonEmpty()) {
            inventReviews.get(inventReviews.size() - 1).last = true;
        }
        if (inventReviews.nonEmpty()) inventReviews.get(0).firstItem = true;

        return inventReviews.append(barcodeReview);
    }

    @Nullable
    private VReview find(MyPredicate<VReview> predicate) {
        MyArray<VReview> found = reviews.getItems().filter(predicate);
        if (found.isEmpty()) {
            return null;
        }
        if (found.size() > 1) {
            throw new AppError(DS.getString(R.string.display_barcode_invent_duplicate, String.valueOf(found.size())));
        }
        return found.get(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void removeReview(String barcode) {
        if (TextUtils.isEmpty(barcode)) throw AppError.NullPointer();

        VReview found = find(VReview.keyAdapterByBarcode(barcode));
        if (found == null) {
            throw new AppError(DS.getString(R.string.display_remove_review_not_found, barcode));
        }

        if (!found.isNotFound()) {
            if (found.isNew()) {
                reviews.delete(found);

            } else if (found.isLinked() || found.isFound()) {
                if (found.isLinked()) found.barcode.setText("");
                found.photoSha.setText("");
                found.note.setText("");
                found.setState(DisplayRequest.NOT_FOUND);

            } else {
                throw AppError.Unsupported();
            }
        }
    }

    public void appendNewReview(String barcode) {
        VReview vReview = find(VReview.keyAdapterByBarcode(barcode));
        if (vReview != null && !vReview.isNotFound()) {
            throw new UserError(DS.getString(R.string.display_barcode_exists, barcode));
        } else if (vReview != null && vReview.isNotFound()) {
            vReview.setState(DisplayRequest.FOUND);
        } else {
            reviews.append(new VReview(barcode));
        }
    }

    public void foundElseLinkReview(String barcode, String displayInventId) {
        VReview barcodeReview = find(VReview.keyAdapterByBarcode(barcode));
        VReview inventoryReview = find(VReview.keyAdapterByInventId(displayInventId));

        if (inventoryReview == null) {
            throw new AppError(DS.getString(R.string.outlet_display_not_found));
        }

        if (!barcode.equals(inventoryReview.barcode.getText())) {
            throw new UserError(DS.getString(R.string.display_barcode_is_not_true));
        }

        if (barcodeReview == null || barcodeReview.isNotFound()) {
            appendNewReview(barcode);
            barcodeReview = find(VReview.keyAdapterByBarcode(barcode));
            assert barcodeReview != null;
            if (barcodeReview.isFound()) {
                return;
            }
        }
        linkReview(barcode, displayInventId);
    }

    public void linkReview(String barcode, final String displayInventId) {
        VReview barcodeReview = find(VReview.keyAdapterByBarcode(barcode));
        VReview inventoryReview = find(VReview.keyAdapterByInventId(displayInventId));

        if (barcodeReview == null) {
            appendNewReview(barcode);
            barcodeReview = find(VReview.keyAdapterByBarcode(barcode));
        }

        assert barcodeReview != null;
        if (barcodeReview.displayInventId.nonEmpty()) {
            String mBarcode = barcodeReview.barcode.getValue();
            String mInventId = barcodeReview.displayInventId.getText();
            Integer mState = barcodeReview.state.getValue();
            throw new AppError(DS.getString(R.string.display_barcode_has_invent, mBarcode, mInventId, mState));
        }

        assert inventoryReview != null;
        if (inventoryReview.hasBarcode()) {
            String mBarcode = inventoryReview.barcode.getValue();
            String mInventId = inventoryReview.displayInventId.getText();
            String mState = inventoryReview.state.getText();
            throw new AppError(DS.getString(R.string.display_inventory_has_barcode, mBarcode, mInventId, mState));
        }

        reviews.delete(barcodeReview);
        reviews.delete(inventoryReview);

        String photoSha = barcodeReview.photoSha.getText();
        String note = barcodeReview.note.getText();
        reviews.append(new VReview(barcode, displayInventId, photoSha, note, DisplayRequest.LINKED));
    }

    public void unlink(String barcode) {
        VReview found = find(VReview.keyAdapterByBarcode(barcode));
        if (found == null) {
            throw new AppError(DS.getString(R.string.display_review_not_found, barcode));
        }

        if (!found.isLinked()) {
            throw new AppError(DS.getString(R.string.display_you_canot_unlink_not_linked));
        }

        reviews.delete(found);
        reviews.append(new VReview(barcode, "", found.photoSha.getText(), found.note.getText(), DisplayRequest.NEW));
        reviews.append(new VReview("", found.displayInventId.getText(), "", "", DisplayRequest.NOT_FOUND));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected MyArray<Variable> gatherVariables() {
        return reviews.getItems().toSuper();
    }
}
