package uz.greenwhite.smartup5_trade.m_display.builder;

import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_display.bean.Display;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayBarcode;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayHolder;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayRequest;
import uz.greenwhite.smartup5_trade.m_display.bean.PersonDisplay;
import uz.greenwhite.smartup5_trade.m_display.variable.VDisplay;
import uz.greenwhite.smartup5_trade.m_display.variable.VReview;

public class BuilderDisplay {

    public static String stringify(VDisplay vDisplay) {
        DisplayBarcode value = vDisplay.toValue();
        DisplayHolder holder = new DisplayHolder(value, vDisplay.holder.entryState);
        return Uzum.toJson(holder, DisplayHolder.UZUM_ADAPTER);
    }

    public static VDisplay make(Scope scope, DisplayHolder holder) {
        assert scope.ref != null;
        PersonDisplay personDisplay = scope.ref.getPersonDisplay()
                .find(holder.display.outletId, PersonDisplay.KEY_ADAPTER);
        MyArray<Display> displays = personDisplay == null ? MyArray.<Display>emptyArray() : personDisplay.displays;
        ValueArray<VReview> vReviews = makeReviews(displays, holder);
        return new VDisplay(holder, vReviews, displays);
    }

    private static ValueArray<VReview> makeReviews(MyArray<Display> displays, DisplayHolder holder) {
        final Set<String> inventId = new HashSet<>();
        MyArray<VReview> result = holder.display.displayBarcode.map(new MyMapper<DisplayRequest, VReview>() {
            @Override
            public VReview apply(DisplayRequest val) {
                inventId.add(val.displayInventId);
                return new VReview(val.barcode, val.displayInventId, val.photoSha, val.note, val.state);
            }
        });

        MyArray<VReview> displayReviews = displays.filter(new MyPredicate<Display>() {
            @Override
            public boolean apply(Display val) {
                return !inventId.contains(val.displayInventId);
            }
        }).map(new MyMapper<Display, VReview>() {
            @Override
            public VReview apply(Display val) {
                return new VReview(val.barcode, val.displayInventId, "", "", DisplayRequest.NOT_FOUND);
            }
        });

        return new ValueArray<>(result.append(displayReviews));
    }
}
