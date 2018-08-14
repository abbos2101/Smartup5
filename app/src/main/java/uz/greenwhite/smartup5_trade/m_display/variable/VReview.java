package uz.greenwhite.smartup5_trade.m_display.variable;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueInteger;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayRequest;

public class VReview extends VariableLike {

    final ValueString barcode;
    final ValueInteger state;
    final ValueString displayInventId;
    public ValueString photoSha, note;

    public VReview(String barcode, String displayInventId, String photoSha, String note, int state) {
        this.barcode = new ValueString(30, barcode);
        this.displayInventId = new ValueString(9, Util.nvl(displayInventId));
        this.photoSha = new ValueString(64, Util.nvl(photoSha));
        this.note = new ValueString(500, Util.nvl(note));
        this.state = new ValueInteger(10, state);

        if (!DisplayRequest.getStates().contains(state, MyMapper.<Integer>identity())) {
            throw new AppError(String.format("wrong state %s", String.valueOf(state)));
        }
    }

    public VReview(String barcode) {
        this(barcode, null, null, null, DisplayRequest.NEW);
    }

    //----------------------------------------------------------------------------------------------

    public boolean hasBarcode() {
        return barcode.nonEmpty();
    }

    public String getBarcode() {
        return barcode.getValue();
    }

    //----------------------------------------------------------------------------------------------

    public void setState(int state) {
        if (!DisplayRequest.getStates().contains(state, MyMapper.<Integer>identity())) {
            throw new AppError(String.format("wrong state %s", String.valueOf(state)));
        }
        this.state.setValue(state);
    }

    public boolean isNew() {
        return this.state.getValue() == DisplayRequest.NEW;
    }

    public boolean isFound() {
        return this.state.getValue() == DisplayRequest.FOUND;
    }

    public boolean isLinked() {
        return this.state.getValue() == DisplayRequest.LINKED;
    }

    public boolean isNotFound() {
        return this.state.getValue() == DisplayRequest.NOT_FOUND;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(barcode, displayInventId, photoSha, note).toSuper();
    }

    public static MyPredicate<VReview> keyAdapterByBarcode(final String barcode) {
        if (TextUtils.isEmpty(barcode)) throw AppError.NullPointer();
        return new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview vReview) {
                return barcode.equals(vReview.barcode.getText());
            }
        };
    }

    public static MyPredicate<VReview> keyAdapterByInventId(final String displayInventId) {
        if (TextUtils.isEmpty(displayInventId)) throw AppError.NullPointer();
        return new MyPredicate<VReview>() {
            @Override
            public boolean apply(VReview vReview) {
                return displayInventId.equals(vReview.displayInventId.getText());
            }
        };
    }
}
