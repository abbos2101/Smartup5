package uz.greenwhite.smartup5_trade.common;

import android.location.Location;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uz.greenwhite.lib.collection.MyPredicate;

public class CommonUtil {

    @Nullable
    public static Location getCorrectLocation(ArrayList<Location> locations) {
        if (locations.isEmpty()) return null;
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location l, Location r) {
                return MyPredicate.compare((int) l.getAccuracy(), (int) r.getAccuracy());
            }
        });
        return locations.get(0);
    }
}
