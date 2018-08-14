package uz.greenwhite.smartup5_trade.m_deal.variable.photo;// 30.06.2016


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.lib.view_setup.UI;

public class VDealPhoto extends VariableLike {

    public final String sha;
    public final ValueSpinner photoType;
    public final ValueString note;
    public final String date;
    public final String latLng;

    public VDealPhoto(String sha,
                      ValueSpinner photoType,
                      ValueString note,
                      String date,
                      String latLng) {
        this.sha = sha;
        this.photoType = photoType;
        this.note = note;
        this.date = date;
        this.latLng = latLng;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(photoType, note);
    }

    public static final MyMapper<VDealPhoto, String> KEY_ADAPTER = new MyMapper<VDealPhoto, String>() {
        @Override
        public String apply(VDealPhoto photo) {
            return photo.sha;
        }
    };

    //----------------------------------------------------------------------------------------------

    public CharSequence tvDetail() {
        SpinnerOption value = photoType.getValue();
        return UI.html().v(value.name).html();
    }
}
