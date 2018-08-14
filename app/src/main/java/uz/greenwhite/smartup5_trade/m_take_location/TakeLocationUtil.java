package uz.greenwhite.smartup5_trade.m_take_location;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;

public class TakeLocationUtil {

    @NonNull
    public static Location getCorrectLocation(ArrayList<Location> locations) {
        if (locations.isEmpty()) throw new AppError("locations is empty");

        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location l, Location r) {
                return MyPredicate.compare((int) l.getAccuracy(), (int) r.getAccuracy());
            }
        });
        return locations.get(0);
    }
}
